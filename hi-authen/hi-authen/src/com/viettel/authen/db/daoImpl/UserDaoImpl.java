/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.db.daoImpl;

import com.viettel.authen.db.dao.UserDao;
import com.viettel.authen.run.StartApp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HienDM
 */

public class UserDaoImpl implements UserDao {
    
     public List<Map> getAllUser() throws SQLException {
        return StartApp.database.queryData("select * from sm_user");
    }    
    
    public int insertUser(List lstParam) throws SQLException {
        String sql = "insert into users(user_name, full_name, msisdn, email, birthday, password, user_type, create_date, is_enable) values (?,?,?,?,?,?,?,?,1)";
        return StartApp.database.insertData(sql, lstParam);
    }
    
    public void updateUser(List lstParam, boolean changePassword) throws SQLException {
        String sql = "update users set user_name = ?, full_name = ?, msisdn = ?, birthday = ?, email = ?, user_type = ? ";
        if(changePassword) sql += ", password = ? "; 
        sql += " where user_id = ?";
        StartApp.database.executeQuery(sql, lstParam);
    }    
    
    public List<List> searchUser(Integer numberRow, Integer pageLength, String name, String msisdn, String fullName, Integer userType, Date fromDate, Date toDate) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewUser(',user_id,');\">',user_name,'</a>') as name, full_name, " +
                " msisdn, " +
                " CASE user_type " +
                " WHEN 1 THEN 'Quản trị' " +
                " WHEN 2 THEN 'Người dùng' " +
                " END AS user_type" +
                " , DATE_FORMAT(create_date, '%d-%m-%Y') as create_date, " + 
                " CONCAT('<input type=\"checkbox\" name=\"userid\" onclick=\"validateCheckAll(this)\" value=\"',user_id,'\"/>') as user_id "
                + " FROM users ";
        String queryCount = " SELECT count(user_id) FROM users WHERE is_enable = 1 ";
        String query = " ";
        if(name != null && !name.trim().isEmpty()) {
            query += " AND user_name like ? ";
            lstParam.add("%" + name.trim() + "%");
            lstParamCount.add("%" + name.trim() + "%");
        }
        if(msisdn != null && !msisdn.trim().isEmpty()) {
            query += " AND msisdn like ? ";
            lstParam.add("%" + msisdn.trim() + "%");
            lstParamCount.add("%" + msisdn.trim() + "%");
        }
        if(fullName != null && !fullName.trim().isEmpty()) {
            query += " AND full_name like ? ";
            lstParam.add("%" + fullName.trim() + "%");
            lstParamCount.add("%" + fullName.trim() + "%");
        }        
        if(userType != null && userType != 0) {
            query += " AND user_type = ? ";
            lstParam.add(userType);
            lstParamCount.add(userType);
        }
        if(fromDate != null) {
            query += " AND create_date >= ? ";
            lstParam.add(fromDate);            
            lstParamCount.add(fromDate);            
        }
        if(toDate != null) {
            query += " AND create_date <= ? ";
            lstParam.add(toDate);            
            lstParamCount.add(toDate);            
        }
        queryCount += query;
        query += " ORDER BY user_id DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstUser = StartApp.database.queryDataToList(queryData, lstParam);
        List<List> lstCount = StartApp.database.queryDataToList(queryCount, lstParamCount);
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
        
        if(arrApp != null && !arrApp.isEmpty()) {
            List lstUserApp = new ArrayList();
            for(int i = 0; i < arrApp.size(); i++) {
                List lstRow = new ArrayList();
                lstRow.add(userId);
                lstRow.add(Integer.parseInt(arrApp.get(i).toString()));
                lstUserApp.add(lstRow);
            }

            StartApp.database.executeQueryBatch("insert into user_app (user_id, app_id) values (?, ?) ", lstUserApp);
        }
    }    
    
    public Map getUserById(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        List<Map> lstUser = StartApp.database.queryData("select user_id, user_name, password, msisdn, full_name, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, email from users where user_id = ?", lstParam);
        if(lstUser != null && !lstUser.isEmpty()) return lstUser.get(0);
        else return null;
    }
    
    public List<Map> getOrderMaid() throws SQLException {
        return StartApp.database.queryData("select user_id, name, msisdn, residence, user_type, DATE_FORMAT(birthday, '%d-%m-%Y') as birthday, id_number, village, picture from sm_user where user_type = 3 limit 3");
    }
    
    public void updateUserImage(String picture, Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(picture);
        lstParam.add(userId);
        StartApp.database.executeQuery("update sm_user set picture = ? where user_id = ?", lstParam);
    }
}
