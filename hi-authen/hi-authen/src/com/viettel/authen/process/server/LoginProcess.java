/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.viettel.authen.run.StartApp;
import com.hh.connector.server.Server;
import com.hh.util.EncryptDecryptUtils;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.UpdateTransToDBThread;
import com.viettel.authen.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author HienDM
 */
public class LoginProcess extends ServerProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoginProcess.class.getSimpleName());
    private static org.apache.log4j.Logger logKafka = org.apache.log4j.Logger.getLogger("kafkaLogger");
    public static final String LOGIN_SUCCESS = "0";
    public static final String LOGIN_INCORRECT = "1";
    public static final String CAPTCHA_INCORRECT = "2";
    public static final String USER_EMPTY = "3";
    public static final String PASSWORD_EMPTY = "4";
    public static final String CAPTCHA_EMPTY = "5";
    public static final String NOT_ALLOWED_IP = "6";
    private static Gson gson = new Gson();
    
    public LoginProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap obj) throws Exception {
        logKafka.info((new Gson()).toJson(obj));
        log.info("========== ABCDEF ===========");
        log.info((new Gson()).toJson(obj));
        log.info("////////// ABCDEF ///////////");
        String captCha = "";
        Integer failCount = null;
        if(getSessionStringAttribute(obj,"ssoFailCount") != null)
            failCount = Integer.parseInt(getSessionStringAttribute(obj,"ssoFailCount"));
        if (failCount == null) failCount = 0;
        else if (failCount > 2) captCha = (String) obj.get("captcha");
        
        String password = (String) obj.get("password");
        String userName = (String) obj.get("userName");
        String forward = (String) obj.get("forward");
        String ip = (String) obj.get("original-ip-request");
        if (userName == null || userName.isEmpty()) {
            failCount++;
            sendLoginResponse(obj, USER_EMPTY, failCount);
            return;
        }
        if (password == null || password.isEmpty()) {
            failCount++;
            sendLoginResponse(obj, PASSWORD_EMPTY, failCount);
            return;
        }
        
        
        // Kiem tra captcha
        String mobile = (String) obj.get("gm");
        boolean checkCaptcha = true;
        if (!(mobile == null || mobile.isEmpty())) {
            if ((captCha != null) && (!captCha.isEmpty()) && (!"null".equals(captCha))) {
                String pixCaptcha = (String) getSessionStringAttribute(obj,"sso_captcha");
                if (pixCaptcha != null) {
                    checkCaptcha = pixCaptcha.toLowerCase().equals(captCha.toLowerCase());
                }
                if (!checkCaptcha) {
                    failCount++;
                    sendLoginResponse(obj, CAPTCHA_INCORRECT, failCount);
                    return;
                }
            } else if (failCount > 2) {
                checkCaptcha = false;
                failCount++;
                sendLoginResponse(obj, CAPTCHA_EMPTY, failCount);
                return;
            }
            removeSessionAttribute(obj,"sso_captcha");
        }
        
        // Neu nhap dung captcha
        if (checkCaptcha) {

            // 2020-04-13 17:27:04 donnn: Check IP
            String loginToken = (String) obj.get("loginToken");
            // String queryCheckIp = "select lt.*, ui.allowed_ip from (select * from login_token where USER_NAME = ? and LOGIN_TOKEN = ? order by id desc limit 1) lt INNER join user_ip ui on lt.user_name = ui.user_name";
            String queryCheckIp = " select user_name, allowed_ip from user_ip where user_name = ? ";
            String userIp = ip;
            List params = new ArrayList();
            params.add(userName);
            // params.add(loginToken);
            log.info("queryCheckIp");
            log.info(queryCheckIp);
            boolean checkAllowedIp = false;
            List<Map> resultQueryCheckIp = StartApp.database.queryData(queryCheckIp, params);
            if (!(userName.equals("donnn1"))) {
                checkAllowedIp = true;
            } else if ((resultQueryCheckIp == null) || (resultQueryCheckIp.size() < 1)) {
                // allow all other users
                checkAllowedIp = false;
            } else {
                ArrayList<String> allowedIps = gson.fromJson(StartApp.hicache.getStringAttribute("user_ips", userName), ArrayList.class);
                log.info("ABCDEF Data from hicache for user " + userName + allowedIps);
                if (allowedIps != null) {
                    for(int idx = 0; idx < allowedIps.size(); idx++) {
                        String allowedIp = allowedIps.get(idx);
                        log.info("ABCDEF comparing ip " + userIp + " and allowedIp " + allowedIp + " result " + StringUtils.checkIpInNetwork(userIp, allowedIp));
                        if (StringUtils.checkIpInNetwork(userIp, allowedIp)) {
                            checkAllowedIp = true;
                            break;
                        }
                    }
                }
            }

            if (!checkAllowedIp) {
                log.info("ABCDEF user " + userName + " trying to log in at unauthorized IP " + userIp);
                sendLoginResponse(obj, NOT_ALLOWED_IP, failCount);
                return;
            }

            log.info("ABCDEF checkAllowedIp " + checkAllowedIp);

            // End check IP

            EncryptDecryptUtils edu = new EncryptDecryptUtils();
            Gson gson = new Gson();
            String json = StartApp.hicache.getStringAttribute("credentials", userName);
            log.info("ABCDEF " + json);
            LinkedTreeMap user = null;
            if(json != null) user = gson.fromJson(json, LinkedTreeMap.class);
            String correctPassword = null;
            if(user != null) correctPassword = (String)user.get("password");
            log.info("ABCDEF password " + password + " correctPassword " + correctPassword + " result " + correctPassword.equals(edu.encodePassword(password)));
            if (user != null && correctPassword != null && correctPassword.equals(edu.encodePassword(password))) {
                log.info("ABCDEF nhay cm xuong day r");
                String strUserInfo = gson.toJson(user);
                String callBackKey = getSessionStringAttribute(obj, "callback-url");
                removeSessionAttribute(obj, "callback-url");
                log.info("ABCDEF nhay cm xuong day r 1");
                if(callBackKey == null || "0".equals(forward)) {
                    log.info("ABCDEF nhay cm xuong day 2");
                    boolean checkApp = false;
                    List<String> apps = (List)user.get("appid");
                    if(apps != null) {
                        for(String appId : apps) {
                            Map row = (Map) StartApp.hicache.getStoreAttribute("application", appId);
                            if("bi_authen".equals(row.get("app_code"))) {
                                checkApp = true;
                                break;
                            }
                        }
                    }              
                    if("root".equals(userName) || checkApp) {
                        String newCookie = UUID.randomUUID().toString();
                        StartApp.hicache.createStore(newCookie, 86400000l);
                        StartApp.hicache.setStoreAttribute(newCookie, "sso_username", strUserInfo);
                        obj.put("cookie", newCookie);
                        obj.put("username", userName);
                        sendLoginResponse(obj, LOGIN_SUCCESS, 0);                         
                    } else {
                        failCount++;
                        setSessionAttribute(obj, "ssoFailCount", "" + failCount);
                        sendLoginResponse(obj, LOGIN_INCORRECT, failCount);                         
                    }
                } else {
                    List lstData = (List) StartApp.hicache.getStoreAttribute(callBackKey, "callback-url");
                    String callBack = (String)lstData.get(0);
                    String appCode = (String)lstData.get(1);
                    List<String> apps = (List)user.get("appid");
                    log.info("ABCDEF Get app_code: " + appCode);
                    boolean checkApp = false;
                    if(apps != null) {
                        for(String appId : apps) {
                            log.info("ABCDEF loop app_id: " + appId);
                            Map row = (Map) StartApp.hicache.getStoreAttribute("application", appId);
                            log.info("ABCDEF loop app_code: " + row.get("app_code"));
                            if(appCode.equals(row.get("app_code"))) {
                                checkApp = true;
                                break;
                            }
                        }
                    } else {
                        log.info("ABCDEF apps null");
                    }
                    log.info("ABCDEF checkApp " + checkApp);
                    log.info("Get session callback-url: " + callBack + " storeName: " + "login_" + (String) obj.get("access-token"));
                    if(checkApp) {
                        String newCookie = UUID.randomUUID().toString();
                        StartApp.hicache.createStore(newCookie, 86400000l);
                        StartApp.hicache.setStoreAttribute(newCookie, "sso_username", strUserInfo);
                        obj.put("call_back", callBack + "&sso-token=" + newCookie);
                        obj.put("cookie", newCookie);
                        sendLoginResponse(obj, LOGIN_SUCCESS, 0);
                    } else {
                        failCount++;
                        setSessionAttribute(obj, "ssoFailCount", "" + failCount);
                        sendLoginResponse(obj, LOGIN_INCORRECT, failCount);                        
                    }
                }
            } else {
                failCount++;
                setSessionAttribute(obj, "ssoFailCount", "" + failCount);
                sendLoginResponse(obj, LOGIN_INCORRECT, failCount);
            }
        }
        obj.put("result-code", "000");
        UpdateTransToDBThread.transQueue.offer(obj);        
    }
    
    public void sendLoginResponse(LinkedTreeMap request, String status, Integer failCount) throws Exception {
        LinkedTreeMap<String, String> data = new LinkedTreeMap();
        data.put("status", status);
        if (failCount == null) {
            failCount = 0;
        }
        data.put("call_back", (String)request.get("call_back"));
        data.put("failCount", failCount.toString());
        data.put("accessToken", (String)request.get("cookie"));
        String json = new Gson().toJson(data);
        returnStringToFrontend(request, json);
    }    
}
