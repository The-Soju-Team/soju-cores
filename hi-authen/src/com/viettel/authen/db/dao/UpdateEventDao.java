/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.db.dao;

import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.run.StartApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author dvgp_admin
 */
public class UpdateEventDao {
    private static Logger log = Logger.getLogger(UpdateEventDao.class.getSimpleName());

    public static void updateDBExecute(List<LinkedTreeMap> lstMsg) throws Exception {
        List lstParam = new ArrayList();
        for (LinkedTreeMap msg : lstMsg) {
            List lstRow = new ArrayList();
            lstRow.add(msg.get("hi-process"));
            lstRow.add(new Date());
            lstRow.add(msg.get("access-token"));
            lstRow.add(msg.get("result-code"));
            lstRow.add(msg.get("result-message"));
            lstRow.add(msg.get("userName"));
            lstRow.add(msg.get("call_back"));
            lstRow.add(msg.get("cmd"));
            lstParam.add(lstRow);
        }
        StartApp.database.executeQueryBatch("insert trans_event (trans_code, start_time, token, result_code, result_message, user_name, call_back, cmd) values (?, ?, ?, ?, ?, ?, ?, ?) ", lstParam);
    }
}
