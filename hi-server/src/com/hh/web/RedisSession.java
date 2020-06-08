/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.web;

import com.hh.redis.RedisConnector;
import com.hh.server.HHServer;
import com.hh.util.ConfigUtils;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author hiendm1
 */
public class RedisSession extends HttpSession {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RedisSession.class.getSimpleName());
    private static RedisSession session;
    private static RedisConnector connector;
    public static JedisPool jedisPool;
    
    public RedisSession() {
        connector = new RedisConnector(
                HHServer.config.getConfig("redis.host"), 
                Integer.parseInt(HHServer.config.getConfig("redis.port")), 
                Integer.parseInt(HHServer.config.getConfig("redis.max-total")), 
                Integer.parseInt(HHServer.config.getConfig("redis.max-wait-mili")));
    }
        
    public static RedisSession getInstance() {
        if (session == null) session = new RedisSession();
        return session;
    }    
    
    @Override
    public void createSession(String sessionId) {
        connector.createStore(sessionId, sessionTimeout);
    }

    @Override
    public Object getSessionAttribute(String sessionId, String key) {
        return connector.getStoreAttribute(sessionId, key);
    }
    
    @Override
    public void setSessionAttribute(String sessionId, String key, Object value) {
        connector.setStoreAttribute(sessionId, key, value);
    }

    @Override
    public void refreshExpire(String sessionId) {
        connector.refreshExpire(sessionId, sessionTimeout);
    }

    @Override
    public void removeSessionAttribute(String sessionId, String key) {
        connector.removeStoreAttribute(sessionId, key);
    }

    @Override
    public void removeSession(String sessionId) {
        connector.removeStore(sessionId);
    }
}
