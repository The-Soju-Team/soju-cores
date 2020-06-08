/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.server;

import akka.actor.ActorSystem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.typesafe.config.ConfigFactory;
import com.hh.util.EncryptDecryptUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author HienDM1
 */
public class NettyServer {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NettyServer.class.getSimpleName());
    public static ActorSystem system;
    private HashMap acceptClient = new HashMap();
    private NioEventLoopGroup workerGroup;
    private Server server;
    private Timer retryTimer;
    public Cache<Object, Object> retryMessage;
    
    public NettyServer(Server server) {
        this.server = server;
        if(system == null) {
            retryMessage = CacheBuilder.newBuilder()
                    .maximumSize(1000000)
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .build();
            
            retryTimer = new Timer();
            retryTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    retrySendMessage();
                }
            }, 0, 10000);            
            
            String coreNumber = server.config.getConfig("cpu-number");
            if(coreNumber == null || coreNumber.trim().isEmpty()) coreNumber = "8";            
            system = ActorSystem.create("hi-connector", ConfigFactory.parseString(
                    "hh-dispatcher {\n"
                    + "  fork-join-executor {\n"
                    + "    parallelism-min = " + coreNumber + "\n"
                    + "    parallelism-factor = 1\n"
                    + "    parallelism-max = " + coreNumber + "\n"
                    + "  }\n"
                    + " }\n"
                    + " akka {\n"
                    + "  jvm-exit-on-fatal-error = false\n"
                    + " }"
            ));
        }        
    }
    
    public void startServer() throws Exception {    
        loadAcceptor();
        initListener();  
    }
    
    public void loadAcceptor() {
        try {
            int count = 1;
            while (!server.config.getAccept("client" + count).isEmpty()) {
                String[] hosts = server.config.getAccept("client" + count + ".hosts").split(",");
                String userName = server.config.getAccept("client" + count + ".username");
                String password = server.config.getAccept("client" + count + ".password");
                for(int i = 0; i< hosts.length; i++) {
                    HashMap mapAcceptor = new HashMap();
                    mapAcceptor.put("username", userName);
                    mapAcceptor.put("password", password);
                    mapAcceptor.put("host",hosts[i]);
                    mapAcceptor.put("clientname",server.config.getAccept("client" + count));
                    acceptClient.put(EncryptDecryptUtils.encodeSHA256(
                            hosts[i] + EncryptDecryptUtils.encodeSHA256(userName + password)), 
                            mapAcceptor);
                }
                count++;
            }
        } catch (Exception ex) {
            log.error("Error when init connector", ex);
        }        
    }
    
    public boolean isValidToken(String ipAddress, String token) throws Exception {
        if("true".equals(server.config.getConfig("accept-all-client"))) return true;
        if(acceptClient.isEmpty()) return true;
        if(ipAddress != null && token != null)
            return acceptClient.containsKey(EncryptDecryptUtils.encodeSHA256(ipAddress + token));
        return false;
    }
    
    private void initListener() {
        log.info("Socket is starting...");
        
        try{
            if("".equals(server.config.getConfig("netty-port"))) return;
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            final SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());            
            
            Integer port = Integer.parseInt(server.config.getConfig("netty-port"));
            log.info("Socket Acceptor is listening on port " + port);
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            workerGroup = new NioEventLoopGroup();
            serverBootstrap.group(workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(port));
            serverBootstrap.option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    if("true".equals(server.config.getConfig("netty-ssl"))) {
                        socketChannel.pipeline().addLast(sslCtx.newHandler(socketChannel.alloc()));
                    }
                    socketChannel.pipeline().addLast(new ServerDecoder(), new IdleStateHandler(0L, 0L, 30L, TimeUnit.SECONDS), new ServerHandler(server));
                }
            });
            
            if(server.loadAfterReady != null) server.loadAfterReady.process();
            
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            log.error("error when start netty server", e);
        } finally {
            if(workerGroup != null) workerGroup.shutdownGracefully();
        }
    }    
    
    public void stopServer() {
        if(workerGroup != null) workerGroup.shutdownGracefully();
    }
    
    private void retrySendMessage() {
        try {
            log.debug("Retry cache size: " + retryMessage.size());
            Map<Object, Object> mapMessage = retryMessage.asMap();
            for(Map.Entry<Object, Object> entry : mapMessage.entrySet()) {
                List row = (ArrayList) entry.getValue();
                ChannelHandlerContext ctx = (ChannelHandlerContext) row.get(0);
                if(ctx.channel() != null && ctx.channel().isOpen() && ctx.channel().isActive()) {
                    LinkedTreeMap msg = (LinkedTreeMap)entry.getValue();
                    log.debug("===> Retry message: " + entry.getValue());
                    retryMessage.invalidate(entry.getValue());
                    ServerHandler.onSender(ctx, msg, server);
                }
            }
        } catch(Exception ex) {
            log.error("Error when retry send message", ex);
        }
    }    
}
