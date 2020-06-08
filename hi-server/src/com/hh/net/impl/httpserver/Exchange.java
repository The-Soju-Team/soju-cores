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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.logging.Level;
import javax.net.ssl.SSLEngine;

/**
 *
 * @author hiendm1
 */
public class Exchange extends UntypedActor {

    SocketChannel chan;
    HttpConnection connection;
    HttpContextImpl context;
    InputStream rawin;
    OutputStream rawout;
    String protocol;
    ExchangeImpl tx;
    HttpContextImpl ctx;
    boolean rejected = false;
    ServerImpl server;

    Exchange(SocketChannel chan, String protocol, HttpConnection conn, ServerImpl server) throws IOException {
        this.chan = chan;
        this.connection = conn;
        this.protocol = protocol;
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
    
    private void handleRequest() throws Exception {
        /* context will be null for new connections */
        context = connection.getHttpContext();
        boolean newconnection;
        SSLEngine engine = null;
        String requestLine = null;
        SSLStreams sslStreams = null;
        try {
            if (context != null) {
                this.rawin = connection.getInputStream();
                this.rawout = connection.getRawOutputStream();
                newconnection = false;
            } else {
                /* figure out what kind of connection this is */
                newconnection = true;
                if (server.https) {
                    if (server.sslContext == null) {
                        server.logger.warning("SSL connection received. No https contxt created");
                        throw new HttpError("No SSL context established");
                    }
                    sslStreams = new SSLStreams(server, server.sslContext, chan);
                    rawin = sslStreams.getInputStream();
                    rawout = sslStreams.getOutputStream();
                    engine = sslStreams.getSSLEngine();
                    connection.sslStreams = sslStreams;
                } else {
                    rawin = new BufferedInputStream(
                            new Request.ReadStream(
                                    server, chan
                            ));
                    rawout = new Request.WriteStream(
                            server, chan
                    );
                }
                connection.raw = rawin;
                connection.rawout = rawout;
            }
            Request req = new Request(rawin, rawout);
            requestLine = req.requestLine();
            if (requestLine == null) {
                /* connection closed */
                connection.close();
                return;
            }
            int space = requestLine.indexOf(' ');
            if (space == -1) {
                reject(Code.HTTP_BAD_REQUEST,
                        requestLine, "Bad request line");
                return;
            }
            String method = requestLine.substring(0, space);
            int start = space + 1;
            space = requestLine.indexOf(' ', start);
            if (space == -1) {
                reject(Code.HTTP_BAD_REQUEST,
                        requestLine, "Bad request line");
                return;
            }

            RequestProcessor rp = new RequestProcessor(this, req, start, space,
                            requestLine, method, newconnection, engine, sslStreams);
            rp.handleRequest();

        } catch (IOException e1) {
            server.logger.log(Level.FINER, "ServerImpl.Exchange (1)", e1);
            connection.close();
        } catch (NumberFormatException e3) {
            reject(Code.HTTP_BAD_REQUEST,
                    requestLine, "NumberFormatException thrown");
        } catch (Exception e4) {
            server.logger.log(Level.FINER, "ServerImpl.Exchange (2)", e4);
            connection.close();
        }      
    }

    void reject(int code, String requestStr, String message) {
        rejected = true;
        sendReply(
                code, false, "<h1>" + code + Code.msg(code) + "</h1>" + message
        );
        connection.close();
    }

    void sendReply(
            int code, boolean closeNow, String text) {
        try {
            StringBuilder builder = new StringBuilder(512);
            builder.append("HTTP/1.1 ")
                    .append(code).append(Code.msg(code)).append(System.getProperty("line.separator").toString());

            if (text != null && text.length() != 0) {
                builder.append("Content-Length: ")
                        .append(text.length()).append(System.getProperty("line.separator").toString())
                        .append("Content-Type: text/html\r\n");
            } else {
                builder.append("Content-Length: 0\r\n");
                text = "";
            }
            if (closeNow) {
                builder.append("Connection: close\r\n");
            }
            builder.append(System.getProperty("line.separator").toString()).append(text);
            String s = builder.toString();
            byte[] b = s.getBytes("ISO8859_1");
            rawout.write(b);
            rawout.flush();
            if (closeNow) {
                connection.close();
            }
        } catch (IOException e) {
            server.logger.log(Level.FINER, "ServerImpl.sendReply", e);
            connection.close();
        }
    }

    void requestCompleted (HttpConnection c) {
        assert c.getState() == HttpConnection.State.REQUEST;
        c.rspStartedTime = new Date().getTime();
        c.setState (HttpConnection.State.RESPONSE);
    }    
}
