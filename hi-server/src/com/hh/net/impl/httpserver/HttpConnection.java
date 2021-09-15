/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * encapsulates all the connection specific state for a HTTP/S connection
 * one of these is hung from the selector attachment and is used to locate
 * everything from that.
 */
class HttpConnection {

    HttpContextImpl context;
    SSLEngine engine;
    SSLContext sslContext;
    SSLStreams sslStreams;

    /* high level streams returned to application */
    InputStream i;

    /* low level stream that sits directly over channel */
    InputStream raw;
    OutputStream rawout;

    SocketChannel chan;
    SelectionKey selectionKey;
    String protocol;
    long time;
    volatile long creationTime; // time this connection was created
    volatile long rspStartedTime; // time we started writing the response
    int remaining;
    boolean closed = false;
    Logger logger;
    volatile State state;

    ;
    HttpConnection() {
    }

    public String toString() {
        String s = null;
        if (chan != null) {
            s = chan.toString();
        }
        return s;
    }

    void setContext(HttpContextImpl ctx) {
        context = ctx;
    }

    State getState() {
        return state;
    }

    void setState(State s) {
        state = s;
    }

    void setParameters(
            InputStream in, OutputStream rawout, SocketChannel chan,
            SSLEngine engine, SSLStreams sslStreams, SSLContext sslContext, String protocol,
            HttpContextImpl context, InputStream raw
    ) {
        this.context = context;
        this.i = in;
        this.rawout = rawout;
        this.raw = raw;
        this.protocol = protocol;
        this.engine = engine;
        this.chan = chan;
        this.sslContext = sslContext;
        this.sslStreams = sslStreams;
        this.logger = context.getLogger();
    }

    SocketChannel getChannel() {
        return chan;
    }

    void setChannel(SocketChannel c) {
        chan = c;
    }

    synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        try {
            if (sslStreams != null) {
                sslStreams.close();
                sslStreams = null;
            }
        } catch (IOException e) {
            ServerImpl.dprint(e);
        }

        if (logger != null && chan != null) {
            logger.finest("Closing connection: " + chan.toString());
        }

        if (!chan.isOpen()) {
            ServerImpl.dprint("Channel already closed");
            return;
        }
        try {
            if (raw != null) {
                raw.close();
                raw = null;
            }
        } catch (IOException e) {
            ServerImpl.dprint(e);
        }
        try {
            if (rawout != null) {
                rawout.close();
                rawout = null;
            }
        } catch (IOException e) {
            ServerImpl.dprint(e);
        }
        try {
            chan.close();
            chan = null;
        } catch (IOException e) {
            ServerImpl.dprint(e);
        }
        try {
            if (i != null) {
                i.close();
                i = null;
            }
        } catch (IOException e) {
            ServerImpl.dprint(e);
        }

        context = null;
        sslContext = null;
    }

    int getRemaining() {
        return remaining;
    }

    /* remaining is the number of bytes left on the lowest level inputstream
     * after the exchange is finished
     */
    void setRemaining(int r) {
        remaining = r;
    }

    SelectionKey getSelectionKey() {
        return selectionKey;
    }

    InputStream getInputStream() {
        return i;
    }

    OutputStream getRawOutputStream() {
        return rawout;
    }

    String getProtocol() {
        return protocol;
    }

    SSLEngine getSSLEngine() {
        return engine;
    }

    SSLContext getSSLContext() {
        return sslContext;
    }

    HttpContextImpl getHttpContext() {
        return context;
    }

    public enum State {IDLE, REQUEST, RESPONSE}
}
