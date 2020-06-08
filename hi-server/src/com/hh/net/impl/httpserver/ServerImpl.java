/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.hh.net.impl.httpserver;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.hh.net.httpserver.HttpHandler;
import com.hh.net.httpserver.HttpsConfigurator;
import com.hh.net.httpserver.HttpContext;
import com.hh.net.httpserver.HttpServer;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.hh.net.impl.httpserver.HttpConnection.State;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;

/**
 * Provides implementation for both HTTP and HTTPS
 */
public class ServerImpl implements TimeSource {

    public String protocol;
    public boolean https;
    public Executor executor;
    public HttpsConfigurator httpsConfig;
    public SSLContext sslContext;
    public ContextList contexts;
    public InetSocketAddress address;
    public ServerSocketChannel schan;
    public Selector selector;
    public SelectionKey listenerKey;
    public Set<HttpConnection> idleConnections;
    public Set<HttpConnection> allConnections;
    /* following two are used to keep track of the times
     * when a connection/request is first received
     * and when we start to send the response
     */
    public Set<HttpConnection> reqConnections;
    public Set<HttpConnection> rspConnections;
    public List<Event> events;
    public Object lolock = new Object();
    public volatile boolean finished = false;
    public volatile boolean terminating = false;
    public boolean bound = false;
    public boolean started = false;
    public volatile long time;  /* current time */
    public volatile long subticks = 0;
    public volatile long ticks; /* number of clock ticks since server started */
    public HttpServer wrapper;
    //private Parameters pa = new Parameters();

    final static int CLOCK_TICK = ServerConfig.getClockTick();
    final static long IDLE_INTERVAL = ServerConfig.getIdleInterval();
    final static int MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
    final static long TIMER_MILLIS = ServerConfig.getTimerMillis ();
    final static long MAX_REQ_TIME=getTimeMillis(ServerConfig.getMaxReqTime());
    final static long MAX_RSP_TIME=getTimeMillis(ServerConfig.getMaxRspTime());
    final static boolean timer1Enabled = MAX_REQ_TIME != -1 || MAX_RSP_TIME != -1;

    public Timer timer, timer1;
    public Logger logger;
    
    ServerImpl (
        HttpServer wrapper, String protocol, InetSocketAddress addr, int backlog
    ) throws IOException {
        this.protocol = protocol;
        this.wrapper = wrapper;
        this.logger = Logger.getLogger ("com.hh.net.httpserver");
        ServerConfig.checkLegacyProperties (logger);
        https = protocol.equalsIgnoreCase ("https");
        this.address = addr;
        contexts = new ContextList();
        schan = ServerSocketChannel.open();
        if (addr != null) {
            ServerSocket socket = schan.socket();
            socket.bind (addr, backlog);
            bound = true;
        }
        selector = Selector.open ();
        schan.configureBlocking (false);
        listenerKey = schan.register (selector, SelectionKey.OP_ACCEPT);
        idleConnections = Collections.synchronizedSet (new HashSet<HttpConnection>());
        allConnections = Collections.synchronizedSet (new HashSet<HttpConnection>());
        reqConnections = Collections.synchronizedSet (new HashSet<HttpConnection>());
        rspConnections = Collections.synchronizedSet (new HashSet<HttpConnection>());
        time = System.currentTimeMillis();
        timer = new Timer ("server-timer", true);
        timer.schedule (new ServerTimerTask(), CLOCK_TICK, CLOCK_TICK);
        //pa.setData();
        if (timer1Enabled) {
            timer1 = new Timer ("server-timer1", true);
            timer1.schedule (new ServerTimerTask1(),MAX_REQ_TIME,MAX_REQ_TIME);
            logger.config ("HttpServer timer1 enabled period in ms:  " + MAX_REQ_TIME);
            logger.config ("MAX_REQ_TIME:  "+MAX_REQ_TIME);
            logger.config ("MAX_RSP_TIME:  "+MAX_RSP_TIME);
        }
        events = new LinkedList<Event>();
        logger.config ("HttpServer created "+protocol+" "+ addr);
    }
    
    public void bind (InetSocketAddress addr, int backlog) throws IOException {
        if (bound) {
            throw new BindException ("HttpServer already bound");
        }
        if (addr == null) {
            throw new NullPointerException ("null address");
        }
        ServerSocket socket = schan.socket();
        socket.bind (addr, backlog);
        bound = true;
    }

    public void start () {
        if (!bound || started || finished) {
            throw new IllegalStateException ("server in wrong state");
        }
        if (executor == null) {
            executor = new DefaultExecutor();
        }
        
        try {
            ActorRef actor = com.hh.server.HHServer.system.actorOf(
                    Props.create(Dispatcher.class, this).withDispatcher("hh-dispatcher"));
            actor.tell("execute", actor);
            actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
        } catch(Exception ex) {
            logger.log(Level.FINER, "ServerImpl.Exchange (1)", ex);
        }
    }

    public void setExecutor (Executor executor) {
        if (started) {
            throw new IllegalStateException ("server already started");
        }
        this.executor = executor;
    }

    private static class DefaultExecutor implements Executor {
        public void execute (Runnable task) {
            task.run();
        }
    }

    public Executor getExecutor () {
        return executor;
    }

    public void setHttpsConfigurator (HttpsConfigurator config) {
        if (config == null) {
            throw new NullPointerException ("null HttpsConfigurator");
        }
        if (started) {
            throw new IllegalStateException ("server already started");
        }
        this.httpsConfig = config;
        sslContext = config.getSSLContext();
    }

    public HttpsConfigurator getHttpsConfigurator () {
        return httpsConfig;
    }

    public void stop (int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException ("negative delay parameter");
        }
        terminating = true;
        try { schan.close(); } catch (IOException e) {}
        selector.wakeup();
        long latest = System.currentTimeMillis() + delay * 1000;
        while (System.currentTimeMillis() < latest) {
            delay();
            if (finished) {
                break;
            }
        }
        finished = true;
        selector.wakeup();
        synchronized (allConnections) {
            for (HttpConnection c : allConnections) {
                c.close();
            }
        }
        allConnections.clear();
        idleConnections.clear();
        timer.cancel();
        if (timer1Enabled) {
            timer1.cancel();
        }
    }

    public synchronized HttpContextImpl createContext (String path, HttpHandler handler) {
        if (handler == null || path == null) {
            throw new NullPointerException ("null handler, or path parameter");
        }
        HttpContextImpl context = new HttpContextImpl (protocol, path, handler, this);
        contexts.add (context);
        logger.config ("context created: " + path);
        return context;
    }

    public synchronized HttpContextImpl createContext (String path) {
        if (path == null) {
            throw new NullPointerException ("null path parameter");
        }
        HttpContextImpl context = new HttpContextImpl (protocol, path, null, this);
        contexts.add (context);
        logger.config ("context created: " + path);
        return context;
    }

    public synchronized void removeContext (String path) throws IllegalArgumentException {
        if (path == null) {
            throw new NullPointerException ("null path parameter");
        }
        contexts.remove (protocol, path);
        logger.config ("context removed: " + path);
    }

    public synchronized void removeContext (HttpContext context) throws IllegalArgumentException {
        if (!(context instanceof HttpContextImpl)) {
            throw new IllegalArgumentException ("wrong HttpContext type");
        }
        contexts.remove ((HttpContextImpl)context);
        logger.config ("context removed: " + context.getPath());
    }

    public InetSocketAddress getAddress() {
        return (InetSocketAddress)schan.socket().getLocalSocketAddress();
    }

    Selector getSelector () {
        return selector;
    }

    void addEvent (Event r) {
        synchronized (lolock) {
            events.add (r);
            selector.wakeup();
        }
    }    

    static boolean debug = ServerConfig.debugEnabled ();

    static synchronized void dprint (String s) {
        if (debug) {
            System.out.println (s);
        }
    }

    static synchronized void dprint (Exception e) {
        if (debug) {
            System.out.println (e);
            e.printStackTrace();
        }
    }

    Logger getLogger () {
        return logger;
    }

    public void closeConnection(HttpConnection conn) {
        conn.close();
        allConnections.remove(conn);
        switch (conn.getState()) {
        case REQUEST:
            reqConnections.remove(conn);
            break;
        case RESPONSE:
            rspConnections.remove(conn);
            break;
        case IDLE:
            idleConnections.remove(conn);
            break;
        }
        assert !reqConnections.remove(conn);
        assert !rspConnections.remove(conn);
        assert !idleConnections.remove(conn);
    }

    void logReply (int code, String requestStr, String text) {
        if (!logger.isLoggable(Level.FINE)) {
            return;
        }
        if (text == null) {
            text = "";
        }
        String r;
        if (requestStr.length() > 80) {
           r = requestStr.substring (0, 80) + "<TRUNCATED>";
        } else {
           r = requestStr;
        }
        String message = r + " [" + code + " " +
                    Code.msg(code) + "] ("+text+")";
        logger.fine (message);
    }

    long getTicks() {
        return ticks;
    }

    public long getTime() {
        return time;
    }

    void delay () {
        Thread.yield();
        try {
            Thread.sleep (200);
        } catch (InterruptedException e) {}
    }

    private int exchangeCount = 0;

    synchronized void startExchange () {
        exchangeCount ++;
    }

    synchronized int endExchange () {
        exchangeCount --;
        assert exchangeCount >= 0;
        return exchangeCount;
    }

    HttpServer getWrapper () {
        return wrapper;
    }

    void requestStarted (HttpConnection c) {
        c.creationTime = getTime();
        c.setState (State.REQUEST);
        reqConnections.add (c);
    }

    // called after a request has been completely read
    // by the server. This stops the timer which would
    // close the connection if the request doec't arrive
    // quickly enough. It then starts the timer
    // that ensures the client reads the response in a timely
    // fashion.

    void requestCompleted (HttpConnection c) {
        assert c.getState() == State.REQUEST;
        reqConnections.remove (c);
        c.rspStartedTime = getTime();
        rspConnections.add (c);
        c.setState (State.RESPONSE);
    }

    // called after response has been sent
    void responseCompleted (HttpConnection c) {
        assert c.getState() == State.RESPONSE;
        rspConnections.remove (c);
        c.setState (State.IDLE);
    }

    /**
     * TimerTask run every CLOCK_TICK ms
     */
    class ServerTimerTask extends TimerTask {
        public void run () {
            LinkedList<HttpConnection> toClose = new LinkedList<HttpConnection>();
            time = System.currentTimeMillis();
            ticks ++;
            synchronized (idleConnections) {
                for (HttpConnection c : idleConnections) {
                    if (c.time <= time) {
                        toClose.add (c);
                    }
                }
                for (HttpConnection c : toClose) {
                    idleConnections.remove (c);
                    allConnections.remove (c);
                    c.close();
                }
            }
        }
    }

    class ServerTimerTask1 extends TimerTask {

        // runs every TIMER_MILLIS
        public void run () {
            LinkedList<HttpConnection> toClose = new LinkedList<HttpConnection>();
            time = System.currentTimeMillis();
            synchronized (reqConnections) {
                if (MAX_REQ_TIME != -1) {
                    for (HttpConnection c : reqConnections) {
                        if (c.creationTime + TIMER_MILLIS + MAX_REQ_TIME <= time) {
                            toClose.add (c);
                        }
                    }
                    for (HttpConnection c : toClose) {
                        logger.log (Level.FINE, "closing: no request: " + c);
                        reqConnections.remove (c);
                        allConnections.remove (c);
                        c.close();
                    }
                }
            }
            toClose = new LinkedList<HttpConnection>();
            synchronized (rspConnections) {
                if (MAX_RSP_TIME != -1) {
                    for (HttpConnection c : rspConnections) {
                        if (c.rspStartedTime + TIMER_MILLIS +MAX_RSP_TIME <= time) {
                            toClose.add (c);
                        }
                    }
                    for (HttpConnection c : toClose) {
                        logger.log (Level.FINE, "closing: no response: " + c);
                        rspConnections.remove (c);
                        allConnections.remove (c);
                        c.close();
                    }
                }
            }
        }
    }

    void logStackTrace (String s) {
        logger.finest (s);
        StringBuilder b = new StringBuilder ();
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        for (int i=0; i<e.length; i++) {
            b.append (e[i].toString()).append("\n");
        }
        logger.finest (b.toString());
    }

    static long getTimeMillis(long secs) {
        if (secs == -1) {
            return -1;
        } else {
            return secs * 1000;
        }
    }
}
