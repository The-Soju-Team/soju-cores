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
 * @author HienDM
 */

public interface UserDao {

    public int insertUser(List lstParam) throws SQLException;

    public void updateUser(List lstParam, boolean changePassword) throws SQLException;

    public List<List> searchUser(String userName, Integer numberRow, Integer pageLength, String name, String mobile,
                                 String fullName, Integer userType, Date fromDate, Date toDate) throws SQLException;

    public void deleteUser(String lstUser) throws SQLException;

    public Map getUserById(Integer userId) throws SQLException;

    public void updateUserApp(Integer userId, List arrApp) throws SQLException;
}
