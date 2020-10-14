package com.hh.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruongNX25
 */

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtils {

    private RedisUtils() {
    }

    protected static Map<String, JedisPool> redisMap = new HashMap<String, JedisPool>();
    private static Logger log = Logger.getLogger(RedisUtils.class.getName());
    // Let's use JedisPool for thread-safe xD
    protected static final String HOST_REDIS_DEFAULT;
    protected static final int PORT_REDIS_DEFAULT;

    static {
        // HOST_REDIS_DEFAULT = "10.58.244.172";
        HOST_REDIS_DEFAULT = "bi172";
        PORT_REDIS_DEFAULT = 9125;
    }

    public static Jedis getDefaultRedisClient() {
        return new RedisSession.Builder().configHost(HOST_REDIS_DEFAULT).configPort(PORT_REDIS_DEFAULT).build();
    }

    public static Jedis getOrCreateRedisClient(String host, int port) {
        return new RedisSession.Builder().configHost(host).configPort(port).build();
    }

    // This function is to use get value of key in DEFAULT BI REDIS SERVER WHICH IS
    // LOCATED AT BI172:6379 (OR in server.conf)
    public static String getRedisValue(String key) {
        log.info(String.format("Getting Redis value with key: %s", key));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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

    public static byte[] getRedisValue(byte[] key) {
        log.info(String.format("Getting Redis value with key: %s", key.toString()));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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

    public static void deleteRedisKey(String key) {
        log.info(String.format("Check exist redis key: %s", key));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
        try {
            redis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
    }

    public static void deleteRedisKey(byte[] key) {
        log.info(String.format("Check exist redis key: %s", key.toString()));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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

    public boolean isKeyExistsRedis(String key) {
        log.info(String.format("Check exist redis key: %s", key));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
        try {
            return redis.exists(key);
        } catch (Exception e) {
            return false;
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

    public static boolean isKeyExistsRedis(byte[] key) {
        log.info(String.format("Check exist redis key: %s", key.toString()));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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

    public static Set<String> getRedisKeyByPattern(String pattern) {
        Set<String> keys = null;
        log.info(String.format("Getting Redis keys with pattern: %s", pattern));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
        try {
            keys = redis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return keys;
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

    public static boolean setRedisKeyValue(String key, String value) {
        log.info(String.format("Setting Redis value with key: %s - value: %s", key, value));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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

    public static boolean setRedisKeyValue(byte[] key, byte[] value) {
        log.info(String.format("Setting Redis value with key: %s - value: %s", key.toString(), value.toString()));
        Jedis redis = getOrCreateRedisClient(HOST_REDIS_DEFAULT, PORT_REDIS_DEFAULT);
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
