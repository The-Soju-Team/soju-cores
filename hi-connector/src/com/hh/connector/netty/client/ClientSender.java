/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.client;

import akka.actor.UntypedActor;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import com.google.gson.internal.LinkedTreeMap;

/**
 *
 * @author Ha
 */
public class ClientSender extends UntypedActor {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ClientSender.class.getSimpleName());
    private String connector;
    private String token;
    private Channel channel;
    private String address;
    private Server server;
    
    public ClientSender(String connector, String token, Channel channel, String address, Server server) {
        this.connector = connector;
        this.token = token;
        this.channel = channel;
        this.address = address;
        this.server = server;
    }
    
    @Override
    public void onReceive(Object obj) {
        try {        
            try {
                process(obj);
            } catch(Exception ex) {
                log.error("Error receive message", ex);
                try {
                    getContext().stop(getSelf());
                } catch (Exception e) {}            
            } 
        } catch (Throwable e) {
            log.error("Co loi Fatal ", e);
        }   
    }   

    public void process(Object obj) throws Exception {
        final String serverCode = server.config.getConfig("server-code");
        try {
            LinkedTreeMap msg = (LinkedTreeMap) obj;
            Config.printClientMessage(connector, msg, null, true, server.config.getConfig("server-code"));
            if(token != null && !token.isEmpty()) msg.put("token", token);
            if(serverCode != null && !serverCode.isEmpty()) msg.put("server-code", serverCode);
            byte[] msgBytes = ServerDecoder.mapToByteArray(msg); // pack bản tin iso ra mảng byte
            if(channel != null) {
                ByteBuf buf = channel.alloc().buffer().writeBytes(msgBytes);
                Future future = channel.writeAndFlush(buf);
                /*
                gửi bản tin đi thông qua channel và nhận kết quả qua ChannelFuture.
                 */
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info(serverCode.toUpperCase() + " SEND_SUCCESS_NETTY: " + address);
                        } else {
                            log.info(serverCode.toUpperCase() + "SEND_FAIL_NETTY: " + address);
                            log.info("CAUSE: " + future.cause());
                        }
                    }
                });
            } else {
                log.info(serverCode.toUpperCase() + "LOST CONNECTION CANT NOT SEND TO " + address);
            }
        } catch (Exception e) {
            log.error(serverCode.toUpperCase() + "CANT NOT SEND TO " + address, e);
        }
    }
}
