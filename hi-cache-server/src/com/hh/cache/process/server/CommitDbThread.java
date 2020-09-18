/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import org.apache.log4j.Logger;

/**
 * @author HienDM
 */
public class CommitDbThread implements Runnable {
    private static final Logger log = org.apache.log4j.Logger.getLogger(CommitDbThread.class);

    @Override
    public void run() {
        while (true) {
            try {
                log.info("Committing Cache Data To Database");
                MemManager.commitCacheToDb();
                log.info("Done Committing Cache To Database");
                Thread.sleep(30000l);
            } catch (Exception ex) {
                log.error("Error when commit to disk", ex);
            }
        }
    }


}
