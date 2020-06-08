/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.server;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;

/**
 *
 * @author HienDM
 */
public class ServerDispatcher extends UntypedActor {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerDispatcher.class.getSimpleName());
    public ChannelHandlerContext ctx;
    public Server server;
    
    public ServerDispatcher(ChannelHandlerContext ctx, Server server) {
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
        String ipAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        if (Config.CMD_KEEP_ALIGN.equals(msg.get("hi-path"))) {
            ServerHandler.onSender(ctx, Config.pingMessage(server.config.getConfig("server-code")), server);
            log.debug("Accepted keep alive from: [" + msg.get("server-code") + "]" + ipAddress);
        } else {
            String accessToken = (String) msg.get("token");
            if (server.nettyServer.isValidToken(ipAddress, accessToken)) {
                Config.printServerMessage((String) msg.get("server-code"), msg, null, true, server.config.getConfig("server-code"));
                if (msg.containsKey("hi-process") && msg.get("hi-process") != null) {
                    String command = msg.get("hi-process").toString();
                    Class process = null;
                    for (Map.Entry<String, Class> entry : server.serverAction.entrySet()) {
                        if (entry.getKey().equals(command)) {
                            process = entry.getValue();
                        }
                    }

                    ActorRef actor = null;
                    if (process != null) {
                        boolean checkFilter = true;
                        if(server.serverFilter != null) checkFilter = server.serverFilter.doFilter(msg, server);
                        if(checkFilter)
                            actor = NettyServer.system.actorOf(Props.create(process, ctx, server).withDispatcher("hh-dispatcher"));
                    } else {
                        ServerHandler.onSender(ctx, Config.responseMessage(msg, Config.ERROR_NOT_SUPPORT, "Command " + command + " is not supported"), server);
                        return;
                    }
                    if (actor != null) {
                        actor.tell(msg, actor);
                        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
                    }
                } else {
                    ServerHandler.onSender(ctx, Config.responseMessage(msg, Config.ERROR_NOT_SUPPORT, "Command NULL is not supported"), server);
                    return;                    
                }
            } else {
                String errorMessage = "username or password is invalid";
                if (msg.containsKey("content")) {
                    msg.remove("content");
                }
                msg.put("error_code", Config.ERROR_INVALID_USER);
                msg.put("message", errorMessage);
                ServerHandler.onSender(ctx, msg, server);
                log.info(errorMessage);
            }
        }
    }
}
