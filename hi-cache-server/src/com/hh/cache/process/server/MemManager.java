/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.cache.constant.MemManagerConstants;
import com.hh.cache.run.StartApp;
import com.hh.connector.process.TimerProcess;

/**
 * @author hiendm1
 */
public class MemManager implements ApiManager {
    private static final Logger log = org.apache.log4j.Logger.getLogger(MemManager.class.getSimpleName());
    private static MemManager session;
    private static long cacheTimeout = 900000;

    public static LinkedTreeMap<String, LinkedTreeMap> cache = new LinkedTreeMap();
    public static LinkedTreeMap<String, LinkedTreeMap> cacheSession = new LinkedTreeMap();
    private static LinkedTreeMap<String, LinkedTreeMap> permission = new LinkedTreeMap();
    private static LinkedHashSet<String> admins = new LinkedHashSet();
    private static LinkedTreeMap<String, String> credentials = new LinkedTreeMap();
    private static LinkedTreeMap<String, String> credentialsClone = new LinkedTreeMap();

    public static boolean maintenanceFlag = false;
    public static final String TIMEOUT_FIELD = "timeout-265a11090fa5431aa702968e72d12e86";
    public static final String TIMEOUT_VALUE = "timeout-265a11090fa5431aa702968e72d12e87";
    private long CLEAR_TIME = 60000l;

    public MemManager() {
        CommitDiskThread commitThread = new CommitDiskThread();
        commitThread.start();

        TimerProcess timerClear = new TimerProcess(ClearMemTask.class, this.CLEAR_TIME);
        timerClear.start();
    }

    public static MemManager getInstance() {
        if (session == null) {
            session = new MemManager();
        }
        return session;
    }

    private static synchronized void credentialsCRUD(int actionType, String key, String value) {
        switch (actionType) {
        case MemManagerConstants.CREDENTIALS_UPDATE_ACTION:
        case MemManagerConstants.CREDENTIALS_ADD_ACTION:
            credentials.put(key, value);
            break;
        case MemManagerConstants.CREDENTIALS_REMOVE_ACTION:
            credentials.remove(key);
            break;
        case MemManagerConstants.CREDENTIALS_CLONE_ACTION:
            log.info("Cloning Credentials");
            credentialsClone.clear();
            for (Map.Entry<String, String> kv : credentials.entrySet()) {
                credentialsClone.put(kv.getKey(), kv.getValue());
            }
            log.info("Done cloning credentials");
            break;
        default:
            break;
        }
    }

    public static void setCacheTimeout(long timeout) {
        cacheTimeout = timeout;
    }

    @Override
    public String login(String userName, String password, String spaceName) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (password == null) {
            return ERROR_PASSWORD_CANT_NULL;
        }
        String sha256hex = Hashing.sha256()
                .hashString(password + "kook", StandardCharsets.UTF_8)
                .toString();
        if (sha256hex != null && sha256hex.equals(credentials.get(userName))) {
            String accessToken = UUID.randomUUID().toString().replace("-", "");
            LinkedTreeMap userInfo = new LinkedTreeMap();
            userInfo.put("user-name", userName);
            if (spaceName != null && !spaceName.trim().isEmpty()) {
                if (!cache.containsKey(spaceName)) {
                    return ERROR_CANT_FIND_SPACE + spaceName + "!";
                }
                if (permission.get(userName).containsKey(spaceName)) {
                    userInfo.put("space", spaceName);
                } else {
                    return ERROR_PERMISSION_DENIED;
                }
            } else {
                if (permission.get(userName) != null && permission.get(userName).size() == 1) {
                    userInfo.put("space", permission.get(userName).keySet().iterator().next());
                }
            }
            this.adminLogin(accessToken, userInfo);
            return accessToken;
        } else {
            return ERROR_LOGIN_FAILED;
        }
    }

    public void adminLogin(String accessToken, LinkedTreeMap userInfo) {
        userInfo.put(TIMEOUT_FIELD, new Date().getTime() + cacheTimeout);
        cacheSession.put(accessToken, userInfo);
        this.commitCacheSession(accessToken, userInfo);
    }

    @Override
    public String login(String userName, String password) {
        return this.login(userName, password, null);
    }

    @Override
    public String createUser(String userName, String password, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (password == null) {
            return ERROR_PASSWORD_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (credentials.containsKey(userName)) {
                return String.format(ERROR_USER_EXISTED, userName);
            }
            String sha256hex = Hashing.sha256()
                    .hashString(password + "kook", StandardCharsets.UTF_8)
                    .toString();
            this.adminCreateUser(userName, sha256hex);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminCreateUser(String userName, String password) {
        credentials.put(userName, password);
        this.commitUser(userName, password);
    }

    @Override
    public String deleteUser(String userName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (credentials.containsKey(userName)) {
                return String.format(ERROR_USER_EXISTED, userName);
            }
            this.adminDeleteUser(userName);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminDeleteUser(String userName) {
        credentials.remove(userName);
        this.commitDeleteUser(userName);
    }

    @Override
    public String grantPermission(String role, String userName, String spaceName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (role == null) {
            return ERROR_ROLE_CANT_NULL;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (spaceName == null) {
            return ERROR_SPACE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        String roleUpper = role.toUpperCase();
        if (admins.contains(executor)) {
            if (!credentials.containsKey(userName)) {
                return ERROR_CANT_FIND_USER + userName + "!";
            }
            if (!cache.containsKey(spaceName)) {
                return ERROR_CANT_FIND_SPACE + spaceName + "!";
            }
            LinkedTreeMap userPermission = permission.get(userName);
            if (userPermission == null) {
                userPermission = new LinkedTreeMap();
            }
            if (ROLE_OWNER.equals(roleUpper) || ROLE_VIEWER.equals(roleUpper)) {
                this.adminGrantPermission(role, userName, spaceName);
                return INFO_EXECUTE_SUCCESS;
            } else {
                return ERROR_CANT_FIND_ROLE + role + "!";
            }
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminGrantPermission(String role, String userName, String spaceName) {
        LinkedTreeMap userPermission = permission.get(userName);
        if (userPermission == null) {
            userPermission = new LinkedTreeMap();
        }
        userPermission.put(spaceName, role);
        permission.put(userName, userPermission);
        this.commitGrantPermission(userName, spaceName, role);
    }

    @Override
    public String removePermission(String userName, String spaceName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (spaceName == null) {
            return ERROR_SPACE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (!credentials.containsKey(userName)) {
                return ERROR_CANT_FIND_USER + userName + "!";
            }
            if (!cache.containsKey(spaceName)) {
                return ERROR_CANT_FIND_SPACE + spaceName + "!";
            }
            LinkedTreeMap userPermission = permission.get(userName);
            if (userPermission == null) {
                userPermission = new LinkedTreeMap();
            }
            this.adminRemovePermission(userName, spaceName);
            return INFO_EXECUTE_SUCCESS;

        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminRemovePermission(String userName, String spaceName) {
        LinkedTreeMap userPermission = permission.get(userName);
        if (userPermission == null) {
            userPermission = new LinkedTreeMap();
        }
        userPermission.remove(spaceName);
        this.commitRemovePermission(userName, spaceName);
    }

    @Override
    public String grantAdmin(String userName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (!credentials.containsKey(userName)) {
                return ERROR_CANT_FIND_USER + userName + "!";
            }
            this.adminGrantAdmin(userName);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminGrantAdmin(String userName) {
        admins.add(userName);
        this.commitGrantAdmin(userName);
    }

    @Override
    public String removeAdmin(String userName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (userName == null) {
            return ERROR_USER_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (!credentials.containsKey(userName)) {
                return ERROR_CANT_FIND_USER + userName + "!";
            }
            this.adminRemoveAdmin(userName);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminRemoveAdmin(String userName) {
        admins.remove(userName);
        this.commitRemoveAdmin(userName);
    }

    @Override
    public String createSpace(String spaceName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (spaceName == null) {
            return ERROR_SPACE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (cache.containsKey(spaceName)) {
                return String.format(ERROR_SPACE_EXISTED, spaceName);
            }
            this.adminCreateSpace(spaceName);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminCreateSpace(String spaceName) {
        cache.put(spaceName, new LinkedTreeMap());
        this.commitSpace(spaceName);
    }

    @Override
    public String useSpace(String spaceName, String accessToken) {
        if (spaceName == null) {
            return ERROR_SPACE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        if (!cache.containsKey(spaceName)) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        LinkedTreeMap userInfo = cacheSession.get(accessToken);
        userInfo.put("space", spaceName);
        this.commitUseSpace(spaceName, accessToken);
        return INFO_EXECUTE_SUCCESS;
    }

    @Override
    public Object getSpace(String spaceName, String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) == null) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        return cache.get(spaceName);
    }

    @Override
    public Object getSpaceSize(String spaceName, String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        if (cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (executor != null) {
            cacheSession.get(accessToken).put(TIMEOUT_FIELD, new Date().getTime() + this.CLEAR_TIME);
        }
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) == null) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            log.debug("GET SPACE SIZE [User: " + executor + " - space: " + spaceName + " - size: " + space.size() + "]");
        }
        if (space == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        } else {
            return space.size();
        }
    }

    @Override
    public Object getSpaceSize(String accessToken) {
        return this.getSpaceSize(null, accessToken);
    }

    @Override
    public String deleteSpace(String spaceName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (spaceName == null) {
            return ERROR_SPACE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (admins.contains(executor)) {
            if (!cache.containsKey(spaceName)) {
                return ERROR_CANT_FIND_SPACE + spaceName + "!";
            }
            this.adminDeleteSpace(spaceName);
            return INFO_EXECUTE_SUCCESS;
        } else {
            return ERROR_PERMISSION_DENIED;
        }
    }

    public void adminDeleteSpace(String spaceName) {
        cache.remove(spaceName);
        this.commitDeleteSpace(spaceName);
    }

    @Override
    public String createStore(String spaceName, String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        //        if(space.containsKey(storeName)) return String.format(ERROR_STORE_EXISTED, storeName);
        this.adminCreateStore(spaceName, storeName, timeOutMiliSeconds);
        return INFO_EXECUTE_SUCCESS;
    }

    public void adminCreateStore(String spaceName, String storeName, long timeOutMiliSeconds) {
        this.adminCreateStore(spaceName, storeName, new LinkedTreeMap(), timeOutMiliSeconds);
    }

    public void adminCreateStore(String spaceName, String storeName, LinkedTreeMap store, long timeOutMiliSeconds) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            if (timeOutMiliSeconds > 0) {
                long currentTime = new Date().getTime();
                long putTime = timeOutMiliSeconds + currentTime;
                store.put(TIMEOUT_FIELD, putTime);
                store.put(TIMEOUT_VALUE, timeOutMiliSeconds);
            }
            space.put(storeName, store);
            this.commitStore(spaceName, storeName, store);
        }
    }

    @Override
    public String createStore(String spaceName, String storeName, String accessToken) {
        return this.createStore(spaceName, storeName, new LinkedTreeMap(), accessToken, 0);
    }

    @Override
    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex, Long size) {
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) == null) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        Object store = space.get(storeName);
        if (store == null) {
            return null;
        }
        if (store instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                if (new Date().getTime() > (Long) timeout) {
                    return ERROR_CANT_FIND_STORE + storeName + "!";
                }
                ((LinkedTreeMap) store).put(TIMEOUT_FIELD, new Date().getTime() + (Long) ((LinkedTreeMap) store).get(TIMEOUT_VALUE));
            }
            if (startIndex != null && size != null) {
                long count = 0;
                LinkedTreeMap data = new LinkedTreeMap();
                Map.Entry<Object, Object> item = null;
                for (Object entry : ((LinkedTreeMap) store).entrySet()) {
                    item = (Map.Entry<Object, Object>) entry;
                    if (count >= startIndex && count < startIndex + size) {
                        data.put(item.getKey(), item.getValue());
                    } else if (count >= startIndex + size) {
                        break;
                    }
                    count++;
                }
                return data;
            } else if (startIndex != null) {
                long count = 0;
                LinkedTreeMap data = new LinkedTreeMap();
                Map.Entry<Object, Object> item =  null;
                for (Object entry : ((LinkedTreeMap) store).entrySet()) {
                    item = (Map.Entry<Object, Object>) entry;
                    if (count >= startIndex) {
                        data.put(item.getKey(), item.getValue());
                    }
                    count++;
                }
                return data;
            } else {
                return store;
            }
        } else {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
    }

    @Override
    public Object getStoreSize(String spaceName, String storeName, String accessToken) {
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) == null) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        Object store = space.get(storeName);
        if (store == null) {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
        if (store instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                if (new Date().getTime() > (Long) timeout) {
                    return ERROR_CANT_FIND_STORE + storeName + "!";
                }
                ((LinkedTreeMap) store).put(TIMEOUT_FIELD, new Date().getTime() + (Long) ((LinkedTreeMap) store).get(TIMEOUT_VALUE));
            }
            return ((LinkedTreeMap) store).size();
        } else {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
    }

    @Override
    public String deleteStore(String spaceName, String storeName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(storeName)) {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        } else {
            Object store = space.get(storeName);
            if (store instanceof LinkedTreeMap) {
                Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
                if (timeout != null && timeout instanceof Long) {
                    if (new Date().getTime() > (Long) timeout) {
                        return ERROR_CANT_FIND_STORE + storeName + "!";
                    }
                }
            } else {
                return ERROR_CANT_FIND_STORE + storeName + "!";
            }
        }
        this.adminDeleteStore(spaceName, storeName);
        return INFO_EXECUTE_SUCCESS;
    }

    public void adminDeleteStore(String spaceName, String storeName) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            space.remove(storeName);
            this.commitDeleteStore(spaceName, storeName);
        }
    }

    @Override
    public String deleteSequence(String spaceName, String sequenceName, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (sequenceName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(sequenceName)) {
            return ERROR_CANT_FIND_SEQUENCE + sequenceName + "!";
        } else {
            Object sequence = space.get(sequenceName);
            if (!(sequence instanceof Long)) {
                return ERROR_CANT_FIND_SEQUENCE + sequenceName + "!";
            }
        }
        this.adminDeleteSequence(spaceName, sequenceName);
        return INFO_EXECUTE_SUCCESS;
    }

    @Override
    public String deleteSequence(String sequenceName, String accessToken) {
        return this.deleteSequence(null, sequenceName, accessToken);
    }

    public void adminDeleteSequence(String spaceName, String sequenceName) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            space.remove(sequenceName);
            this.commitDeleteSequence(spaceName, sequenceName);
        }
    }

    @Override
    public String setStoreAttribute(String spaceName, String storeName, String key, Object value, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(storeName)) {
            return ERROR_CANT_FIND_STORE + spaceName + "!";
        }
        Object store = space.get(storeName);
        if (store instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                if (new Date().getTime() > (Long) timeout) {
                    return ERROR_CANT_FIND_STORE + storeName + "!";
                }
                ((LinkedTreeMap) store).put(TIMEOUT_FIELD, new Date().getTime() + (Long) ((LinkedTreeMap) store).get(TIMEOUT_VALUE));
            }
            this.adminSetStoreAttribute(spaceName, storeName, key, value);
        } else {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
        return INFO_EXECUTE_SUCCESS;
    }

    public void adminSetStoreAttribute(String spaceName, String storeName, String key, Object value) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            Object store = space.get(storeName);
            if (store instanceof LinkedTreeMap) {
                ((LinkedTreeMap) store).put(key, value);
            }
            this.commitStoreAttribute(spaceName, storeName, key, value);
        }
    }

    @Override
    public LinkedTreeMap getStoreAttribute(String spaceName, String storeName, String key, String accessToken) {
        LinkedTreeMap result = new LinkedTreeMap();
        if (storeName == null) {
            result.put("message", ERROR_STORE_CANT_NULL);
            return result;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            result.put("message", ERROR_PERMISSION_DENIED);
            return result;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            result.put("message", ERROR_CANT_FIND_SPACE + spaceName + "!");
            return result;
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) == null) {
                result.put("message", ERROR_PERMISSION_DENIED);
                return result;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(storeName)) {
            result.put("message", ERROR_CANT_FIND_STORE + storeName + "!");
            return result;
        }
        Object store = space.get(storeName);
        if (store instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                if (new Date().getTime() > (Long) timeout) {
                    result.put("message", ERROR_CANT_FIND_STORE + storeName + "!");
                    return result;
                }
                ((LinkedTreeMap) store).put(TIMEOUT_FIELD, new Date().getTime() + (Long) ((LinkedTreeMap) store).get(TIMEOUT_VALUE));
            }
            result.put("message", INFO_EXECUTE_SUCCESS);
            result.put("data", ((LinkedTreeMap) store).get(key));
            return result;
        } else {
            result.put("message", ERROR_CANT_FIND_STORE + storeName + "!");
            return result;
        }
    }

    @Override
    public String deleteStoreAttribute(String spaceName, String storeName, String key, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(storeName)) {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
        Object store = space.get(storeName);
        if (store instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) store).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                if (new Date().getTime() > (Long) timeout) {
                    return ERROR_CANT_FIND_STORE + storeName + "!";
                }
            }
            this.adminDeleteStoreAttribute(spaceName, storeName, key);
        } else {
            return ERROR_CANT_FIND_STORE + storeName + "!";
        }
        return INFO_EXECUTE_SUCCESS;
    }

    public void adminDeleteStoreAttribute(String spaceName, String storeName, String key) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            Object store = space.get(storeName);
            if (store instanceof LinkedTreeMap) {
                ((LinkedTreeMap) store).remove(key);
            }
            this.commitDeleteStoreAttribute(spaceName, storeName, key);
        }
    }

    @Override
    public String refreshStoreExpire(String spaceName, String storeName, String accessToken) {
        if (storeName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || permission.get(executor).get(spaceName) != null) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        this.getStoreSize(spaceName, storeName, accessToken);
        return INFO_EXECUTE_SUCCESS;
    }

    @Override
    public String createSequence(String spaceName, String sequenceName, long startWith, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (sequenceName == null) {
            return ERROR_STORE_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (space.containsKey(sequenceName)) {
            return String.format(ERROR_STORE_EXISTED, sequenceName);
        }
        this.adminCreateSequence(spaceName, sequenceName, startWith);
        return INFO_EXECUTE_SUCCESS;
    }

    public void adminCreateSequence(String spaceName, String sequenceName, long startWith) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space != null) {
            space.put(sequenceName, startWith);
        }
    }

    @Override
    public Object incrementAndGet(String spaceName, String sequenceName, Long number, String accessToken) {
        if (maintenanceFlag) {
            return ERROR_SYSTEM_MAINTENANCE;
        }
        if (sequenceName == null) {
            return ERROR_SEQ_CANT_NULL;
        }
        if (number == null) {
            return ERROR_INCREMENT_CANT_NULL;
        }
        if (accessToken == null || accessToken.trim().isEmpty() || cacheSession.get(accessToken) == null) {
            return ERROR_PERMISSION_DENIED;
        }
        String executor = (String) cacheSession.get(accessToken).get("user-name");
        if (spaceName == null || spaceName.trim().isEmpty()) {
            spaceName = (String) cacheSession.get(accessToken).get("space");
        }
        if (spaceName == null) {
            return ERROR_CANT_FIND_SPACE + spaceName + "!";
        }
        if (!admins.contains(executor)) {
            if (permission.get(executor) == null || !ROLE_OWNER.equals(permission.get(executor).get(spaceName))) {
                return ERROR_PERMISSION_DENIED;
            }
        }
        LinkedTreeMap space = cache.get(spaceName);
        if (!space.containsKey(sequenceName)) {
            return ERROR_CANT_FIND_SEQ + sequenceName + "!";
        }
        Object seqValue = space.get(sequenceName);
        if (seqValue instanceof Long) {
            return this.adminIncrementAndGet(spaceName, sequenceName, number);
        } else {
            return ERROR_CANT_FIND_SEQ + sequenceName + "!";
        }
    }

    public Object adminIncrementAndGet(String spaceName, String sequenceName, Long number) {
        LinkedTreeMap space = cache.get(spaceName);
        if (space == null) {
            return null;
        }
        Object seqValue = space.get(sequenceName);
        if (seqValue instanceof Long) {
            synchronized (seqValue) {
                seqValue = (Long) seqValue + number;
                space.put(sequenceName, seqValue);
            }
        }
        return seqValue;
    }

    private void commitUser(String userName, String password) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_CREATE_USER);
        command.put("user-name", userName);
        command.put("password", password);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitDeleteUser(String userName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_DELETE_USER);
        command.put("user-name", userName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitSpace(String spaceName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_CREATE_SPACE);
        command.put("space-name", spaceName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitDeleteSpace(String spaceName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_DELETE_SPACE);
        command.put("space-name", spaceName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitUseSpace(String spaceName, String accessToken) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_USE_SPACE);
        command.put("space-name", spaceName);
        command.put("access-token", accessToken);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitSequence(String spaceName, String sequenceName, Long startWith) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_CREATE_SEQ);
        command.put("space-name", spaceName);
        command.put("sequence-name", sequenceName);
        command.put("start-with", startWith);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitDeleteSequence(String spaceName, String sequenceName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_DELETE_SEQ);
        command.put("space-name", spaceName);
        command.put("sequence-name", sequenceName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitStore(String spaceName, String storeName, Object value) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_CREATE_STORE);
        command.put("space-name", spaceName);
        command.put("store-name", storeName);
        //set time out
        boolean isExpire = false;
        if (value instanceof LinkedTreeMap) {
            Object timeout = ((LinkedTreeMap) value).get(TIMEOUT_FIELD);
            if (timeout != null && timeout instanceof Long) {
                long currentTime = new Date().getTime();
                long putTime = (long) timeout - currentTime;
                if (currentTime < (Long) timeout) {
                    command.put("timeout", putTime);
                } else {
                    isExpire = true;
                }
            }
        }
        if (!isExpire) {
            CommitDiskThread.append(gson.toJson(command));
        }
    }

    private void commitDeleteStore(String spaceName, String storeName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_DELETE_STORE);
        command.put("space-name", spaceName);
        command.put("store-name", storeName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitStoreAttribute(String spaceName, String storeName, Object key, Object value) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_SET_STORE_ATB);
        command.put("space-name", spaceName);
        command.put("store-name", storeName);
        command.put("key", key);
        command.put("value", value);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitDeleteStoreAttribute(String spaceName, String storeName, Object key) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_DELETE_STORE_ATB);
        command.put("space-name", spaceName);
        command.put("store-name", storeName);
        command.put("key", key);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitCacheSession(String accessToken, Object userInfo) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_LOGIN);
        command.put("access-token", accessToken);
        command.put("user-info", userInfo);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitGrantAdmin(String userName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_GRANT_ADMIN);
        command.put("user-name", userName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitRemoveAdmin(String userName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_REMOVE_ADMIN);
        command.put("user-name", userName);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitGrantPermission(String userName, String spaceName, String role) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_GRANT_PERMISSION);
        command.put("user-name", userName);
        command.put("space-name", spaceName);
        command.put("role", role);
        CommitDiskThread.append(gson.toJson(command));
    }

    private void commitRemovePermission(String userName, String spaceName) {
        if (MemManager.maintenanceFlag) {
            return;
        }
        Gson gson = new Gson();
        LinkedTreeMap command = new LinkedTreeMap();
        command.put("cmd", API_REMOVE_PERMISSION);
        command.put("user-name", userName);
        command.put("space-name", spaceName);
        CommitDiskThread.append(gson.toJson(command));
    }

    public void compressCacheToFile() {
        ClearMemTask.clearMem();

        //create credential
        for (Map.Entry<String, String> entry : credentials.entrySet()) {
            this.commitUser(entry.getKey(), entry.getValue());
        }

        //create cache
        for (Map.Entry<String, LinkedTreeMap> entry : cache.entrySet()) {
            this.commitSpace(entry.getKey());
            LinkedTreeMap space = entry.getValue();
            Map.Entry<String, Object> item = null;
            for (Object entry1 : space.entrySet()) {
                item = (Map.Entry<String, Object>) entry1;
                if (item.getValue() instanceof Long) {
                    this.commitSequence(entry.getKey(), item.getKey(), (Long) item.getValue());
                } else if (item.getValue() instanceof LinkedTreeMap) {
                    this.commitStore(entry.getKey(), item.getKey(), item.getValue());
                    LinkedTreeMap store = (LinkedTreeMap) item.getValue();
                    Map.Entry<Object, Object> storeItem = null;
                    for (Object entry2 : store.entrySet()) {
                        storeItem = (Map.Entry<Object, Object>) entry2;
                        this.commitStoreAttribute(entry.getKey(), item.getKey(), storeItem.getKey(), storeItem.getValue());
                    }
                }
            }
        }

        //create cache session
        for (Map.Entry<String, LinkedTreeMap> entry : cacheSession.entrySet()) {
            this.commitCacheSession(entry.getKey(), entry.getValue());
        }

        //create admins
        for (String entry : admins) {
            this.commitGrantAdmin(entry);
        }

        //create permission
        for (Map.Entry<String, LinkedTreeMap> entry : permission.entrySet()) {
            LinkedTreeMap roleSpace = entry.getValue();
            for (Object entry1 : roleSpace.entrySet()) {
                Map.Entry<String, String> item = (Map.Entry<String, String>) entry1;
                this.commitGrantPermission(entry.getKey(), item.getKey(), item.getValue());
            }
        }
    }

    @Override
    public Object getSpace(String accessToken) {
        return this.getSpace(null, accessToken);
    }

    @Override
    public String createStore(String storeName, String accessToken, long timeOutMiliSeconds) {
        return this.createStore(null, storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }

    @Override
    public String createStore(String storeName, String accessToken) {
        return this.createStore(null, storeName, accessToken);
    }

    @Override
    public Object getStore(String storeName, String accessToken, Long startIndex, Long size) {
        return this.getStore(null, storeName, accessToken, startIndex, size);
    }

    @Override
    public Object getStore(String storeName, String accessToken, Long startIndex) {
        return this.getStore(null, storeName, accessToken, startIndex, null);
    }

    @Override
    public Object getStore(String storeName, String accessToken) {
        return this.getStore(null, storeName, accessToken, null, null);
    }

    @Override
    public Object getStoreSize(String storeName, String accessToken) {
        return this.getStoreSize(null, storeName, accessToken);
    }

    @Override
    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex) {
        return this.getStore(spaceName, storeName, accessToken, startIndex, null);
    }

    @Override
    public Object getStore(String spaceName, String storeName, String accessToken) {
        return this.getStore(spaceName, storeName, accessToken, null, null);
    }

    @Override
    public String deleteStore(String storeName, String accessToken) {
        return this.deleteStore(null, storeName, accessToken);
    }

    @Override
    public String setStoreAttribute(String storeName, String key, Object value, String accessToken) {
        return this.setStoreAttribute(null, storeName, key, value, accessToken);
    }

    @Override
    public LinkedTreeMap getStoreAttribute(String storeName, String key, String accessToken) {
        return this.getStoreAttribute(null, storeName, key, accessToken);
    }

    @Override
    public String deleteStoreAttribute(String storeName, String key, String accessToken) {
        return this.deleteStoreAttribute(null, storeName, key, accessToken);
    }

    @Override
    public String refreshStoreExpire(String storeName, String accessToken) {
        return this.refreshStoreExpire(null, storeName, accessToken);
    }

    @Override
    public String createSequence(String sequenceName, long startWith, String accessToken) {
        return this.createSequence(null, sequenceName, startWith, accessToken);
    }

    @Override
    public Object incrementAndGet(String sequenceName, Long number, String accessToken) {
        return this.incrementAndGet(null, sequenceName, number, accessToken);
    }

    public void loadDefaultSetup() {
        String rootPassword = credentials.get("root");
        if (rootPassword == null) {
            String sha256hex = Hashing.sha256()
                    .hashString("root" + "kook", StandardCharsets.UTF_8)
                    .toString();
            this.adminCreateUser("root", sha256hex);
            this.adminGrantAdmin("root");
            this.login("root", "root");
        }
    }

    @Override
    public String createStore(String spaceName, String storeName, String accessToken, long timeOutMiliSeconds) {
        return this.createStore(spaceName, storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }

    @Override
    public String createStore(String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds) {
        return this.createStore(null, storeName, new LinkedTreeMap(), accessToken, timeOutMiliSeconds);
    }

    @Override
    public String createStore(String storeName, LinkedTreeMap store, String accessToken) {
        return this.createStore(null, storeName, new LinkedTreeMap(), accessToken, 0);
    }

    public static void commitCacheToDb() throws SQLException {
        //create credential
        List lstParam = new ArrayList();
        // credentialsCRUD(MemManagerConstants.CREDENTIALS_CLONE_ACTION, "", "");
        // At this point
        List lstRow = null;
        for (Map.Entry<String, String> entry : MemManager.credentials.entrySet()) {
            lstRow = new ArrayList();
            lstRow.add(entry.getKey());
            lstRow.add(entry.getValue());
            lstParam.add(lstRow);
        }
        if (!lstParam.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_credential");
            StartApp.db.executeQueryBatch("insert into hi_credential (user_name, password) values (?,?)", lstParam);
        }

        //create cache
        List lstParamSpace = new ArrayList();
        List lstParamStore = new ArrayList();
        List lstParamSequence = new ArrayList();
        List lstParamItem = new ArrayList();

        List lstRowSpace = null;
        LinkedTreeMap space = null;
        List lstRowSequence = null;
        List lstRowStore = null;
        LinkedTreeMap store = null;
        Map.Entry<Object, Object> storeItem = null;
        List lstRowItem = null;
        for (Map.Entry<String, LinkedTreeMap> entry : cache.entrySet()) {
            lstRowSpace = new ArrayList();
            lstRowSpace.add(entry.getKey());
            space = entry.getValue();
            lstRowSpace.add(space.size());
            lstParamSpace.add(lstRowSpace);
            for (Object entry1 : space.entrySet()) {
                Map.Entry<String, Object> item = (Map.Entry<String, Object>) entry1;
                if (item.getValue() instanceof Long) {
                    lstRowSequence = new ArrayList();
                    lstRowSequence.add(item.getKey());
                    lstRowSequence.add(item.getValue());
                    lstRowSequence.add(entry.getKey());
                    lstParamSequence.add(lstRowSequence);
                } else if (item.getValue() instanceof LinkedTreeMap) {
                    lstRowStore = new ArrayList();
                    lstRowStore.add(item.getKey());
                    store = (LinkedTreeMap) item.getValue();
                    lstRowStore.add(store.size());
                    lstRowStore.add(entry.getKey());
                    if (store.get(TIMEOUT_FIELD) != null) {
                        lstRowStore.add(new Date(Math.round(Double.parseDouble(store.get(TIMEOUT_FIELD).toString()))));
                    } else {
                        lstRowStore.add(null);
                    }
                    lstParamStore.add(lstRowStore);
                    for (Object entry2 : store.entrySet()) {
                        storeItem = (Map.Entry<Object, Object>) entry2;
                        lstRowItem = new ArrayList();
                        lstRowItem.add(storeItem.getKey());
                        lstRowItem.add(storeItem.getValue());
                        lstRowItem.add(item.getKey());
                        lstRowItem.add(entry.getKey());
                        lstParamItem.add(lstRowItem);
                    }
                }
            }
        }
        if (!lstParamSpace.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_space");
            StartApp.db.executeQueryBatch("insert into hi_space (space_name, space_size) values (?,?)", lstParamSpace);
        }

        if (!lstParamSequence.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_sequence");
            StartApp.db.executeQueryBatch("insert into hi_sequence (seq_name, value, space_name) values (?,?,?)", lstParamSequence);
        }

        if (!lstParamStore.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_store");
            StartApp.db.executeQueryBatch("insert into hi_store (store_name, store_size, space_name, time_to_live) values (?,?,?,?)", lstParamStore);
        }

        if (!lstParamItem.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_item");
            StartApp.db.executeQueryBatch("insert into hi_item (item_key, item_value, store_name, space_name) values (?,?,?,?)", lstParamItem);
        }

        //create admins
        List lstParamAdmin = new ArrayList();
        for (String entry : admins) {
            List lstRowAdmin = new ArrayList();
            lstRowAdmin.add(entry);
            lstParamAdmin.add(lstRowAdmin);
        }
        if (!lstParamAdmin.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_admin");
            StartApp.db.executeQueryBatch("insert into hi_admin (user_name) values (?)", lstParamAdmin);
        }

        //create permission
        List lstParamPermission = new ArrayList();
        LinkedTreeMap roleSpace = null;
        List lstRowPermission = null;
        Map.Entry<String, String> item = null;
        for (Map.Entry<String, LinkedTreeMap> entry : permission.entrySet()) {
            roleSpace = entry.getValue();
            for (Object entry1 : roleSpace.entrySet()) {
                lstRowPermission = new ArrayList();
                item = (Map.Entry<String, String>) entry1;
                lstRowPermission.add(entry.getKey());
                lstRowPermission.add(item.getKey());
                lstRowPermission.add(item.getValue());
                lstParamPermission.add(lstRowPermission);
            }
        }
        if (!lstParamPermission.isEmpty()) {
            StartApp.db.executeQuery("truncate table hi_permission");
            StartApp.db.executeQueryBatch("insert into hi_permission (user_name, space_name, role ) values (?,?,?)", lstParamPermission);
        }
    }
}
