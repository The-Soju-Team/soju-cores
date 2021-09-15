package com.hh.connector.netty.client;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.netty.server.NettyServer;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.connector.server.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by HienDM
 */
public class NettyConnection {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger
            .getLogger(NettyConnection.class.getSimpleName());
    public boolean isActive = false;
    public String token;
    public String connector;
    public AtomicLong currentMessage = new AtomicLong(); // ghi lại số bản tin gửi tại một thời điẻm
    public AtomicLong numOfTimeout = new AtomicLong(); // nghi lại tổng số bản tin timeout
    public Server server;
    private Bootstrap bootstrap = new Bootstrap(); // Object trong Netty để khởi tạo kết nối.
    private SocketAddress addr; // Địa chỉ Server kết nối tới.
    private Channel channel; // Đối tượng channel
    // private static IsoBpWrapper msgUtil = IsoBpWrapper.getInstance(); // Công cụ
    // pack unpack bản tin iso
    private Timer timer;
    private Timer retryTimer;
    private Cache<Object, Object> retryMessage;

    public NettyConnection(String host, int port, String ssl, Server server) {
        this(new InetSocketAddress(host, port), ssl, server);
    }

    public NettyConnection(final SocketAddress addr, final String ssl, Server serverConnector) {
        try {
            retryMessage = CacheBuilder.newBuilder().maximumSize(1000000).expireAfterAccess(10, TimeUnit.MINUTES)
                    .build();

            retryTimer = new Timer();
            retryTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    retrySendMessage();
                }
            }, 0, 10000);

            final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            this.addr = addr;
            this.timer = new Timer();
            this.server = serverConnector;
            bootstrap.group(new NioEventLoopGroup()); // khai báo kết nối có một EventLoopGroup
            bootstrap.channel(NioSocketChannel.class); // khai báo kết nối thuộc loại Non Blocking IO
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // caash hình giữ kết nối
            bootstrap.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                /*
                 * Khởi tạo chuỗi Channel Handle gắn với 1 kết nối DataTransferDecoder: Decode
                 * byte nhận được => iso IdleStateHandler: Handle detect khi kết nối rơi vào
                 * trang thái Idle (30s không đọc, không ghi) ClientHandler: Nhận và xử lý ban
                 * tin iso, xử lý khi rơi kết nối idle.
                 */
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    if ("true".equals(ssl)) {
                        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), ((InetSocketAddress) addr).getHostName(),
                                ((InetSocketAddress) addr).getPort()));
                    }
                    ch.pipeline().addLast(new ServerDecoder(), new IdleStateHandler(0L, 0L, 10L, TimeUnit.SECONDS),
                            new ClientHandler(connector, server));
                }
            });

            scheduleConnect(10); // bắt đầu kết nối
        } catch (Exception ex) {
            log.error(" CANT NOT CREATE NETTY CONNECTION ", ex);
        }
    }

    public AtomicLong getNumOfTimeout() {
        return numOfTimeout;
    }

    public void setNumOfTimeout(AtomicLong numOfTimeout) {
        this.numOfTimeout = numOfTimeout;
    }
    /*
     * Hàm gửi bản tin iso
     */

    public LinkedTreeMap send(final LinkedTreeMap msg) throws IOException {
        try {
            if (channel != null && channel.isOpen() && channel.isActive()) {
                ActorRef actor = NettyServer.system
                        .actorOf(Props.create(ClientSender.class, connector, token, channel, addr.toString(), server)
                                .withDispatcher("hh-dispatcher"));
                actor.tell(msg, actor);
                actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
            } else {
                retryMessage.put(UUID.randomUUID().toString(), msg);
            }
        } catch (Exception e) {
            log.error("Error receive from client: ", e);
        }
        return msg;
    }

    public void close() {
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doConnect() {
        final String serverCode = server.config.getConfig("server-code").toUpperCase();
        try {
            numOfTimeout.set(0L); // reset số timeout về 0

            log.info(serverCode + " connect to: " + addr.toString());
            ChannelFuture f = bootstrap.connect(addr); // khỏi tạo kết nối, tuy nhiên do bất đồng bộ bên chưa biết thành
            // công hay không.
            /*
             * Kết quả của kết nối được trả về qua đói tượng ChannelFuture.
             */
            f.addListener(new ChannelFutureListener() {
                @Override
                /*
                 * Callback được gọi khi có kết quả của kết nối
                 */
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) { // kết nối thất bại
                        isActive = false;
                        log.info(serverCode + " reconect fail to " + addr.toString());
                        future.channel().close();
                        Thread.sleep(10000); // Chờ một thời gian trước khi kết nối lại
                        /*
                         * Khởi tạo lại kết nối add callback này cho kết nối mới tạo ra. Nếu kết nối vãn
                         * không thành công => Tiếp lục lặp lại kết nối đến khi nào thành công.
                         */
                        bootstrap.connect(addr).addListener(this);
                    } else {// good, the connection is ok
                        channel = future.channel(); // Lấy ra channel khi kết nối thành công
                        // add a listener to detect the connection lost
                        /**
                         * Xử lý callback trả về khi kết nối bị mất => Gọi kết nối lại
                         */
                        channel.closeFuture().addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                log.info(serverCode + " lost connection to " + addr.toString());
                                scheduleConnect(5);
                            }

                        });
                        isActive = true;
                        log.info(serverCode + " connect to: " + addr.toString() + " OK !");
                    }

                }
            });
        } catch (Exception ex) {
            log.error(serverCode + " Exception at Connect to: " + addr.toString(), ex);
            scheduleConnect(1000);
        }
    }

    private void scheduleConnect(long millis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doConnect();
            }
        }, millis);
    }

    private void retrySendMessage() {
        try {
            log.debug("Retry cache size: " + retryMessage.size());
            if (channel != null && channel.isOpen() && channel.isActive()) {
                Map<Object, Object> mapMessage = retryMessage.asMap();
                for (Map.Entry<Object, Object> entry : mapMessage.entrySet()) {
                    LinkedTreeMap msg = (LinkedTreeMap) entry.getValue();
                    log.debug("===> Retry message: " + entry.getValue());
                    retryMessage.invalidate(entry.getValue());
                    send(msg);
                }
            }
        } catch (Exception ex) {
            log.error("Error when retry send message", ex);
        }
    }

    public void handleMessage(String msg) {

    }

    public AtomicLong getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(AtomicLong currentMessage) {
        this.currentMessage = currentMessage;
    }

    public boolean connectionReady() {
        return (channel != null && channel.isActive());
    }
}