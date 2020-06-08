/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.client;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.hh.connector.netty.server.NettyServer;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.netty.server.ServerHandler;
import java.util.Map;

/**
 *
 * @author HienDM
 */
public class ClientDispatcher extends UntypedActor {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ClientDispatcher.class.getSimpleName());
    public ChannelHandlerContext ctx;
    public Server server;
    
    public ClientDispatcher(ChannelHandlerContext ctx, Server server) {
        this.ctx = ctx;
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
        LinkedTreeMap msg = ServerDecoder.byteArrayToMap((byte[]) obj);
        ActorRef actor = null;
        if (msg.containsKey("hi-process") && msg.get("hi-process") != null) {
            String command = msg.get("hi-process").toString();
            Class process = null;

            for (Map.Entry<String, Class> entry : server.clientAction.entrySet()) {
                if (entry.getKey().equals(command)) {
                    process = entry.getValue();
                }
            }

            if (process != null) {
                boolean checkFilter = true;
                if(server.clientFilter != null) checkFilter = server.clientFilter.doFilter(msg, server);
                if(checkFilter)
                    actor = NettyServer.system.actorOf(Props.create(process, ctx, server).withDispatcher("hh-dispatcher"));
            } else {
                Config.printServerMessage("CLIENT", msg, null, false, server.config.getConfig("server-code"));
                return;
            }
            if (actor != null) {
                actor.tell(msg, actor);
                actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
            }
        } 
        if(actor == null) ServerHandler.onSender(ctx, Config.responseMessage(msg, Config.ERROR_NOT_SUPPORT, "can't find hi-process!"), server);
    }
}
