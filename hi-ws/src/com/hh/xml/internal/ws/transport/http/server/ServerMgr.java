/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.hh.xml.internal.ws.transport.http.server;


import com.hh.net.httpserver.HttpContext;
import com.hh.net.httpserver.HttpServer;
import com.hh.xml.internal.ws.server.ServerRtException;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Manages all the WebService HTTP servers created by JAXWS runtime.
 *
 * @author Jitendra Kotamraju
 */
final class ServerMgr {

    private static final ServerMgr serverMgr = new ServerMgr();
    private static final Logger logger =
        Logger.getLogger(com.hh.xml.internal.ws.util.Constants.LoggingDomain + ".server.http");
    private final Map<InetSocketAddress,ServerState> servers = new HashMap<InetSocketAddress,ServerState>();

    private ServerMgr() {}

    /**
     * Gets the singleton instance.
     * @return manager instance
     */
    static ServerMgr getInstance() {
        return serverMgr;
    }

    /*
     * Creates a HttpContext at the given address. If there is already a server
     * it uses that server to create a context. Otherwise, it creates a new
     * HTTP server. This sever is added to servers Map.
     */
    /*package*/ HttpContext createContext(String address) {
        try {
            HttpServer server;
            ServerState state;
            URL url = new URL(address);
            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            InetSocketAddress inetAddress = new InetSocketAddress(url.getHost(),
                port);
            synchronized(servers) {
                state = servers.get(inetAddress);
                if (state == null) {
                    logger.fine("Creating new HTTP Server at "+inetAddress);
                    // Creates server with default socket backlog
                    server = HttpServer.create(inetAddress, 0);
                    server.setExecutor(Executors.newCachedThreadPool());
                    String path = url.toURI().getPath();
                    logger.fine("Creating HTTP Context at = "+path);
                    HttpContext context = server.createContext(path);
                    server.start();
                    logger.fine("HTTP server started = "+inetAddress);
                    state = new ServerState(server);
                    servers.put(inetAddress, state);
                    return context;
                }
            }
            server = state.getServer();
            logger.fine("Creating HTTP Context at = "+url.getPath());
            HttpContext context = server.createContext(url.getPath());
            state.oneMoreContext();
            return context;
        } catch(Exception e) {
            throw new ServerRtException("server.rt.err",e );
        }
    }

    /*
     * Removes a context. If the server doesn't have anymore contexts, it
     * would stop the server and server is removed from servers Map.
     */
    /*package*/ void removeContext(HttpContext context) {
        InetSocketAddress inetAddress = context.getServer().getAddress();
        synchronized(servers) {
            ServerState state = servers.get(inetAddress);
            int instances = state.noOfContexts();
            if (instances < 2) {
                ((ExecutorService)state.getServer().getExecutor()).shutdown();
                state.getServer().stop(0);
                servers.remove(inetAddress);
            } else {
                state.getServer().removeContext(context);
                state.oneLessContext();
            }
        }
    }

    private static final class ServerState {
        private final HttpServer server;
        private int instances;

        ServerState(HttpServer server) {
            this.server = server;
            this.instances = 1;
        }

        public HttpServer getServer() {
            return server;
        }

        public void oneMoreContext() {
            ++instances;
        }

        public void oneLessContext() {
            --instances;
        }

        public int noOfContexts() {
            return instances;
        }
    }
}
