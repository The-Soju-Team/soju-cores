/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.web;

import com.hh.server.HHServer;
import java.util.List;
import java.util.Map;


/**
 *
 * @author hiendm1
 */
public abstract class HttpSession {
    public static int sessionTimeout = Integer.parseInt(HHServer.config.getConfig("session-timeout"));
    private static HttpSession session;
    public static final String SESSION_DEFAULT_KEY = "default-key";
    public static final String SESSION_DEFAULT_VALUE = "default-value";    
    
    public static HttpSession getInstance() {
        return session;
    }

    public static void setConnector(HttpSession connector) {
        session = connector;
    }
    
    public abstract void refreshExpire(String sessionId);
    
    public abstract void createSession(String sessionId);
    
    public abstract Object getSessionAttribute(String sessionId, String key);
    
    public abstract void setSessionAttribute(String sessionId, String key, Object value);
    
    public abstract void removeSessionAttribute(String sessionId, String key);
    
    public abstract void removeSession(String sessionId);
    
}
