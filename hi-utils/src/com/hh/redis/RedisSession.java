package com.hh.redis;

import org.apache.log4j.Logger;

import com.hh.constant.Constants;
import com.viettel.bi.ssoutils.logic.redis.RedisUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisSession {
    private static final Logger LOG = Logger.getLogger(RedisSession.class.getSimpleName());
    private String host;
    private int port;

    public RedisSession() {
        this.host = Constants.HOST_REDIS_DEFAULT;
        this.port = Constants.PORT_REDIS_DEFAULT;
    }

    public static class Builder {
        private String host;
        private int port;

        public Builder configHost(String host) {
            this.host = host;
            return this;
        }

        public Builder configPort(int port) {
            this.port = port;
            return this;
        }

        public Jedis build() {
            return new RedisSession().build(this);
        }
    }

    private Jedis build(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        if (!RedisUtils.redisMap.containsKey(this.host + this.port)) {
            try {
                JedisPool redisPool = new JedisPool(this.host, this.port);
                RedisUtils.redisMap.put(this.host + ":" + this.port, redisPool);
                LOG.info(String.format("Setup connection succesfully to Redis Server at :%s:%d", this.host, this.port));
                return RedisUtils.redisMap.get(this.host + ":" + this.port).getResource();
            } catch (Exception e) {
                LOG.warn(String.format("Setup connection fail to Redis Server at :%s:%d ", this.host, this.port));
                e.printStackTrace();
            }
        }
        return RedisUtils.redisMap.get(this.host + this.port).getResource();
    }
}
