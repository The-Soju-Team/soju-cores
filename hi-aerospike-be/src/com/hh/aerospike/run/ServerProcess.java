/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.aerospike.run;

import com.google.gson.Gson;
import com.hh.connector.process.BaseProcess;
import com.hh.connector.server.Server;
import com.hh.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import com.google.gson.internal.LinkedTreeMap;
import static com.hh.aerospike.run.StartApp.config;
import static com.hh.aerospike.run.StartApp.hicache;
import com.hh.cache.process.client.HiCacheSession;
import com.hh.util.EncryptDecryptUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author HienDM
 */
public class ServerProcess extends BaseProcess{
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerProcess.class.getSimpleName());
    
    private static String FRONTEND_CONNECTOR;
    private static String AUTHEN_CONNECTOR;
    private static String AUTHEN_CACHE;
    
    public ServerProcess(ChannelHandlerContext ctx, Server server) {       
        super(ctx, server);
    }

    public static void returnRedirectToFrontend(LinkedTreeMap request, String url, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "redirect");
        response.put("url", url);
        response.put("access-token", request.get("access-token"));
        server.connector.send(response, FRONTEND_CONNECTOR);        
    }
    
    public void returnRedirectToFrontend(LinkedTreeMap request, String url) throws Exception {
        returnRedirectToFrontend(request, url, server);
    }

    public static void returnFileToFrontend(LinkedTreeMap request, String filePath, Server server) throws Exception {
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        response.put("hi-process", "/return");
        response.put("return-type", "file");
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
        LinkedTreeMap response = new LinkedTreeMap();
        response.put("hi-message-id", request.get("hi-message-id"));
        response.put("server-code", request.get("server-code"));
        if(request.get("cookie") != null) response.put("cookie", request.get("cookie"));
        response.put("hi-process", "/return");
        response.put("return-type", "string");
        response.put("access-token", request.get("access-token"));
        if(data == null || data.trim().isEmpty()) data = "{}";
        response.put("data", data);
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
    
    public static void defaultSetup() {
        Object spaceSize = StartApp.hicache.getSpaceSize("authen");
        if(spaceSize == null) {
            StartApp.hicache.createSpace("authen");
            StartApp.hicache.useSpace("authen");
            StartApp.hicache.createStore("credentials");
            EncryptDecryptUtils edu = new EncryptDecryptUtils();
            LinkedTreeMap user = new LinkedTreeMap();
            user.put("user_name", "root");
            user.put("password", edu.encodePassword("root"));
            user.put("name", "Administrator");
            user.put("role", "admin");
            StartApp.hicache.setStoreAttribute("credentials", "root", new Gson().toJson(user));
        }
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
    
}
