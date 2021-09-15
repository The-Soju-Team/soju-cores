/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.db.daoImpl;

import com.viettel.authen.db.dao.AppDao;
import com.viettel.authen.run.StartApp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HienDM
 */

public class AppDaoImpl implements AppDao {

    public int insertApp(List lstParam) throws SQLException {
        String sql = "insert into application(app_name, app_code, ip, port) values (?,?,?,?)";
        return StartApp.database.insertData(sql, lstParam);
    }

    public void updateApp(List lstParam) throws SQLException {
        String sql = "update application set app_name = ?, app_code = ?, ip = ?, port = ? ";
        sql += " where app_id = ?";
        StartApp.database.executeQuery(sql, lstParam);
    }

    public List<Map> getAllApps() throws SQLException {
        return StartApp.database.queryData("select * from application");
    }

    public List<List> searchApp(Integer numberRow, Integer pageLength, String appName, String appCode) throws SQLException {
        List lstParam = new ArrayList();
        List lstParamCount = new ArrayList();
        String queryData = " SELECT 1 as rownum, CONCAT('<a href=\"javascript:void(0)\" onclick=\"loadViewApp(',app_id,');\">',app_code,'</a>') as app_code, app_name, ip, port, "
                + " CONCAT('<input type=\"checkbox\" name=\"appid\" onclick=\"validateCheckAll(this)\" value=\"',app_id,'\"/>') as app_id "
                + " FROM application ";
        String queryCount = " SELECT count(app_id) FROM application ";
        String query = " ";
        if (appName != null && !appName.trim().isEmpty()) {
            query += " AND app_name like ? ";
            lstParam.add("%" + appName.trim() + "%");
            lstParamCount.add("%" + appName.trim() + "%");
        }
        if (appCode != null && !appCode.trim().isEmpty()) {
            query += " AND app_code like ? ";
            lstParam.add("%" + appCode.trim() + "%");
            lstParamCount.add("%" + appCode.trim() + "%");
        }

        queryCount += query;
        query += " ORDER BY app_id DESC LIMIT ?,? ";
        queryData += query;
        lstParam.add(numberRow);
        lstParam.add(pageLength);
        List<List> lstApp = StartApp.database.queryDataToList(queryData, lstParam);
        List<List> lstCount = StartApp.database.queryDataToList(queryCount, lstParamCount);
        List<List> lstResult = new ArrayList();
        lstResult.add(lstCount);
        lstResult.add(lstApp);
        return lstResult;
    }

    public void deleteApp(String lstApp) throws SQLException {
        StartApp.database.executeQuery("delete from user_app where app_id in (" + lstApp + ")");
        StartApp.database.executeQuery("delete from application where app_id in (" + lstApp + ")");
    }

    public Map getAppById(Integer appId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(appId);
        List<Map> lstApp = StartApp.database.queryData("select app_id, app_name, app_code, ip, port from application where app_id = ?", lstParam);
        if (lstApp != null && !lstApp.isEmpty()) return lstApp.get(0);
        else return null;
    }

    public List<List> getAppByUserId(Integer userId) throws SQLException {
        List lstParam = new ArrayList();
        lstParam.add(userId);
        return StartApp.database.queryDataToList("select '' as stt, b.app_name, b.app_id from user_app as a inner join application as b on a.app_id = b.app_id where a.user_id = ?", lstParam);
    }
}
