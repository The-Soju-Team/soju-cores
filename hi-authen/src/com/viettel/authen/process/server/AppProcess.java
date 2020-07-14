/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import com.viettel.authen.db.dao.AppDao;
import com.viettel.authen.db.daoImpl.AppDaoImpl;
import com.viettel.authen.run.ServerProcess;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viettel.authen.global.CommandConstants;
import com.viettel.authen.run.StartApp;
import com.viettel.authen.run.UpdateTransToDBThread;
import org.apache.log4j.Logger;

/**
 *
 * @author HienDM
 */
public class AppProcess extends ServerProcess {
    private static Logger log = Logger.getLogger(AppProcess.class.getSimpleName());
    public AppProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }    

    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        String cmd = (String) msg.get(CommandConstants.COMMAND);
        switch (cmd) {
            case "ADD_APP":
                addApp(msg);
                break;
            case "SEARCH_APP":
                searchApp(msg);
                break;
            case "GET_APP_BY_USER":
                getAppByUser(msg);
                break;                
            case "LOAD_VIEW_APP":
                loadViewApp(msg);
                break;
            case "UPDATE_APP":
                updateApp(msg);
                break;
            default:
                this.returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
                break;
        }
        msg.put("result-code", "000");
        UpdateTransToDBThread.transQueue.offer(msg);
    }
    
    public void searchApp(LinkedTreeMap msg) throws Exception {
        AppDao udb = new AppDaoImpl();
        if(msg.get("isdelete") != null && msg.get("isdelete").equals("1")) {
            String deleteApps = (String)msg.get("appid");
            if(deleteApps != null) {
                deleteApps = deleteApps.replace("appid=", "");
                deleteApps = deleteApps.replace("&", ",");
                udb.deleteApp(deleteApps);
                
                String[] arrAppId = deleteApps.split(",");
                for(int i = 0; i < arrAppId.length; i++) {
                    String appName = StartApp.hicache.getStringAttribute("credentials_id", arrAppId[i]);
                    StartApp.hicache.deleteStoreAttribute("credentials_id", arrAppId[i]);
                    StartApp.hicache.deleteStoreAttribute("credentials", appName);
                }
            }
        }        
        
        int pageLength = 10;
        if(msg.get("length") != null) {
            pageLength = Integer.parseInt((String)msg.get("length"));
            if(pageLength == 0) pageLength = 10;
        }
        
        int numberRow = 0;
        if(msg.get("start") != null) {
            numberRow = Integer.parseInt((String)msg.get("start"));
        }

        String appName = (String)msg.get("appname");
        String appCode = (String)msg.get("appcode");
                
        if(!(appName != null && !appName.trim().isEmpty())) appName = null;
        if(!(appCode != null && !appCode.trim().isEmpty())) appCode = null;
        
        List<List> listResult = udb.searchApp(numberRow, pageLength, appName, appCode);
        List<List> listData = listResult.get(1);
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, numberRow + i + 1);
        }
        Integer count = Integer.parseInt(((List<List>)listResult.get(0)).get(0).get(0).toString());
        HashMap returnData = new HashMap();
        returnData.put("recordsTotal", count);
        returnData.put("recordsFiltered", count);        
        returnData.put("data", listData);
        
        returnStringToFrontend(msg, new Gson().toJson(returnData));
    }
    
    public void getAppByUser(LinkedTreeMap msg) throws Exception {
        AppDao udb = new AppDaoImpl();

        String userId = (String)msg.get("userid");
        
        List<List> listData = new ArrayList();
        if(userId != null) listData = udb.getAppByUserId(Integer.parseInt(userId));
        for(int i = 0; i < listData.size(); i++) {
            listData.get(i).set(0, i + 1);
        }
        HashMap returnData = new HashMap();
        returnData.put("data", listData);
        returnStringToFrontend(msg, new Gson().toJson(returnData));
    }    
    
    public void addApp(LinkedTreeMap msg) throws Exception {
        String appName = (String)msg.get("appname");
        String appCode = (String)msg.get("appcode");
        String ip = (String)msg.get("ip");
        String port = (String)msg.get("port");

        HashMap app = new HashMap();
        
        List lstParam = new ArrayList();
        
        if(appName != null) {
            lstParam.add(appName.trim());
            app.put("app_name", appName.trim());
        } else lstParam.add(null);
        
        if(appCode != null) {
            lstParam.add(appCode.trim());
            app.put("app_code", appCode.trim());
        } else lstParam.add(null);
        
        if(ip != null) {
            lstParam.add(ip.trim());
            app.put("ip", ip.trim());
        } else lstParam.add(null);
        
        if(port != null) {
            lstParam.add(port.trim());
            app.put("port", port.trim());
        } else lstParam.add(null);      
        
        Integer appId = null;
        try {
            appId = (new AppDaoImpl()).insertApp(lstParam);
        } catch(Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                HashMap returnData = new HashMap();
                returnData.put("error_code", "createapp_04");
                returnData.put("error_message", "Mã ứng dụng đã tồn tại");
                returnStringToFrontend(msg, new Gson().toJson(returnData));
                return;                
            }
        }
        
        if(appId != null) {
            app.put("app_id", appId);
            StartApp.hicache.setStoreAttribute("application", "" + appId, app);
        }
        
        returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
    }
    
    public void loadViewApp(LinkedTreeMap msg) throws Exception {
        Map app = (new AppDaoImpl()).getAppById(Integer.parseInt((String)msg.get("appid")));
        returnStringToFrontend(msg, new Gson().toJson(app));
    }
        
    public void updateApp(LinkedTreeMap msg) throws Exception {
        //------- Update app --------------------------------------------
        String appId = (String)msg.get("appid");
        
        String appName = (String)msg.get("appname");
        String appCode = (String)msg.get("appcode");
        String ip = (String)msg.get("ip");
        String port = (String)msg.get("port");
        HashMap app = new HashMap();
        
        List lstParam = new ArrayList();
        app.put("app_id", appId);
        
        if(appName != null) {
            lstParam.add(appName.trim());
            app.put("app_name", appName.trim());
        }
        else lstParam.add(null);
        
        if(appCode != null) {
            lstParam.add(appCode.trim());
            app.put("app_code", appCode.trim());
        }
        else lstParam.add(null);
        
        if(ip != null) {
            lstParam.add(ip.trim());
            app.put("ip", ip.trim());
        }
        else lstParam.add(null);
        
        if(port != null) {
            lstParam.add(port.trim());
            app.put("port", port.trim());
        }
        else lstParam.add(null);
             
        Integer intAppId = null;
        if(appId != null && !appId.trim().isEmpty()) {
            intAppId = Integer.parseInt(appId);
            lstParam.add(intAppId);
        }
        else lstParam.add(null);             
        
        try {
            (new AppDaoImpl()).updateApp(lstParam);
        } catch(Exception ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                HashMap returnData = new HashMap();
                returnData.put("error_code", "createapp_04");
                returnData.put("error_message", "Mã ứng dụng đã tồn tại");
                returnStringToFrontend(msg, new Gson().toJson(returnData));
                return;
            }
            log.error("Error when update app to db", ex);
        }
        
        if(appId != null) {
            app.put("app_id", appId);
            StartApp.hicache.setStoreAttribute("application", appId, app);
        }        
        
        returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
    }
}
