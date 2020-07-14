/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.run;

import com.google.gson.Gson;
import com.hh.connector.process.BaseProcess;
import com.hh.connector.server.Server;
import com.hh.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import com.google.gson.internal.LinkedTreeMap;
import static com.viettel.authen.run.StartApp.config;
import static com.viettel.authen.run.StartApp.hicache;
import com.hh.cache.process.client.HiCacheSession;
import com.hh.util.EncryptDecryptUtils;
import com.viettel.authen.db.daoImpl.AppDaoImpl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author dvgp_admin
 */
public class ServerProcess extends BaseProcess{
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerProcess.class.getSimpleName());
    
    private static String FRONTEND_CONNECTOR;
    private static String AUTHEN_CONNECTOR;
    private static String AUTHEN_CACHE;
    
    public ServerProcess(ChannelHandlerContext ctx, Server server) {       
        super(ctx, server);
    }
    
    public static void returnRedirectToFrontend(LinkedTreeMap request, String pageName, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "redirect");
        response.put("page-name", pageName);
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        response.put("access-token", request.get("access-token"));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }
    
    public void returnRedirectToFrontend(LinkedTreeMap request, String pageName) throws Exception {
        returnRedirectToFrontend(request, pageName, server);
    }

    public static void returnFileToFrontend(LinkedTreeMap request, String filePath, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "file");
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        if (filePath.contains("" + File.separator)) {
            response.put("file-name", filePath.substring(filePath.lastIndexOf("" + File.separator) + 1));
        } else {
            response.put("file-name", filePath);
        }
        response.put("access-token", request.get("access-token"));
        response.put("data", FileUtils.byteArrayToHex(IOUtils.toByteArray(new FileInputStream(filePath))));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }
    
    public static void returnFileToFrontend(LinkedTreeMap request, byte[] data, String fileName, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "file");
        response.put("file-name", fileName);
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        response.put("access-token", request.get("access-token"));
        response.put("data", FileUtils.byteArrayToHex(data));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }
    
    public void returnFileToFrontend(LinkedTreeMap request, String filePath) throws Exception {
        returnFileToFrontend(request, filePath, server);
    }
    
    public void returnFileToFrontend(LinkedTreeMap request, byte[] data, String filePath) throws Exception {
        returnFileToFrontend(request, data, filePath, server);
    }
    
    public static void returnDownloadToFrontend(LinkedTreeMap request, String filePath, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "download");
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        if (filePath.contains("" + File.separator)) {
            response.put("file-name", filePath.substring(filePath.lastIndexOf("" + File.separator) + 1));
        } else {
            response.put("file-name", filePath);
        }
        response.put("access-token", request.get("access-token"));
        response.put("data", FileUtils.byteArrayToHex(IOUtils.toByteArray(new FileInputStream(filePath))));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }

    public void returnDownloadToFrontend(LinkedTreeMap request, String filePath) throws Exception {
        returnFileToFrontend(request, filePath, server);
    }    
    
    public static void returnStringToFrontend(LinkedTreeMap request, String data, Server server) throws Exception  {
        String strUserName = (String)request.get("username");
        request.remove("username");
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        response.put("hi-process", "/return");
        response.put("return-type", "string");
        response.put("access-token", request.get("access-token"));
        if(data == null || data.trim().isEmpty()) data = "{}";
        LinkedTreeMap d = (new Gson()).fromJson(data, LinkedTreeMap.class);
        d.put("sso_user_name", strUserName);
        response.put("data", (new Gson()).toJson(d));
        server.connector.send(response, FRONTEND_CONNECTOR);       
    }
    
    public void returnStringToFrontend(LinkedTreeMap request, String data) throws Exception  {
        returnStringToFrontend(request, data, server);
    }
    
    
    public static void returnDataToFrontend(LinkedTreeMap request, String data, String contentType, Server server) throws Exception  {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "data");
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        response.put("content-type", contentType);
        response.put("access-token", request.get("access-token"));
        response.put("data", FileUtils.byteArrayToHex(data.getBytes(Charset.forName(FileUtils.UTF_8))));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }    
    
    public void returnDataToFrontend(LinkedTreeMap request, String data, String contentType) throws Exception  {
        returnDataToFrontend(request, data, contentType, server);
    }
    
    public Object getSessionAttribute(LinkedTreeMap msg, String key) {
        return StartApp.hicache.getStoreAttribute((String)msg.get("access-token"), key);
    }
    
    public String getSessionStringAttribute(LinkedTreeMap msg, String key) {
        return StartApp.hicache.getStringAttribute("", (String)msg.get("access-token"), key, StartApp.hicache.accessToken);
    }    
    
    public void setSessionAttribute(LinkedTreeMap msg, String key, Object value) {
        StartApp.hicache.setStoreAttribute((String)msg.get("access-token"), key, value);
    }
    
    public void removeSessionAttribute(LinkedTreeMap msg, String key) {
        StartApp.hicache.deleteStoreAttribute((String)msg.get("access-token"), key);
    }  
    
    public static void defaultSetup() throws SQLException {
        Object spaceSize = StartApp.hicache.getSpaceSize("authen");
        if(spaceSize == null) {
            log.info("RESET AUTHEN DATA!");
            StartApp.hicache.createSpace("authen");
            StartApp.hicache.useSpace("authen");
            StartApp.hicache.createStore("credentials");
            StartApp.hicache.createStore("credentials_id");
            StartApp.hicache.createStore("user_ips");
            EncryptDecryptUtils edu = new EncryptDecryptUtils();
            LinkedTreeMap user = new LinkedTreeMap();
            user.put("user_name", "root");
            user.put("password", edu.encodePassword("root"));
            user.put("name", "Administrator");
            user.put("role", "admin");
            StartApp.hicache.setStoreAttribute("credentials", "root", new Gson().toJson(user));
        } else {
            log.info("KEEP AUTHEN DATA!");
        }
        if(StartApp.hicache.getStoreSize("application") == null || 
                StartApp.hicache.getStoreSize("application").toString().equals("0.0")) {
            StartApp.hicache.createStore("application");
            List<Map> apps = (new AppDaoImpl()).getAllApps();
            for(Map app : apps) {
                StartApp.hicache.setStoreAttribute("application", app.get("app_id").toString(), app);
            }
        };
    }

    public static void setHicacheConnector(String connector) {
        hicache = new HiCacheSession(config.getConfig("server-code"));
        HiCacheSession.createCache(hicache.cacheName);
        StartApp.hicache.setConnector(connector, StartApp.server);        
    }
    
    public static void setFrontendConnector(String connector) {
        FRONTEND_CONNECTOR = connector;
    }
    
    public static void setAuthenConnector(String connector) {
        AUTHEN_CONNECTOR = connector;
    }    
    
    public static void setAuthenCache(String authenCache) {
        AUTHEN_CACHE = authenCache;
    }
    
    public static String getFrontendConnector() {
        return FRONTEND_CONNECTOR;
    }
    
    public static String getAuthenConnector() {
        return AUTHEN_CONNECTOR;
    }    
    
    public static String getAuthenCache() {
        return AUTHEN_CACHE;
    }
    
    public static void updateCredentialFromDatabase() {
        try {
            // List<Map> lstUser = StartApp.database.queryData("select user_id, user_name, password, msisdn, full_name, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, email from users ");
            // for(Map user : lstUser) {
            //     int intUserId = Integer.parseInt(user.get("user_id").toString());
            //     StartApp.hicache.setStoreAttribute("credentials_id", "" + intUserId, user.get("user_name"));
            //     StartApp.hicache.setStoreAttribute("credentials", (String)user.get("user_name"), (new Gson()).toJson(user));
            // }

            // log.info("ABCDEF credentials loaded from DB");

            List<Map> allowedIps = StartApp.database.queryData(" select user_name, allowed_ip from user_ip ");
            HashMap<String, ArrayList<String>> userIp = new HashMap<String, ArrayList<String>>();

            for(Map row : allowedIps) {
                ArrayList<String> lstIps = userIp.get((String) row.get("user_name"));
                if (lstIps == null) {
                    lstIps = new ArrayList<String>();
                    userIp.put((String) row.get("user_name"), lstIps);
                }
                lstIps.add((String) row.get("allowed_ip"));
            }

            // log.info("ABCDEF " + (new Gson()).toJson(userIp));

            StartApp.hicache.useSpace("authen");

            StartApp.hicache.deleteStore("user_ips");

            StartApp.hicache.createStore("user_ips");

            for(Entry<String, ArrayList<String>> entry: userIp.entrySet()) {
                // Map m = new HashMap();
                // m.put("ips", entry.getValue());
                StartApp.hicache.setStoreAttribute("user_ips", entry.getKey(), (new Gson()).toJson(entry.getValue()));
            }
            // log.info("ABCDEF IP for donnn:");
            // log.info("ABCDEF hehe");
            // log.info("ABCDEF user_ips" + StartApp.hicache.getStringAttribute("user_ips", "donnn"));
            // log.info("ABCDEF hihi");
            // log.info("ABCDEF credentials" + StartApp.hicache.getStringAttribute("credentials", "donnn"));
            // log.info("/ ABCDEF IP for donnn:");
            
            // log.info("ABCDEF IP loaded from DB");
        } catch (Exception ex) {
            log.error("Error when return to client", ex);
        }   
    }
}
