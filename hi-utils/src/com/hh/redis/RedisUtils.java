package com.hh.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hh.constant.Constants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtils {

    private RedisUtils() {
    }

    protected static Map<String, JedisPool> redisMap = new HashMap<String, JedisPool>();
    private static Logger log = Logger.getLogger(RedisUtils.class.getName());
    // Let's use JedisPool for thread-safe xD

    public static Jedis getDefaultRedisClient() {
        return new RedisSession.Builder().configHost(Constants.HOST_REDIS_DEFAULT)
                .configPort(Constants.PORT_REDIS_DEFAULT).build();
    }

    public static Jedis getOrCreateRedisClient(String host, int port) {
        return new RedisSession.Builder().configHost(host).configPort(port).build();
    }

    public static String getRedisValue(Jedis redis, String key) {
        log.info(String.format("Getting Redis value with key: %s", key));
        try {
            String temp = redis.get(key);
            log.debug(String.format("Value of key:%s is: %s", key, temp));
            return temp;
        } catch (Exception e) {
            return null;
        } finally {
            redis.close();
        }
    }

    public static byte[] getRedisValue(Jedis redis, byte[] key) {
        log.info(String.format("Getting Redis value with key: %s", key.toString()));
        try {
            byte[] temp = redis.get(key);
            log.debug(String.format("Value of key:%s is: %s", key.toString(), temp.toString()));
            return temp;
        } catch (Exception e) {
            return null;
        } finally {
            redis.close();
        }
    }

    public static void deleteRedisKey(Jedis redis, String key) {
        log.info(String.format("Check exist redis key: %s", key));
        try {
            redis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
    }

    public static void deleteRedisKey(Jedis redis, byte[] key) {
        log.info(String.format("Check exist redis key: %s", key.toString()));
        try {
            redis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
    }

    public boolean isKeyExistsRedis(Jedis redis, String key) {
        log.info(String.format("Check exist redis key: %s", key));
        try {
            return redis.exists(key);
        } catch (Exception e) {
            return false;
        } finally {
            redis.close();
        }
    }

    public static boolean isKeyExistsRedis(Jedis redis, byte[] key) {
        log.info(String.format("Check exist redis key: %s", key.toString()));
        try {
            return redis.exists(key);
        } catch (Exception e) {
            return false;
        } finally {
            redis.close();
        }
    }

    public static Set<String> getRedisKeyByPattern(Jedis redis, String pattern) {
        Set<String> keys = null;
        log.info(String.format("Getting Redis keys with pattern: %s", pattern));
        try {
            keys = redis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return keys;
    }

    public static boolean setRedisKeyValue(Jedis redis, String key, String value) {
        log.info(String.format("Setting Redis value with key: %s - value: %s", key, value));
        try {
            redis.set(key, value);
            log.debug(String.format("Key: %s - Value: %s has been put to redis successfully", key, value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            redis.close();
        }
    }

    public static boolean setRedisKeyValue(Jedis redis, byte[] key, byte[] value) {
        log.info(String.format("Setting Redis value with key: %s - value: %s", key.toString(), value.toString()));
        try {
            redis.set(key, value);
            log.debug(String.format("Key: %s - Value: %s has been put to redis successfully", key.toString(),
                    value.toString()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            redis.close();
        }
    }
}
