/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.run;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.db.dao.UpdateEventDao;

/**
 * @author HienDM
 */
public class UpdateTransToDBThread extends Thread {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger
            .getLogger(UpdateTransToDBThread.class.getSimpleName());

    public static ConcurrentLinkedQueue<LinkedTreeMap> transQueue = new ConcurrentLinkedQueue();

    @Override
    public void run() {
        while (true) {
            try {
                List<LinkedTreeMap> lstMsg = new ArrayList();
                while (!transQueue.isEmpty()) {
                    LinkedTreeMap data = transQueue.poll();
                    lstMsg.add(data);
                }
                UpdateEventDao.updateDBExecute(lstMsg);
                Thread.sleep(10000l);
            } catch (Exception ex) {
                log.error("Error when commit to disk", ex);
            }
        }
    }
}
