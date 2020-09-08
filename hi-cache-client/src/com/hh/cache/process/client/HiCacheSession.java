package com.hh.cache.process.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.process.TimerProcess;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import com.hh.util.FileUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

public class HiCacheSession
        implements ApiManager {

    private static Logger log = Logger.getLogger(HiCacheSession.class.getSimpleName());
    public static HashMap<String, Cache<Object, Object>> cacheStore = new HashMap();
    private String connector;
    private AtomicInteger index = new AtomicInteger(1);
    private Server server;
    public String accessToken;
    private String userName;
    private String password;
    private String spaceName;
    public long keepSessionTime = 30000L;
    public long clearRequestTime = 30000L;
    public String cacheName;

    public HiCacheSession(String serverCode) {
        cacheName = serverCode;
        List lstParam = new ArrayList();
        lstParam.add(this);
        TimerProcess timer = new TimerProcess(KeepSessionTask.class, lstParam, Long.valueOf(this.keepSessionTime));
        timer.start();

        List lstParamClear = new ArrayList();
        lstParamClear.add(Long.valueOf(this.clearRequestTime));
        lstParamClear.add(cacheName);
        TimerProcess timerClear = new TimerProcess(ClearTimeoutTask.class, lstParamClear, Long.valueOf(this.clearRequestTime));
        timerClear.start();
    }

    public static void createCache(String serverCode) {
        Cache<Object, Object> cache = CacheBuilder.newBuilder().maximumSize(10000000L).expireAfterAccess(15L, TimeUnit.MINUTES).build();
        cacheStore.put(serverCode, cache);
    }
    
    public static Cache<Object, Object> getCache(String serverCode) {
        return cacheStore.get(serverCode);
    }

    public void setConnector(String connector, Server server) {
        this.connector = connector;
        this.server = server;
    }

    public void connect(String userName, String password, String spaceName) {
        this.userName = userName;
        this.password = password;
        this.spaceName = spaceName;
        login(userName, password);
        Object spaceSize = getSpaceSize(spaceName);
        if (spaceSize != null) {
            useSpace(spaceName);
        }
    }

    public void reConnect() {
        login(this.userName, this.password);
        useSpace(this.spaceName);
    }

    public LinkedTreeMap sendRequest(LinkedTreeMap message) {
        try {
            if ((this.server != null) && (this.server.connector != null)) {
                message.put("hi-process", "/hicache");
                Integer messageId = Integer.valueOf(this.index.incrementAndGet());
                if (messageId.intValue() == 2000000000) {
                    this.index = new AtomicInteger(1);
                }
                message.put("hi-message-id", messageId.toString());
                Object lock = Long.valueOf(new Date().getTime());
                log.info("==========> cache name: " + cacheName);
                cacheStore.get(cacheName).put(messageId.toString(), lock);
                boolean sendResult = this.server.connector.send(message, this.connector);
                if (sendResult) {
                    synchronized (lock) {
                        lock.wait();
                        Object data = cacheStore.get(cacheName).getIfPresent(messageId.toString());
                        if ((data instanceof LinkedTreeMap)) {
                            LinkedTreeMap result = (LinkedTreeMap) cacheStore.get(cacheName).getIfPresent(messageId.toString());
                            if (result != null) {
                                cacheStore.get(cacheName).invalidate(messageId.toString());
                                Config.printClientMessage(this.connector, result, null, false, this.server.config.getConfig("server-code"));
                                return result;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Cache session error: ", ex);
        }
        return null;
    }

    public String login(String userName, String password, String spaceName) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "login");
        message.put("user-name", userName);
        message.put("password", password);
        message.put("space-name", spaceName);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            this.accessToken = ((String) response.get("data"));
            log.info("Login hicache: token: " + this.accessToken + " " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String login(String userName, String password) {
        return login(userName, password, "");
    }

    public String createUser(String userName, String password, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "create-user");
        message.put("user-name", userName);
        message.put("password", password);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String grantPermission(String role, String userName, String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "grant-permission");
        message.put("role", role);
        message.put("user-name", userName);
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String grantAdmin(String userName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "grant-admin");
        message.put("user-name", userName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createSpace(String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "create-space");
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String useSpace(String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "use-space");
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public LinkedTreeMap getSpace(String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "get-space");
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if ((response != null) && (response.get("data") != null)) {
            log.info("hi-cache response: " + response.get("message"));
            return (LinkedTreeMap) response.get("data");
        }
        return null;
    }

    public String deleteSpace(String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "delete-space");
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createStore(String spaceName, String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "create-store");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        if ((store != null) && (!store.isEmpty())) {
            message.put("store", new Gson().toJson(store));
        }
        message.put("hicache-token", accessToken);
        message.put("timeout", "" + timeOutMiliSeconds);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createStore(String spaceName, String storeName, String accessToken) {
        return createStore(spaceName, storeName, new LinkedTreeMap(), accessToken, 0L);
    }

    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex, Long size) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "get-store");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        message.put("hicache-token", accessToken);
        if (startIndex != null) {
            message.put("start-index", startIndex.toString());
        }
        if (size != null) {
            message.put("size", size.toString());
        }
        LinkedTreeMap response = sendRequest(message);
        if ((response != null) && (response.get("data") != null)) {
            log.info("hi-cache response: " + response.get("message"));
            return (LinkedTreeMap) response.get("data");
        }
        return null;
    }

    public Object getStoreSize(String spaceName, String storeName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "get-store-size");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if ((response != null) && (response.get("data") != null)) {
            log.info("hi-cache response: " + response.get("message"));
            return response.get("data");
        }
        return null;
    }

    public Object getSpaceSize(String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "get-space-size");
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if ((response != null) && (response.get("data") != null)) {
            log.info("hi-cache response: " + response.get("message"));
            return response.get("data");
        }
        return null;
    }

    public String deleteStore(String spaceName, String storeName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "delete-store");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String setStoreAttribute(String spaceName, String storeName, String key, Object value, String accessToken) {
        try {
            LinkedTreeMap message = new LinkedTreeMap();
            message.put("cmd", "set-store-atb");
            message.put("space-name", spaceName);
            message.put("store-name", storeName);
            message.put("key", key);
            String data = "";
            if ((value instanceof String)) {
                data = (String) value;
            } else {
                data = FileUtils.byteArrayToHex(FileUtils.objectToByteArray(value));
            }
            message.put("value", data);
            message.put("hicache-token", accessToken);
            LinkedTreeMap response = sendRequest(message);
            if (response != null) {
                log.info("hi-cache response: " + response.get("message"));
                return (String) response.get("message");
            }
            return null;
        } catch (Exception ex) {
            log.error("Error when set data to cache", ex);
        }
        return null;
    }

    public Object getStoreAttribute(String spaceName, String storeName, String key, String accessToken) {
        try {
            LinkedTreeMap message = new LinkedTreeMap();
            message.put("cmd", "get-store-atb");
            message.put("space-name", spaceName);
            message.put("store-name", storeName);
            message.put("key", key);
            message.put("hicache-token", accessToken);
            LinkedTreeMap response = sendRequest(message);
            if ((response != null) && (response.get("data") != null) && ("00|Response successfully!".equals(response.get("message")))) {
                log.info("hi-cache response: " + response.get("message"));
                String hex = (String) response.get("data");
                Object obj = FileUtils.byteArrayToObject(FileUtils.hexToByteArray(hex));
                if ((obj instanceof LinkedHashMap)) {
                    LinkedHashMap data = (LinkedHashMap) obj;
                    LinkedTreeMap result = new LinkedTreeMap();
                    for (Object entry : data.entrySet()) {
                        Map.Entry<Object, Object> item = (Map.Entry) entry;
                        result.put(item.getKey(), item.getValue());
                    }
                    return result;
                }
                return obj;
            }
            return null;
        } catch (Exception ex) {
            log.error("Error when get data from cache", ex);
        }
        return null;
    }

    public String getStringAttribute(String spaceName, String storeName, String key, String accessToken) {
        try {
            LinkedTreeMap message = new LinkedTreeMap();
            message.put("cmd", "get-store-atb");
            message.put("space-name", spaceName);
            message.put("store-name", storeName);
            message.put("key", key);
            message.put("hicache-token", accessToken);
            LinkedTreeMap response = sendRequest(message);
            if ((response != null) && (response.get("data") != null) && ("00|Response successfully!".equals(response.get("message")))) {
                log.info("hi-cache response: " + response.get("message"));
                return (String) response.get("data");
            }
            return null;
        } catch (Exception ex) {
            log.error("Error when get data from cache", ex);
        }
        return null;
    }

    public String getStringAttribute(String spaceName, String storeName, String key) {
        return getStringAttribute(spaceName, storeName, key, this.accessToken);
    }

    public String getStringAttribute(String storeName, String key) {
        return getStringAttribute("", storeName, key, this.accessToken);
    }

    public String deleteStoreAttribute(String spaceName, String storeName, String key, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "delete-store-atb");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        message.put("key", key);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String refreshStoreExpire(String spaceName, String storeName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "refresh-expire");
        message.put("space-name", spaceName);
        message.put("store-name", storeName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createSequence(String spaceName, String sequenceName, long startWith, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "create-sequence");
        message.put("space-name", spaceName);
        message.put("sequence-name", sequenceName);
        message.put("start-with", "" + startWith);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public Object incrementAndGet(String spaceName, String sequenceName, Long number, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "set-store-atb");
        message.put("space-name", spaceName);
        message.put("sequence-name", sequenceName);
        message.put("number", "" + number);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createSpace(String spaceName) {
        return createSpace(spaceName, this.accessToken);
    }

    public Object getSpace(String accessToken) {
        return getSpace("", accessToken);
    }

    public Object getSpace() {
        return getSpace("", this.accessToken);
    }

    public Object getSpaceSize(String spaceName) {
        return getSpaceSize(spaceName, this.accessToken);
    }

    public Object getSpaceSize() {
        return getSpaceSize("", this.accessToken);
    }

    public Object useSpace(String spaceName) {
        return useSpace(spaceName, this.accessToken);
    }

    public String deleteSpace(String spaceName) {
        return deleteSpace(spaceName, this.accessToken);
    }

    public String createStore(String storeName, String accessToken, long timeOutMiliSeconds) {
        return createStore("", storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }

    public String createStore(String storeName, long timeOutMiliSeconds) {
        return createStore("", storeName, new LinkedTreeMap(), this.accessToken, timeOutMiliSeconds);
    }

    public String createStore(String storeName, String accessToken) {
        return createStore("", storeName, accessToken);
    }

    public String createStore(String storeName) {
        return createStore("", storeName, this.accessToken);
    }

    public String createStore(String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds) {
        return createStore("", storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }

    public String createStore(String storeName, LinkedTreeMap store, long timeOutMiliSeconds) {
        return createStore("", storeName, new LinkedTreeMap(), this.accessToken, timeOutMiliSeconds);
    }

    public String createStore(String storeName, LinkedTreeMap store, String accessToken) {
        return createStore("", storeName, accessToken);
    }

    public String createStore(String storeName, LinkedTreeMap store) {
        return createStore("", storeName, this.accessToken);
    }

    public Object getStore(String storeName, String accessToken, Long startIndex, Long size) {
        return getStore(null, storeName, accessToken, startIndex, size);
    }

    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex) {
        return getStore(spaceName, storeName, accessToken, startIndex, null);
    }

    public Object getStore(String spaceName, String storeName, String accessToken) {
        return getStore(spaceName, storeName, accessToken, null, null);
    }

    public Object getStore(String storeName, String accessToken, Long startIndex) {
        return getStore(null, storeName, accessToken, startIndex, null);
    }

    public Object getStore(String storeName, String accessToken) {
        return getStore(null, storeName, accessToken, null, null);
    }

    public Object getStore(String storeName) {
        return getStore(null, storeName, this.accessToken, null, null);
    }

    public Object getStoreSize(String storeName, String accessToken) {
        return getStoreSize(null, storeName, accessToken);
    }

    public Object getStoreSize(String storeName) {
        return getStoreSize(null, storeName, this.accessToken);
    }

    public String deleteStore(String storeName, String accessToken) {
        return deleteStore("", storeName, accessToken);
    }

    public String deleteStore(String storeName) {
        return deleteStore("", storeName, this.accessToken);
    }

    public String setStoreAttribute(String storeName, String key, Object value, String accessToken) {
        return setStoreAttribute("", storeName, key, value, accessToken);
    }

    public String setStoreAttribute(String storeName, String key, Object value) {
        return setStoreAttribute("", storeName, key, value, this.accessToken);
    }

    public Object getStoreAttribute(String storeName, String key, String accessToken) {
        return getStoreAttribute("", storeName, key, accessToken);
    }

    public Object getStoreAttribute(String storeName, String key) {
        return getStoreAttribute("", storeName, key, this.accessToken);
    }

    public String deleteStoreAttribute(String storeName, String key, String accessToken) {
        return deleteStoreAttribute("", storeName, key, accessToken);
    }

    public String deleteStoreAttribute(String storeName, String key) {
        return deleteStoreAttribute("", storeName, key, this.accessToken);
    }

    public String refreshStoreExpire(String storeName, String accessToken) {
        return refreshStoreExpire("", storeName, accessToken);
    }

    public String refreshStoreExpire(String storeName) {
        return refreshStoreExpire("", storeName, this.accessToken);
    }

    public String createSequence(String sequenceName, long startWith, String accessToken) {
        return createSequence("", sequenceName, startWith, accessToken);
    }

    public String createSequence(String sequenceName, long startWith) {
        return createSequence("", sequenceName, startWith, this.accessToken);
    }

    public String deleteSequence(String spaceName, String sequenceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "delete-sequence");
        message.put("space-name", spaceName);
        message.put("sequence-name", sequenceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String deleteSequence(String sequenceName, String accessToken) {
        return deleteSequence("", sequenceName, accessToken);
    }

    public String deleteSequence(String sequenceName) {
        return deleteSequence("", sequenceName, this.accessToken);
    }

    public Object incrementAndGet(String sequenceName, Long number, String accessToken) {
        return incrementAndGet("", sequenceName, number, accessToken);
    }

    public Object incrementAndGet(String sequenceName, Long number) {
        return incrementAndGet("", sequenceName, number, this.accessToken);
    }

    public String deleteUser(String userName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "delete-user");
        message.put("user-name", userName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String removePermission(String userName, String spaceName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "remove-permission");
        message.put("user-name", userName);
        message.put("space-name", spaceName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String removeAdmin(String userName, String accessToken) {
        LinkedTreeMap message = new LinkedTreeMap();
        message.put("cmd", "remove-admin");
        message.put("user-name", userName);
        message.put("hicache-token", accessToken);
        LinkedTreeMap response = sendRequest(message);
        if (response != null) {
            log.info("hi-cache response: " + response.get("message"));
            return (String) response.get("message");
        }
        return null;
    }

    public String createStore(String spaceName, String storeName, String accessToken, long timeOutMiliSeconds) {
        return createStore(spaceName, storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }
}
