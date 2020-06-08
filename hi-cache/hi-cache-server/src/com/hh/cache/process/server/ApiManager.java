/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import com.google.gson.internal.LinkedTreeMap;

/**
 *
 * @author HienDM1
 */
public interface ApiManager {
    
    public static final String API_CREATE_SEQ = "create-sequence";
    public static final String API_CREATE_SPACE = "create-space";
    public static final String API_CREATE_STORE = "create-store";
    public static final String API_CREATE_USER = "create-user";
    public static final String API_DELETE_SPACE = "delete-space";
    public static final String API_DELETE_STORE = "delete-store";
    public static final String API_GET_SPACE = "get-space";
    public static final String API_GET_STORE = "get-store";
    public static final String API_GET_STORE_ATB = "get-store-atb";
    public static final String API_GRANT_PERMISSION = "grant-permission";
    public static final String API_GRANT_ADMIN = "grant-admin";
    public static final String API_INCREMENT_GET = "increment";
    public static final String API_REFRESH_EXPIRE = "refresh-expire";
    public static final String API_DELETE_STORE_ATB = "delete-store-atb";
    public static final String API_SET_STORE_ATB = "set-store-atb";
    public static final String API_USE_SPACE = "use-space";
    public static final String API_LOGIN = "login";
    public static final String API_GET_STORE_SIZE = "get-store-size";
    public static final String API_DELETE_USER = "delete-user";
    public static final String API_DELETE_SEQ = "delete-sequence";    
    public static final String API_REMOVE_PERMISSION = "remove-permission";
    public static final String API_REMOVE_ADMIN = "remove-admin";
    public static final String API_GET_SPACE_SIZE = "get-space-size";
    
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_OWNER = "ROLE_OWNER";
    public static final String ROLE_VIEWER = "ROLE_VIEWER";
    
    public static final String INFO_EXECUTE_SUCCESS = "00|Response successfully!";
    public static final String ERROR_USER_CANT_NULL = "01|User name can't be null!";
    public static final String ERROR_PASSWORD_CANT_NULL = "02|Password can't be null!";
    public static final String ERROR_CANT_FIND_SPACE = "03|Can't find space ";
    public static final String ERROR_PERMISSION_DENIED = "04|Permission denied!";
    public static final String ERROR_LOGIN_FAILED = "05|Login failed!";
    public static final String ERROR_SPACE_CANT_NULL = "06|Space name can't be null!";
    public static final String ERROR_USER_EXISTED = "07|User %s is existed!";
    public static final String ERROR_ROLE_CANT_NULL = "08|Role can't be null!";    
    public static final String ERROR_CANT_FIND_USER = "09|Can't find user ";
    public static final String ERROR_CANT_FIND_ROLE = "10|Can't find role ";
    public static final String ERROR_STORE_CANT_NULL = "11|Store name can't be null!";
    public static final String ERROR_SPACE_EXISTED = "12|Space %s is existed!";
    public static final String ERROR_STORE_EXISTED = "13|Element %s is existed!";
    public static final String ERROR_CANT_FIND_STORE = "14|Can't find store ";
    public static final String ERROR_SEQ_CANT_NULL = "15|Sequence name can't be null!";
    public static final String ERROR_CANT_FIND_SEQ = "16|Can't find sequence ";    
    public static final String ERROR_INCREMENT_CANT_NULL = "17|Increment can't be null!";
    public static final String ERROR_SYSTEM_MAINTENANCE = "18|System maintenance!";
    public static final String ERROR_CANT_FIND_SEQUENCE = "19|Can't find sequence ";
    
    public String login(String userName, String password, String spaceName);
    
    public String login(String userName, String password);
    
    public String createUser(String userName, String password, String accessToken);
    
    public String deleteUser(String userName, String accessToken);

    public String grantPermission(String role, String userName, String spaceName, String accessToken);
    
    public String grantAdmin(String userName, String accessToken);
    
    public String removePermission(String userName, String spaceName, String accessToken);
    
    public String removeAdmin(String userName, String accessToken);    
    
    public String createSpace(String spaceName, String accessToken);
    
    public String useSpace(String spaceName, String accessToken);
    
    public Object getSpace(String spaceName, String accessToken);
    
    public Object getSpace(String accessToken);
    
    public String deleteSpace(String spaceName, String accessToken);
    
    public String createStore(String spaceName, String storeName, String accessToken, long timeOutMiliSeconds);
    
    public String createStore(String storeName, String accessToken, long timeOutMiliSeconds);
    
    public String createStore(String spaceName, String storeName, String accessToken);
    
    public String createStore(String storeName, String accessToken);
    
    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex, Long size);
    
    public Object getStore(String spaceName, String storeName, String accessToken, Long startIndex);
    
    public Object getStore(String spaceName, String storeName, String accessToken);
    
    public Object getStore(String storeName, String accessToken, Long startIndex, Long size);
    
    public Object getStore(String storeName, String accessToken, Long startIndex);
    
    public Object getStore(String storeName, String accessToken);
    
    public Object getStoreSize(String spaceName, String storeName, String accessToken);
    
    public Object getStoreSize(String storeName, String accessToken);    
    
    public Object getSpaceSize(String spaceName, String accessToken);
    
    public Object getSpaceSize(String accessToken);
    
    public String deleteStore(String spaceName, String storeName, String accessToken);
    
    public String deleteStore(String storeName, String accessToken);
    
    public String setStoreAttribute(String spaceName, String storeName, String key, Object value, String accessToken);
    
    public String setStoreAttribute(String storeName, String key, Object value, String accessToken);
    
    public Object getStoreAttribute(String spaceName, String storeName, String key, String accessToken);
    
    public Object getStoreAttribute(String storeName, String key, String accessToken);
    
    public String deleteStoreAttribute(String spaceName, String storeName, String key, String accessToken);
    
    public String deleteStoreAttribute(String storeName, String key, String accessToken);
    
    public String refreshStoreExpire(String spaceName, String storeName, String accessToken);
    
    public String refreshStoreExpire(String storeName, String accessToken);
    
    public String createSequence(String spaceName, String sequenceName, long startWith, String accessToken);
    
    public String createSequence(String sequenceName, long startWith, String accessToken);
    
    public String deleteSequence(String spaceName, String sequenceName, String accessToken);
    
    public String deleteSequence(String sequenceName, String accessToken);
    
    public Object incrementAndGet(String spaceName, String sequenceName, Long number, String accessToken);
    
    public Object incrementAndGet(String sequenceName, Long number, String accessToken);
    
    public String createStore(String spaceName, String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds);
    
    public String createStore(String storeName, LinkedTreeMap store, String accessToken, long timeOutMiliSeconds);
    
    public String createStore(String storeName, LinkedTreeMap store, String accessToken);
}
