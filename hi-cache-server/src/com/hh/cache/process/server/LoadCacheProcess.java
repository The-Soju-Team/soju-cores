/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hh.cache.run.StartApp;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.internal.LinkedTreeMap;
import static com.hh.cache.process.server.CommitDiskThread.cacheWriter;
import com.hh.util.FileUtils;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 *
 * @author HienDM
 */
public class LoadCacheProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StartApp.class.getSimpleName());
    
    public static void reloadCache() throws Exception {        
        MemManager.maintenanceFlag = true;
        File logFile = new File(StartApp.config.getConfig("data-path") + "/cache.log");
        if(logFile.exists()) {
            Path path = Paths.get(StartApp.config.getConfig("data-path") + "/cache.log");
            FileChannel fileChannel = FileChannel.open(path);
            ByteBuffer buffer = ByteBuffer.allocate(64 * 1024 * 1024);
            int noOfBytesRead = fileChannel.read(buffer);
            while (noOfBytesRead != -1 && noOfBytesRead != 0) {
                buffer.flip();
                while (buffer.remaining() > 4) {
                    buffer.mark();
                    int size = buffer.getInt();
                    if (buffer.remaining() < size) {
                        buffer.reset();
                        buffer.compact();
                        break;
                    }

                    byte[] data = new byte[size];
                    buffer.get(data);

                    // Execute
                    //data = reverseBit(data);
                    String strData = new String(data);
                    strData = strData.substring(0, strData.length() - 1);
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    LinkedTreeMap message = gson.fromJson(strData, LinkedTreeMap.class);
                    adminExecuteCommands(message);                    
                }
                noOfBytesRead = fileChannel.read(buffer);
            }
            fileChannel.close();
            MemManager.maintenanceFlag = false;
        }
    }
    
    private static byte[] reverseBit(byte[] input) {
        for(int i = 0; i < input.length; i++) {
            input[i] = (byte)~input[i];
        }
        return input;
    }
    
    public static void compressCache() throws Exception {
        File cacheFile = new File(StartApp.config.getConfig("data-path") + "/cache.log").getAbsoluteFile();
        if(!cacheFile.exists()) cacheFile.createNewFile();        
        if(cacheWriter == null) {
            cacheWriter = new FileOutputStream(cacheFile, true);
        }          
        if(CommitDiskThread.cacheWriter == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());        
        File tempFile = new File(StartApp.config.getConfig("data-path") + "/cache_" + currentDate + ".bak");
        File zipFile = new File(StartApp.config.getConfig("data-path") + "/cache_" + currentDate + ".bak.zip");
        if(tempFile.exists()) return;
        if(zipFile.exists()) return;
        log.debug("===> compress cache to file <===");
        MemManager.maintenanceFlag = true;
        CommitDiskThread.cacheWriter.close();
        cacheFile.renameTo(tempFile);
        CommitDiskThread.cacheWriter = new FileOutputStream(new File(StartApp.config.getConfig("data-path") + "/cache.log").getAbsoluteFile(), true);
        MemManager.maintenanceFlag = false;
        MemManager.getInstance().compressCacheToFile();
        FileUtils fu = new FileUtils();
        fu.zip(tempFile.getAbsolutePath(), zipFile.getAbsolutePath());
        tempFile.delete();
        log.debug("===> compress cache to file successful <===");
    }
    
    public static void adminExecuteCommands(LinkedTreeMap<String, Object> msg) {
        if(msg == null) return;
        if (ApiManager.API_CREATE_USER.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminCreateUser(
                    (String)msg.get("user-name"), 
                    (String)msg.get("password"));
        } else if (ApiManager.API_DELETE_USER.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminDeleteUser(
                    (String)msg.get("user-name"));
        } else if (ApiManager.API_CREATE_SPACE.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminCreateSpace(
                    (String)msg.get("space-name"));            
        } else if (ApiManager.API_DELETE_SPACE.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminDeleteSpace(
                    (String)msg.get("space-name"));            
        } else if (ApiManager.API_CREATE_SEQ.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminCreateSequence(
                    (String)msg.get("space-name"), 
                    (String)msg.get("sequence-name"), 
                    Long.parseLong((String)msg.get("start-with")));
        } else if (ApiManager.API_DELETE_SEQ.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminDeleteSequence(
                    (String)msg.get("space-name"), 
                    (String)msg.get("sequence-name"));
        } else if (ApiManager.API_CREATE_STORE.equals(msg.get("cmd"))) {
            long timeout = 0;
            if(msg.get("timeout") != null) timeout = Math.round((Double)msg.get("timeout"));
            MemManager.getInstance().adminCreateStore(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    timeout);
        } else if (ApiManager.API_DELETE_STORE.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminDeleteStore(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"));
        } else if (ApiManager.API_SET_STORE_ATB.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminSetStoreAttribute(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("key"), 
                    msg.get("value"));
        } else if (ApiManager.API_DELETE_STORE_ATB.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminDeleteStoreAttribute(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("key"));
        } else if (ApiManager.API_LOGIN.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminLogin(
                    (String)msg.get("access-token"), 
                    (LinkedTreeMap)msg.get("user-info"));
        } else if (ApiManager.API_GRANT_ADMIN.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminGrantAdmin(
                    (String)msg.get("user-name"));
        } else if (ApiManager.API_REMOVE_ADMIN.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminRemoveAdmin(
                    (String)msg.get("user-name"));
        } else if (ApiManager.API_USE_SPACE.equals(msg.get("cmd"))) {
            MemManager.getInstance().useSpace(
                    (String)msg.get("space-name"),
                    (String)msg.get("access-token"));
        } else if (ApiManager.API_GRANT_PERMISSION.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminGrantPermission(
                    (String)msg.get("role"), 
                    (String)msg.get("user-name"), 
                    (String)msg.get("space-name"));                        
        } else if (ApiManager.API_REMOVE_PERMISSION.equals(msg.get("cmd"))) {
            MemManager.getInstance().adminRemovePermission( 
                    (String)msg.get("user-name"), 
                    (String)msg.get("space-name"));                        
        }
    }
    
}
