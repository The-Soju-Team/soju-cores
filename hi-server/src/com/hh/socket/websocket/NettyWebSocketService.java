/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.hh.socket.websocket;

//import com.viettel.ipcc.agentserver.websock.server.WebSocketServer;

import com.hh.connector.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class NettyWebSocketService extends Thread implements WebSocketService {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NettyWebSocketService.class.getSimpleName());
    private Map<String, String> keyByUserWiki = new HashMap<String, String>();

    private WebSocketConfig conf;

    private Server server;

    public void setConnector(Server server) {
        this.server = server;
    }

    private void initialize0() throws Exception {
        final SslContext sslCtx = null;
//        if (this.conf.sslEnabled()) {
//            if (this.conf.getCertChainFilename() != null) {
//                // They provided a cert chain filename (and ostensibly a private key)
//                // ssl w/ CA signed certificate.
//                final File certChain = new File(this.conf.getCertChainFilename());
//                final File privKey = new File(this.conf.getPrivateKeyFilename());
//                checkCertChainAndPrivKey(certChain, privKey);
//                sslCtx = SslContext.newServerContext(certChain, privKey);
//            } else {
//                // SSL enabled but no certificate specified, lets use a selfie
//                //this.logger.warn("websocket.ssl.enable = true but websocket.ssl.certChainFile " +
//                //                 "is unspecified, generating a Self Signed Certificate.");
//                SelfSignedCertificate ssc = new SelfSignedCertificate();
//                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
//            }
//        } else {
//            sslCtx = null;
//        }

        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        // get rid of silly lag
        b.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
        b.localAddress(new InetSocketAddress(conf.getPort()));
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new WebSocketServerInitializer(sslCtx, this));

        Channel ch = b.bind().sync().channel();

        ch.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
            public void operationComplete(ChannelFuture f) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }

    @Override
    public void run() {
        try {
            initialize0();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebSocketConfig getConf() {
        return conf;
    }

    public void setConf(WebSocketConfig conf) {
        this.conf = conf;
    }

    private static final class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
        private final SslContext sslCtx;
        private final NettyWebSocketService nwss;

        public WebSocketServerInitializer(SslContext sslCtx, NettyWebSocketService nwss) {
            this.sslCtx = sslCtx;
            this.nwss = nwss;
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new WebSocketServerHandler(this.nwss));
        }
    }

    private static final class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
        private final NettyWebSocketService nwss;
        private WebSocketServerHandshaker handshaker;
        private NettyWebSocket nws;
        private StringBuilder frames;

        public WebSocketServerHandler(NettyWebSocketService nwss) {
            this.nwss = nwss;
        }

        private static void sendHttpResponse(ChannelHandlerContext ctx,
                                             FullHttpRequest req,
                                             FullHttpResponse res) {
            // Generate an error page if response getStatus code is not OK (200).
            if (res.getStatus().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
                res.content().writeBytes(buf);
                buf.release();
                HttpHeaders.setContentLength(res, res.content().readableBytes());
            }

            // Send the response and close the connection if necessary.
            ChannelFuture f = ctx.channel().writeAndFlush(res);
            if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }

        private static String getWebSocketLocation(FullHttpRequest req, boolean ssl) {
            String location = req.headers().get(HttpHeaders.Names.HOST) + "/";
            if (ssl) {
                return "wss://" + location;
            } else {
                return "ws://" + location;
            }
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        private void handleHttpReqB(ChannelHandlerContext ctx,
                                    FullHttpRequest req,
                                    WebSocketRequest wsRequest) {
            WebSocketHandler handler = new BackendWebSocketHandler();

            if (handler == null) {
                ByteBuf content = Unpooled.copiedBuffer(
                        "ERROR: no registered component for path [" + wsRequest.getHandlerName() + "]",
                        StandardCharsets.UTF_8);
                sendHttpResponse(ctx, req,
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                HttpResponseStatus.NOT_FOUND,
                                content));
                return;
            }

            String loc = getWebSocketLocation(req, this.nwss.conf.sslEnabled());
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    loc, null, false, this.nwss.conf.maxFrameSize());
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }

            this.nws = new NettyWebSocket(wsRequest, ctx);

            try {
                handler.onWebSocketConnect(this.nws, wsRequest, this.nwss.server);
            } catch (Exception e) {
                //this.nwss.logger.warn("Exception in {}.onWebSocketConnect()... [{}]",
                //                      handler.getClass().getName(),
                //                      ExceptionUtils.getStackTrace(e));
            }

            ctx.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
                public void operationComplete(ChannelFuture f) {
                    nws.disconnect();
                }
            });
        }

        private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
            // Handle a bad request.
            if (!req.getDecoderResult().isSuccess()) {
                sendHttpResponse(ctx, req,
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                HttpResponseStatus.BAD_REQUEST));
                return;
            }

            // Form: /wiki/handler?k=12345
            WebSocketRequest wsRequest = new NettyWebSocketRequest(req, this.nwss);

            if (req.getMethod() != HttpMethod.GET) {
                log.debug("request method not GET");
            } else {
                // success
                handleHttpReqB(ctx, req, wsRequest);
                return;
            }
            // failure
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.FORBIDDEN));
        }

        private synchronized void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            // Check for closing frame
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            if (frame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            if (!frame.isFinalFragment() || this.frames != null) {
                final String msg;
                if (frame instanceof TextWebSocketFrame) {
                    msg = ((TextWebSocketFrame) frame).text();
                } else if (frame instanceof ContinuationWebSocketFrame) {
                    msg = ((ContinuationWebSocketFrame) frame).text();
                } else {
                    throw new UnsupportedOperationException(
                            "unsupported frame fragment type " + frame.getClass().getName());
                }
                if (this.frames == null) {
                    this.frames = new StringBuilder();
                }
                this.frames.append(msg);
                if (this.frames.length() > this.nwss.conf.maxFrameSize()) {
                    throw new RuntimeException("Frame size too big [" + this.frames.length() +
                            "] max frame size [" + this.nwss.conf.maxFrameSize() + "]");
                }
                if (frame.isFinalFragment()) {
                    final String fullMsg = this.frames.toString();
                    this.frames = null;
                    this.nws.message(fullMsg);
                }
                return;
            }
            if (!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException(
                        String.format("%s frame types not supported", frame.getClass().getName()));
            }

            final String msg = ((TextWebSocketFrame) frame).text();


            this.nws.message(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            //this.nwss.logger.warn("Netty exceptionCaught() [{}]",
            //                      ExceptionUtils.getStackTrace(cause));
            ctx.close();
        }
    }
}
