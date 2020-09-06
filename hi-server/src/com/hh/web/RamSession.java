/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hiendm1
 */
public class RamSession extends HttpSession {
    public static Cache<Object, Object> cache;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RamSession.class.getSimpleName());
    private static RamSession session;

    public RamSession() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(10000000)
                .expireAfterAccess(sessionTimeout, TimeUnit.MINUTES)
                .build();
    }

    public static RamSession getInstance() {
        if (session == null) session = new RamSession();
        return session;
    }

    @Override
    public void createSession(String sessionId) {
        try {
            HashMap<String, Object> httpSession = new HashMap();
            httpSession.put(SESSION_DEFAULT_KEY, SESSION_DEFAULT_VALUE);
            cache.put(sessionId, httpSession);
        } catch (Exception ex) {
            log.error("RamSession error: ", ex);
        }
    }

    @Override
    public Object getSessionAttribute(String sessionId, String key) {
        try {
            HashMap<String, Object> httpSession = (HashMap<String, Object>) cache.getIfPresent(sessionId);
            if (httpSession != null) return httpSession.get(key);
            else return null;
        } catch (Exception ex) {
            log.error("RamSession error: ", ex);
        }
        return null;
    }

    @Override
    public void setSessionAttribute(String sessionId, String key, Object value) {
        try {
            HashMap<String, Object> httpSession = (HashMap<String, Object>) cache.getIfPresent(sessionId);
            if (httpSession != null) {
                httpSession.put(key, value);
                cache.put(sessionId, httpSession);
            }
        } catch (Exception ex) {
            log.error("RamSession error: ", ex);
        }
    }

    @Override
    public void refreshExpire(String sessionId) {
        cache.getIfPresent(sessionId);
    }

    @Override
    public void removeSessionAttribute(String sessionId, String key) {
        HashMap<String, Object> httpSession = (HashMap<String, Object>) cache.getIfPresent(sessionId);
        if (httpSession != null) {
            httpSession.remove(key);
            cache.put(sessionId, httpSession);
        }
    }

    @Override
    public void removeSession(String sessionId) {
        cache.invalidate(sessionId);
    }
}
