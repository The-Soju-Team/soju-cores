/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.run;

import com.google.gson.Gson;
import com.hh.connector.netty.server.ServerFilter;
import com.hh.connector.server.Server;
import com.google.gson.internal.LinkedTreeMap;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HienDM1
 */
public class AuthenFilter implements ServerFilter {
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AuthenFilter.class.getSimpleName());
    public static AtomicInteger messageIds = new AtomicInteger(1);
    public static final long sessionTimeout = 86400000l;

    @Override
    public boolean doFilter(Object msg, Server server) {
        try {
            LinkedTreeMap message = (LinkedTreeMap) msg;

            Object sessionSize = StartApp.hicache.getStoreSize((String) message.get("access-token"));
            if (sessionSize == null) StartApp.hicache.createStore((String) message.get("access-token"), 86400000l);

            if (message.get("hi-process").equals("logout")
                    || message.get("hi-process").equals("login")
                    || message.get("hi-process").equals("get-call-back")
                    || message.get("hi-process").equals("call-back")
                    || message.get("hi-process").equals("call-back-success")
                    || message.get("hi-process").equals("get-captcha"))
                return true;

            String info = StartApp.hicache.getStringAttribute((String) message.get("access-token"), "sso_username");
            if (info != null) {
                return true;
            } else {
                String userInfo = StartApp.hicache.getStringAttribute(ServerProcess.getAuthenCache(), (String) message.get("access-token"), "sso_username");
                if (userInfo != null) {
                    StartApp.hicache.setStoreAttribute((String) message.get("access-token"), "sso_username", userInfo);
                    return true;
                }
            }

            sendCallBackURL(message);

            LinkedTreeMap data = new LinkedTreeMap();
            data.put("response_code", "TIMEOUT");
            ServerProcess.returnStringToFrontend(message, (new Gson().toJson(data)), server);
        } catch (Exception ex) {
            log.error("Error when return to client", ex);
        }
        return false;
    }

    public static void sendCallBackURL(LinkedTreeMap message) {
        String storeName = "login_" + (String) message.get("access-token");
        StartApp.hicache.createStore(storeName, sessionTimeout);
        StartApp.hicache.setStoreAttribute(
                storeName,
                "callback-url",
                StartApp.config.getConfig("authen-callback-url"));
    }
}
