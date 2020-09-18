/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.resources.HttpserverMessages;
import com.hh.xml.internal.ws.transport.http.HttpAdapter;
import com.hh.xml.internal.ws.transport.http.WSHTTPConnection;

import com.hh.webservice.ws.spi.http.HttpHandler;
import com.hh.webservice.ws.spi.http.HttpExchange;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * {@link HttpHandler} implementation that serves the actual request.
 *
 * @author Jitendra Kotamraju
 */
final class PortableHttpHandler extends HttpHandler {

    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String HEAD_METHOD = "HEAD";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";

    private static final Logger logger =
        Logger.getLogger(com.hh.xml.internal.ws.util.Constants.LoggingDomain + ".server.http");

    private final HttpAdapter adapter;
    private final Executor executor;

    public PortableHttpHandler(@NotNull HttpAdapter adapter, @Nullable Executor executor) {
        assert adapter!=null;
        this.adapter = adapter;
        this.executor = executor;
    }

    /**
     * Called by HttpServer when there is a matching request for the context
     */
    public void handle(HttpExchange msg) {
        try {
            logger.fine("Received HTTP request:"+msg.getRequestURI());
            if (executor != null) {
                // Use application's Executor to handle request. Application may
                // have set an executor using Endpoint.setExecutor().
                executor.execute(new HttpHandlerRunnable(msg));
            } else {
                handleExchange(msg);
            }
        } catch(Throwable e) {
            // Dont't propagate the exception otherwise it kills the httpserver
            e.printStackTrace();
        }
    }

    public void handleExchange(HttpExchange msg) throws IOException {
        WSHTTPConnection con = new PortableConnectionImpl(adapter,msg);
        try {
            logger.fine("Received HTTP request:"+msg.getRequestURI());
            String method = msg.getRequestMethod();
            if(method.equals(GET_METHOD) || method.equals(POST_METHOD) || method.equals(HEAD_METHOD)
            || method.equals(PUT_METHOD) || method.equals(DELETE_METHOD)) {
                adapter.handle(con);
            } else {
                logger.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
            }
        } finally {
            msg.close();
        }
    }

    /**
     * Wrapping the processing of request in a Runnable so that it can be
     * executed in Executor.
     */
    class HttpHandlerRunnable implements Runnable {
        final HttpExchange msg;

        HttpHandlerRunnable(HttpExchange msg) {
            this.msg = msg;
        }

        public void run() {
            try {
                handleExchange(msg);
            } catch (Throwable e) {
                // Does application's executor handle this exception ?
                e.printStackTrace();
            }
        }
    }

}
