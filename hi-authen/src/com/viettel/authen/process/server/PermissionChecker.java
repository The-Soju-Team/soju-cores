package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.db.daoImpl.UserDaoImpl;
import com.viettel.authen.global.CommandConstants;
import com.viettel.authen.run.StartApp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.*;


public class PermissionChecker {
    private static Gson gson = new Gson();
    private static final Logger log = Logger.getLogger(PermissionChecker.class);

    public static boolean isPermissionGranted(LinkedTreeMap msg) {
        HashMap userInfo = gson.fromJson(
                StartApp.hicache.getStringAttribute(msg.get("access-token").toString(), "sso_username"), HashMap.class);
        double userType = Double.parseDouble(userInfo.get("user_type").toString());
        if (userType == 1.0) { // Well if he is a administrator, let him do whatever he wants
            return true;
        }
        String cmd = (String) msg.get(CommandConstants.COMMAND);
        switch (cmd) {
            case CommandConstants.UPLOAD_USER_PIC:
                break;
            case CommandConstants.ADD_USER:
            case CommandConstants.LOAD_APP_DATA:
            case CommandConstants.SEARCH_APP:
                return false;
            case CommandConstants.SEARCH_USER:
                break;
            case CommandConstants.LOAD_VIEW_USER:
                try {
                    Map user = (new UserDaoImpl()).getUserById(Integer.parseInt((String) msg.get("userid")));
                    if (userInfo.get("user_name").equals(user.get("user_name"))) {
                        return true;
                    } else {
                        log.debug(String.format("TOGREP | User %s is trying to inject viewing user %s ==== DENY ====",
                                userInfo.get("user_name").toString(), user.get("user_name")));
                        return false;
                    }
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            case CommandConstants.UPDATE_USER:
                try {
                    Map user = (new UserDaoImpl()).getUserById(Integer.parseInt((String) msg.get("userid")));
                    if (userInfo.get("user_name").equals(user.get("user_name"))) {
                        return true;
                    } else {
                        log.debug(String.format("TOGREP | User %s is trying to inject updating user %s ==== DENY ====",
                                userInfo.get("user_name").toString(), user.get("user_name")));
                        return false;
                    }
                } catch (NumberFormatException | SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            case CommandConstants.VIEW_USER_IMAGE:
                break;
            default:
                break;
        }
        return true;
    }
}
