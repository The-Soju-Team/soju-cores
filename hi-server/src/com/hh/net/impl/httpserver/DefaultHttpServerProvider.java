/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.net.impl.httpserver;

import com.hh.net.httpserver.HttpServer;
import com.hh.net.httpserver.HttpsServer;
import com.hh.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DefaultHttpServerProvider extends HttpServerProvider {
    public HttpServer createHttpServer(InetSocketAddress addr, int backlog) throws IOException {
        return new HttpServerImpl(addr, backlog);
    }

    public HttpsServer createHttpsServer(InetSocketAddress addr, int backlog) throws IOException {
        return new HttpsServerImpl(addr, backlog);
    }
}
