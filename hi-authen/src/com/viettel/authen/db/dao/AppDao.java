/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.db.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HienDM
 */

public interface AppDao {    
    public int insertApp(List lstParam) throws SQLException;
    
    public void updateApp(List lstParam) throws SQLException; 
    
    public List<List> searchApp(Integer numberRow, Integer pageLength, String appName, String appCode) throws SQLException;
        
    public void deleteApp(String lstApp) throws SQLException;
        
    public Map getAppById(Integer appId) throws SQLException;    
    
    public List<List> getAppByUserId(Integer userId) throws SQLException;    
}
