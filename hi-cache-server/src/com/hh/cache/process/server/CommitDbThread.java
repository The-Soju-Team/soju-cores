/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author HienDM
 */
public class CommitDbThread extends Thread {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommitDbThread.class.getSimpleName());
    
    public static FileOutputStream cacheWriter;
    public static ConcurrentLinkedQueue<byte[]> content = new ConcurrentLinkedQueue();
    
    @Override
    public void run() {
        while (true) {
            try {
                MemManager.commitCacheToDb();
                Thread.sleep(30000l);
            } catch(Exception ex) {
                log.error("Error when commit to disk", ex);
            }              
        }
    }
    

}
