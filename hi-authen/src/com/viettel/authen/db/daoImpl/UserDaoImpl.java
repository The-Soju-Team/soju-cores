/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.db.daoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.viettel.authen.db.dao.UserDao;
import com.viettel.authen.run.StartApp;
import org.apache.log4j.*;

/**
 * @author HienDM
 */

public class UserDaoImpl implements UserDao {
    private static final int UPDATE_TYPE = 1;
    private static final int DELETE_TYPE = 0;
    private static final Logger log = Logger.getLogger(UserDaoImpl.class.getName());
    private static Gson gson = new Gson();

    public List<Map> getAllUser() throws SQLException {
        return StartApp.database.queryData("select * from sm_user");
    }

    // This is not an optimize way to do this, but we need a work around now so
    // please forgive me!

    /**
     * @param userName: User name that you want to update the information to hicache
     */
    private void updateHiCacheUserCredential(Map resultMap, Map<Object, Object> user, int userId) {
        log.debug("TOGREP | User information updated: " + resultMap);

        // Set user information
        StartApp.hicache.useSpace("authen");
        StartApp.hicache.setStoreAttribute("credentials_id", "" + userId, user.get("user_name"));
        StartApp.hicache.setStoreAttribute("credentials", user.get("user_name").toString(), new Gson().toJson(user));
    }

    private void updateHiCachePasswordExpiry(Map resultMap, Map<Object, Object> user) {
        StartApp.hicache.useSpace("authen");
        StartApp.hicache.setStoreAttribute("expiry_password", user.get("user_name").toString(),
                resultMap.get("password_expiry_date").toString());
    }

    private void updateHiCache(int userId, int type) {
        switch (type) {
            case UPDATE_TYPE:
                log.debug(String.format("TOGREP | Updating HiCache for user %d", userId));
                try {
                    String sql = "SELECT * FROM users WHERE user_id = ?";
                    List params = new ArrayList<>();
                    params.add(userId);
                    List<Map> result = StartApp.database.queryData(sql, params);
                    if (result != null && result.size() != 0) {
//					StartApp.hicache.delete
                        Map<Object, Object> user = new HashMap<>();
                        Map resultMap = result.get(0);
                        user.put("user_name", resultMap.get("user_name").toString());
                        user.put("full_name", resultMap.get("full_name").toString());
                        user.put("mobile", resultMap.get("msisdn").toString());
                        user.put("email", resultMap.get("email").toString());
                        user.put("birthday", resultMap.get("birthday").toString());
                        user.put("password", resultMap.get("password").toString());
                        user.put("user_type", resultMap.get("user_type"));
                        user.put("create_date", resultMap.get("create_date").toString());
                        user.put("user_id", Integer.parseInt(resultMap.get("user_id").toString()));

                        // Get user app list
                        List<String> userAppIds = getUserAppByUserId(userId);
                        log.debug(String.format("TOGREP | User app ids for user:%s ", user.get("user_name").toString())
                                + userAppIds);
                        user.put("appid", userAppIds);

                        // Update user information
                        updateHiCacheUserCredential(resultMap, user, userId);
                        // Set expiry password
                        updateHiCachePasswordExpiry(resultMap, user);

                        // Other function should be called here
                    }
                    log.debug(String.format("TOGREP | Done updating HiCache for userId %d - userName: %s", userId,
                            result.get(0).get("user_name").toString()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case DELETE_TYPE:
                // TODO
                break;
            default:
                break;

        }

    }

    public int insertUser(List lstParam) throws SQLException {
        String sql = "insert into users(user_name, full_name, msisdn, email, birthday, password, user_type, create_date, is_enable, password_expiry_date) values (?,?,?,?,?,?,?,?,1, now() + interval 60 day)";
        int userId = StartApp.database.insertData(sql, lstParam);
        updateHiCache(userId, UPDATE_TYPE);
        return userId;
    }

    public void updateUser(List lstParam, boolean changePassword) throws SQLException {
        String sql = "update users set user_name = ?, full_name = ?, msisdn = ?, birthday = ?, email = ?, user_type = ?, password_expiry_date=now() + interval 60 day ";
        if (changePassword)
            sql += ", password = ? ";
        sql += " where user_id = ?";
        StartApp.database.executeQuery(sql, lstParam);
        updateHiCache((int) lstParam.get(lstParam.size() - 1), UPDATE_TYPE);
    }

    public List<List> searchUser(String userName, Integer numberRow, Integer pageLength, String name, String msisdn,
                                 String fullName, Integer userType, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        // Let's get user information from cache, and get user type
        StartApp.hicache.useSpace("authen");
        String userJsonStringFromCache = StartApp.hicache.getStringAttribute("credentials", userName);
        HashMap userInfo = gson.fromJson(userJsonStringFromCache, HashMap.class);
        String queryData = null;
        String queryCount = null;
        log.debug(String.format("TOGREP | userInfo of user %s: ", userName) + userInfo);
        double userTypeCache = Double.parseDouble(userInfo.get("user_type").toString());
        if (userTypeCache != 1.0) {
            queryData = "SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewUser(',user_id,');\">',user_name,'</a>') as name, full_name, "
                    + " msisdn, " + " CASE user_type " + " WHEN 1 THEN 'Quản trị' " + " WHEN 2 THEN 'Người dùng' "
                    + " END AS user_type" + " , DATE_FORMAT(create_date, '%d-%m-%Y') as create_date, "
                    + " CONCAT('<input type=\"checkbox\" name=\"userid\" onclick=\"validateCheckAll(this)\" value=\"',user_id,'\"/>') as user_id "
                    + " FROM users WHERE user_id = " + userInfo.get("user_id").toString();
            queryCount = "SELECT 1 as hi";
        } else {
            queryData = "SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewUser(',user_id,');\">',user_name,'</a>') as name, full_name, "
                    + " msisdn, " + " CASE user_type " + " WHEN 1 THEN 'Quản trị' " + " WHEN 2 THEN 'Người dùng' "
                    + " END AS user_type" + " , DATE_FORMAT(create_date, '%d-%m-%Y') as create_date, "
                    + " CONCAT('<input type=\"checkbox\" name=\"userid\" onclick=\"validateCheckAll(this)\" value=\"',user_id,'\"/>') as user_id "
                    + " FROM users WHERE 1 = 1 ";
            queryCount = " SELECT count(user_id) FROM users WHERE is_enable = 1 ";
            String query = " ";
            if (name != null && !name.trim().isEmpty()) {
                query += " AND user_name like ? ";
                lstParam.add("%" + name.trim() + "%");
                lstParamCount.add("%" + name.trim() + "%");
            }
            if (msisdn != null && !msisdn.trim().isEmpty()) {
                query += " AND msisdn like ? ";
                lstParam.add("%" + msisdn.trim() + "%");
                lstParamCount.add("%" + msisdn.trim() + "%");
            }
            if (fullName != null && !fullName.trim().isEmpty()) {
                query += " AND full_name like ? ";
                lstParam.add("%" + fullName.trim() + "%");
                lstParamCount.add("%" + fullName.trim() + "%");
            }
            if (userType != null && userType != 0) {
                query += " AND user_type = ? ";
                lstParam.add(userType);
                lstParamCount.add(userType);
            }
            if (fromDate != null) {
                query += " AND create_date >= ? ";
                lstParam.add(fromDate);
                lstParamCount.add(fromDate);
            }
            if (toDate != null) {
                query += " AND create_date <= ? ";
                lstParam.add(toDate);
                lstParamCount.add(toDate);
            }
            queryCount += query;
            query += " ORDER BY user_id DESC LIMIT ?,? ";
            queryData += query;
        }
        lstParam.add(numberRow);
        lstParam.add(pageLength);

        List<List> lstUser = null;
        List<List> lstCount = null;
        if (userTypeCache != 1.0) {
            lstUser = StartApp.database.queryDataToList(queryData);
            lstCount = StartApp.database.queryDataToList(queryCount);
        } else {
            log.debug(String.format("TOGREP | Search User Query: %s", queryData));
            lstUser = StartApp.database.queryDataToList(queryData, lstParam);
            lstCount = StartApp.database.queryDataToList(queryCount, lstParamCount);
        }
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstUser);
        return lstResult;
    }

    public void deleteUser(String lstUser) throws SQLException {
        StartApp.database.executeQuery("delete from users where user_id in (" + lstUser + ")");
    }

    public void deleteUserImage(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        StartApp.database.executeQuery("update sm_user set picture = null where user_id = ?", lstParam);
    }

    public void updateUserApp(Integer userId, List arrApp) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        StartApp.database.executeQuery("delete from user_app where user_id = ? ", lstParam);

        if (arrApp != null && !arrApp.isEmpty()) {
            List lstUserApp = new ArrayList();
            for (int i = 0; i < arrApp.size(); i++) {
                List lstRow = new ArrayList();
                lstRow.add(userId);
                lstRow.add(Integer.parseInt(arrApp.get(i).toString()));
                lstUserApp.add(lstRow);
            }

            StartApp.database.executeQueryBatch("insert into user_app (user_id, app_id) values (?, ?) ", lstUserApp);
            updateHiCache(userId, UPDATE_TYPE);
        }
    }

    public List<String> getUserAppByUserId(Integer userId) throws SQLException {
        List params = new ArrayList<>();
        List<String> userAppIds = new ArrayList<>();
        params.add(userId);
        String query = "SELECT app_id FROM user_app WHERE user_id = ?";
        List<Map> result = StartApp.database.queryData(query, params);
        if (result != null && result.size() != 0) {
            for (Map m : result) {
                userAppIds.add(m.get("app_id").toString());
            }
        }
        return userAppIds;
    }

    public Map getUserById(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        List<Map> lstUser = StartApp.database.queryData(
                "select user_id, user_name, password, msisdn, full_name, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, email from users where user_id = ?",
                lstParam);
        if (lstUser != null && !lstUser.isEmpty())
            return lstUser.get(0);
        else
            return null;
    }

    public List<Map> getOrderMaid() throws SQLException {
        return StartApp.database.queryData(
                "select user_id, name, msisdn, residence, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, id_number, village, picture from sm_user where user_type = 3 limit 3");
    }

    public void updateUserImage(String picture, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(picture);
        lstParam.add(userId);
        StartApp.database.executeQuery("update sm_user set picture = ? where user_id = ?", lstParam);
    }
}
