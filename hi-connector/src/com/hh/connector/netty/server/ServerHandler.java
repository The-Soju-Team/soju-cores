package com.hh.connector.netty.server;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author HienDM1
 * @version 1.0
 * @date Apr 14, 2011
 * @since 1.0
 */
public class ServerHandler extends SimpleChannelInboundHandler {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerHandler.class.getSimpleName());

    private Server server;

    public ServerHandler(Server server) {
        this.server = server;
    }

    public static void onSender(ChannelHandlerContext ctx, LinkedTreeMap msg, Server server) throws Exception {
        try {
            if (ctx.channel() != null && ctx.channel().isOpen() && ctx.channel().isActive()) {
                ActorRef actor = NettyServer.system
                        .actorOf(Props.create(ServerSender.class, ctx, server).withDispatcher("hh-dispatcher"));
                actor.tell(msg, actor);
                actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
            } else {
                List lstRow = new ArrayList();
                lstRow.add(ctx);
                lstRow.add(msg);
                server.nettyServer.retryMessage.put(UUID.randomUUID().toString(), lstRow);
            }
        } catch (Exception e) {
            log.error("Error return to client: ", e);
        }
    }

    public static String padLeft(String msg, int length) {
        StringBuilder strLeft = new StringBuilder();
        int left = length - msg.length();
        for (int i = 0; i < left; i++)
            strLeft.append(" ");
        strLeft.append(msg);
        return strLeft.toString();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        ActorRef actor = NettyServer.system
                .actorOf(Props.create(ServerDispatcher.class, ctx, server).withDispatcher("hh-dispatcher"));
        actor.tell(obj, actor);
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        log.error("Exception on ClientHandler: ", cause);
        channelHandlerContext.close();
    }
}
