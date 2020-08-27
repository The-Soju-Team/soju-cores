/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.net.impl.httpserver;

import akka.actor.UntypedActor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

/**
 * @author hiendm1
 */
public class ServerAccepter extends UntypedActor {

    SelectionKey key;
    ServerImpl server;

    ServerAccepter(SelectionKey key, ServerImpl server) throws IOException {
        this.key = key;
        this.server = server;
    }

    @Override
    public void onReceive(Object o) {
        try {
            handleRequest();
        } catch (Exception ex) {
            server.logger.log(Level.FINER, "ServerImpl.Exchange (1)", ex);
            try {
                getContext().stop(getSelf());
            } catch (Exception e) {
            }
        }
    }

    private void handleRequest() throws Exception {
        if (key.equals(server.listenerKey)) {
//            if (server.terminating) {
//                continue;
//            }
            SocketChannel chan = server.schan.accept();

            // Set TCP_NODELAY, if appropriate
            if (ServerConfig.noDelay()) {
                chan.socket().setTcpNoDelay(true);
            }

//            if (chan == null) {
//                continue; /* cancel something ? */
//
//            }
            chan.configureBlocking(false);
            SelectionKey newkey = chan.register(server.selector, SelectionKey.OP_READ);
            HttpConnection c = new HttpConnection();
            c.selectionKey = newkey;
            c.setChannel(chan);
            newkey.attach(c);
            server.requestStarted(c);
            server.allConnections.add(c);
        }
    }

}
