package com.hh.cache.process.client;

import com.google.common.cache.Cache;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.process.BaseProcess;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import org.apache.log4j.Logger;

public class HiCacheUpdater
        extends BaseProcess {

    private static Logger log = Logger.getLogger(HiCacheUpdater.class.getSimpleName());

    public HiCacheUpdater(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }

    public void process(LinkedTreeMap message)
            throws Exception {
        String messageId = message.get("hi-message-id").toString();
        String serverCode = server.config.getConfig("server-code");
        Cache<Object, Object> cache = HiCacheSession.cacheStore.get(serverCode);
        Object lock = cache.getIfPresent(messageId);
        cache.put(messageId, message);
        if (lock != null) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
