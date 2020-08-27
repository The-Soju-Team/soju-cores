/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.redis;

import com.hh.util.FileUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hiendm1
 */
public class RedisConnector {
    public static final String DEFAULT_KEY = "default-key";
    public static final String DEFAULT_VALUE = "default-value";
    public static JedisPool jedisPool;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RedisConnector.class.getSimpleName());

    public RedisConnector(String host, int port, int maxTotal, int maxWaitMiliseconds) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMiliseconds);
        jedisPool = new JedisPool(config, host, port);
    }

    public void createStore(String storeId) {
        createStore(storeId, null);
    }

    public void createStore(String storeId, Integer minuteTimeout) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            HashMap<byte[], byte[]> store = new HashMap();
            store.put(DEFAULT_KEY.getBytes(), FileUtils.objectToByteArray(DEFAULT_VALUE));
            js.hmset(storeId.getBytes(), store);
            if (minuteTimeout != null)
                js.expire(storeId.getBytes(), minuteTimeout * 60);
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
    }

    public List<Map> getStore(String storeId) {
        Jedis js = null;
        List<Map> lstResult = new ArrayList();
        try {
            js = jedisPool.getResource();
            List<byte[]> store = js.hvals(storeId.getBytes());
            for (int i = 0; i < store.size(); i++) {
                Object value = FileUtils.byteArrayToObject(store.get(i));
                if (value instanceof Map) lstResult.add((Map) value);
            }
            return lstResult;
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
        return null;
    }

    public Object getStoreAttribute(String storeId, String key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            byte[] byteObject = js.hget(storeId.getBytes(), key.getBytes());
            if (byteObject != null) return FileUtils.byteArrayToObject(byteObject);
            else return null;
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
        return null;
    }

    public void setStoreAttribute(String storeId, String key, Object value) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.hset(storeId.getBytes(), key.getBytes(), FileUtils.objectToByteArray(value));
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
    }

    public void removeStoreAttribute(String storeId, String key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.hdel(storeId.getBytes(), key.getBytes());
        } finally {
            if (js != null) js.close();
        }
    }

    public void removeStore(String storeId) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.del(storeId.getBytes());
        } finally {
            if (js != null) js.close();
        }
    }

    public void refreshExpire(String sessionId, int minuteTimeout) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.expire(sessionId.getBytes(), minuteTimeout * 60);
        } finally {
            if (js != null) js.close();
        }
    }

    public void setAttribute(String key, String value) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.set(key, value);
        } finally {
            if (js != null) js.close();
        }
    }

    public void setAttribute(byte[] key, Object value) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.set(key, FileUtils.objectToByteArray(value));
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
    }

    public String getAttribute(String key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            return js.get(key);
        } finally {
            if (js != null) js.close();
        }
    }

    public Object getAttribute(byte[] key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            if (js.get(key) != null)
                return FileUtils.byteArrayToObject(js.get(key));
            else return null;
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
        return null;
    }

    public void removeAttribute(String key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.del(key);
        } finally {
            if (js != null) js.close();
        }
    }

    public void removeAttribute(byte[] key) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            js.del(key);
        } finally {
            if (js != null) js.close();
        }
    }

    public long incrementAndGet(String key, Long number) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            return js.incrBy(key, number);
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
        return 0;
    }

    public long incrementAndGet(String storeId, String key, Long number) {
        Jedis js = null;
        try {
            js = jedisPool.getResource();
            return js.hincrBy(storeId, key, number);
        } catch (Exception ex) {
            log.error("Redis connector error: ", ex);
        } finally {
            if (js != null) js.close();
        }
        return 0;
    }

}
