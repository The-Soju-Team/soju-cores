/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.net.impl.httpserver;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author hiendm1
 */
public class Dispatcher extends UntypedActor {

    public ServerImpl server;

    public Dispatcher(ServerImpl server) {
        this.server = server;
    }
    
    @Override
    public void onReceive(Object o) {
        try {        
            try {
                handleRequest();
            } catch(Exception ex) {
                server.logger.log(Level.FINER, "ServerImpl.Exchange (1)", ex);
                try {
                    getContext().stop(getSelf());
                } catch (Exception e) {}            
            } 
        } catch (Throwable e) {
            server.logger.log(Level.FINER, "Co loi Fatal ", e);
        }        
    }    
    
    private void handleRequest() {
        while (!server.finished) {
            try {
                ListIterator<HttpConnection> li
                        = connsToRegister.listIterator();
                for (HttpConnection c : connsToRegister) {
                    reRegister(c);
                }
                connsToRegister.clear();

                List<Event> list = null;
                server.selector.select(1000);
                synchronized (server.lolock) {
                    if (server.events.size() > 0) {
                        list = server.events;
                        server.events = new LinkedList<Event>();
                    }
                }

                if (list != null) {
                    for (Event r : list) {
                        handleEvent(r);
                    }
                }

                /* process the selected list now  */
                Set<SelectionKey> selected = server.selector.selectedKeys();
                Iterator<SelectionKey> iter = selected.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.equals(server.listenerKey)) {
                        if (server.terminating) {
                            continue;
                        }
                        SocketChannel chan = server.schan.accept();

                        // Set TCP_NODELAY, if appropriate
                        if (ServerConfig.noDelay()) {
                            chan.socket().setTcpNoDelay(true);
                        }

                        if (chan == null) {
                            continue; /* cancel something ? */

                        }
                        chan.configureBlocking(false);
                        SelectionKey newkey = chan.register(server.selector, SelectionKey.OP_READ);
                        HttpConnection c = new HttpConnection();
                        c.selectionKey = newkey;
                        c.setChannel(chan);
                        newkey.attach(c);
                        server.requestStarted(c);
                        server.allConnections.add(c);
                    } else {
                        try {
                            if (key.isReadable()) {
                                SocketChannel chan = (SocketChannel) key.channel();
                                HttpConnection conn = (HttpConnection) key.attachment();

                                key.cancel();
                                chan.configureBlocking(true);
                                if (server.idleConnections.remove(conn)) {
                                    // was an idle connection so add it
                                    // to reqConnections set.
                                    server.requestStarted(conn);
                                }
                                handle(chan, conn);
                            } else {
                                assert false;
                            }
                        } catch (CancelledKeyException e) {
                            handleException(key, null);
                        } catch (IOException e) {
                            handleException(key, e);
                        }
                    }
                }
                // call the selector just to process the cancelled keys
                server.selector.selectNow();
            } catch (IOException e) {
                server.logger.log(Level.FINER, "Dispatcher (4)", e);
            } catch (Exception e) {
                e.printStackTrace();
                server.logger.log(Level.FINER, "Dispatcher (7)", e);
            }
        }        
    }
    
    private void handleEvent(Event r) {
        ExchangeImpl t = r.exchange;
        HttpConnection c = t.getConnection();
        try {
            if (r instanceof WriteFinishedEvent) {

                int exchanges = server.endExchange();
                if (server.terminating && exchanges == 0) {
                    server.finished = true;
                }
                server.responseCompleted(c);
                try(LeftOverInputStream is = t.getOriginalInputStream();)
                {
                    if (!is.isEOF()) {
                        t.close = true;
                    }
                    if (t.close || server.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS) {
                        c.close();
                        server.allConnections.remove(c);
                    } else {
                        if (is.isDataBuffered()) {
                            /* don't re-enable the interestops, just handle it */
                            server.requestStarted(c);
                            handle(c.getChannel(), c);
                        } else {
                            connsToRegister.add(c);
                        }
                    }
                }
            }
        } catch (Exception e) {
            server.logger.log(
                    Level.FINER, "Dispatcher (1)", e
            );
            if(c != null) c.close();
            c = null;
        }
    }

    final LinkedList<HttpConnection> connsToRegister
            = new LinkedList<HttpConnection>();

    void reRegister(HttpConnection c) {
        /* re-register with selector */
        try {
            SocketChannel chan = c.getChannel();
            if(chan != null) {
                chan.configureBlocking(false);
                SelectionKey key = chan.register(server.selector, SelectionKey.OP_READ);
                key.attach(c);
                c.selectionKey = key;
                c.time = server.getTime() + ServerImpl.IDLE_INTERVAL;
                server.idleConnections.add(c);
            }
        } catch (IOException e) {
            server.dprint(e);
            server.logger.log(Level.FINER, "Dispatcher(8)", e);
            c.close();
        }
    }

    private void handleException(SelectionKey key, Exception e) {
        HttpConnection conn = (HttpConnection) key.attachment();
        if (e != null) {
            server.logger.log(Level.FINER, "Dispatcher (2)", e);
        }
        server.closeConnection(conn);
    }

    public void handle(SocketChannel chan, HttpConnection conn)
            throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ActorRef actor = com.hh.server.HHServer.system.actorOf(
                Props.create(Exchange.class, chan, server.protocol, conn, server).withDispatcher("hh-dispatcher"));
        actor.tell("execute", actor);
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
