package com.hh.connector.netty.client;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.hh.connector.netty.server.NettyServer;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by HienDM
 */
public class ClientHandler extends SimpleChannelInboundHandler {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ClientHandler.class.getSimpleName());
    private String connector;
    private Server server;

    public ClientHandler(String connector, Server server) {
        this.connector = connector;
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        log.debug("Channel is active: " + channelHandlerContext.name());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ActorRef actor = NettyServer.system
                    .actorOf(Props.create(ClientDispatcher.class, ctx, server).withDispatcher("hh-dispatcher"));
            actor.tell(msg, actor);
            actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
        } catch (Exception e) {
            log.error("Error receive from client: ", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        // xử lý exception, đóng kết nối.
        log.debug("Exception on ClientHandler: !!!!!!!", cause);
        channelHandlerContext.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("Ping event triggered");
        // detect trạng thái idle, gửi PING giữ kết nối
        if (evt instanceof IdleStateEvent) {
            ByteBuf buf = ctx.alloc().directBuffer().writeBytes(
                    ServerDecoder.mapToByteArray(Config.pingMessage(server.config.getConfig("server-code"))));
            ChannelFuture future = ctx.writeAndFlush(buf);
            future.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    log.info("SEND PING FAIL: ");
                    log.info("CAUSE: " + future1.cause());
                }
            });
        }

        ctx.fireUserEventTriggered(evt);
    }
}
