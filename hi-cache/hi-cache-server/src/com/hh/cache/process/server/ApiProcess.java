package com.hh.cache.process.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hh.connector.netty.server.ServerHandler;
import com.hh.connector.process.BaseProcess;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import java.util.UUID;

public class ApiProcess extends BaseProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ApiProcess.class.getSimpleName());
    public ApiProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }

    public void process(LinkedTreeMap obj) throws Exception {
        LinkedTreeMap<String, Object> msg = (LinkedTreeMap) obj;
        msg = executeCommands(msg);
        if(this.ctx != null) ServerHandler.onSender(this.ctx, msg, server);
    }
    
    public static LinkedTreeMap<String, Object> executeCommands(LinkedTreeMap<String, Object> msg) {
        if (ApiManager.API_LOGIN.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().login(
                    (String)msg.get("user-name"), 
                    (String)msg.get("password"), 
                    (String)msg.get("space-name"));
            if(result.length() == UUID.randomUUID().toString().replace("-", "").length()) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            } else {
                msg.put("data", "");
                msg.put("message", result);
            }            
        } 
        else if (ApiManager.API_CREATE_SEQ.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().createSequence(
                    (String)msg.get("space-name"), 
                    (String)msg.get("sequence-name"), 
                    Long.parseLong((String)msg.get("start-with")), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_DELETE_SEQ.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().deleteSequence(
                    (String)msg.get("space-name"), 
                    (String)msg.get("sequence-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_CREATE_SPACE.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().createSpace(
                    (String)msg.get("space-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_CREATE_STORE.equals(msg.get("cmd"))) {
            LinkedTreeMap store = new LinkedTreeMap();
            String strStore = (String)msg.get("store");
            if(strStore != null && !strStore.trim().isEmpty()) {
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                store = gson.fromJson(strStore, LinkedTreeMap.class);
            }
            String result = MemManager.getInstance().createStore(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    store,
                    (String)msg.get("hicache-token"), 
                    Long.parseLong((String)msg.get("timeout")));
            msg.put("message", result);
        }
        else if (ApiManager.API_CREATE_USER.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().createUser(
                    (String)msg.get("user-name"), 
                    (String)msg.get("password"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_DELETE_USER.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().deleteUser(
                    (String)msg.get("user-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }        
        else if (ApiManager.API_DELETE_SPACE.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().deleteSpace(
                    (String)msg.get("space-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_DELETE_STORE.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().deleteStore(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }        
        else if (ApiManager.API_DELETE_STORE_ATB.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().deleteStoreAttribute(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("key"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_GRANT_PERMISSION.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().grantPermission(
                    (String)msg.get("role"), 
                    (String)msg.get("user-name"), 
                    (String)msg.get("space-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_REMOVE_PERMISSION.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().removePermission(
                    (String)msg.get("user-name"), 
                    (String)msg.get("space-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }        
        else if (ApiManager.API_GRANT_ADMIN.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().grantAdmin(
                    (String)msg.get("user-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_REMOVE_ADMIN.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().removeAdmin(
                    (String)msg.get("user-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }        
        else if (ApiManager.API_INCREMENT_GET.equals(msg.get("cmd"))) {
            Object result = MemManager.getInstance().incrementAndGet(
                    (String)msg.get("space-name"), 
                    (String)msg.get("sequence-name"), 
                    Long.parseLong((String)msg.get("nummber")), 
                    (String)msg.get("hicache-token"));
            if(result instanceof Long) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            } else {
                msg.put("message", result);
            }
        }
        else if (ApiManager.API_REFRESH_EXPIRE.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().refreshStoreExpire(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_SET_STORE_ATB.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().setStoreAttribute(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"), 
                    (String)msg.get("key"), 
                    msg.get("value"), 
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_USE_SPACE.equals(msg.get("cmd"))) {
            String result = MemManager.getInstance().useSpace(
                    (String)msg.get("space-name"),
                    (String)msg.get("hicache-token"));
            msg.put("message", result);
        }
        else if (ApiManager.API_GET_SPACE.equals(msg.get("cmd"))) {
            Object result = MemManager.getInstance().getSpace(
                    (String)msg.get("space-name"),
                    (String)msg.get("hicache-token"));
            if(result instanceof LinkedTreeMap) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            } else {
                msg.put("message", result);
            }
        }
        else if (ApiManager.API_GET_SPACE_SIZE.equals(msg.get("cmd"))) {            
            Object result = MemManager.getInstance().getSpaceSize(
                    (String)msg.get("space-name"), 
                    (String)msg.get("hicache-token"));
            if (result instanceof Integer) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            }           
        }                
        else if (ApiManager.API_GET_STORE.equals(msg.get("cmd"))) {
            Long startIndex = null;
            Long size = null;
            if(msg.get("start-index") != null) startIndex = Long.parseLong((String)msg.get("start-index"));
            if(msg.get("size") != null) startIndex = Long.parseLong((String)msg.get("size"));
            
            Object result = MemManager.getInstance().getStore(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"),
                    (String)msg.get("hicache-token"),
                    startIndex,
                    size);
            if (result instanceof LinkedTreeMap) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            } else {
                msg.put("message", result);
            }            
        }
        else if (ApiManager.API_GET_STORE_SIZE.equals(msg.get("cmd"))) {            
            Object result = MemManager.getInstance().getStoreSize(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"),
                    (String)msg.get("hicache-token"));
            if (result instanceof Integer) {
                msg.put("data", result);
                msg.put("message", ApiManager.INFO_EXECUTE_SUCCESS);
            }           
        }        
        else if (ApiManager.API_GET_STORE_ATB.equals(msg.get("cmd"))) {
            Object result = MemManager.getInstance().getStoreAttribute(
                    (String)msg.get("space-name"), 
                    (String)msg.get("store-name"),
                    (String)msg.get("key"),
                    (String)msg.get("hicache-token"));
            if (result instanceof LinkedTreeMap) {
                msg.put("data", ((LinkedTreeMap) result).get("data"));
                msg.put("message", ((LinkedTreeMap) result).get("message"));
            } else {
                msg.put("message", ((LinkedTreeMap) result).get("message"));
            }            
        }
        
        return msg;
    }
}
