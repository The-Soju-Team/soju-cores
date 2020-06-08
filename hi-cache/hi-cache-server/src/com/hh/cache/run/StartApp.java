/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.run;

import com.hh.cache.process.server.CommitDbThread;
import com.hh.cache.process.server.LoadCacheProcess;
import com.hh.cache.process.server.MemManager;
import com.hh.connector.server.Server;
import com.hh.rdbms.DbcpConnector;
import com.hh.util.ConfigUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author HienDM
 */
public class StartApp {
    public static ConfigUtils config;
    public static Server server = new Server();
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MemManager.class.getSimpleName());    
    public static DbcpConnector db;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        start("../etc");
    }
    
    public static void start(String configPath) throws Exception {
        config = new ConfigUtils(configPath);        
        String logLevel = config.getConfig("log-level");
        if (logLevel.isEmpty()) {
            logLevel = "ERROR";
        }
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.toLevel(logLevel));        
        ConsoleAppender a = (ConsoleAppender) org.apache.log4j.Logger.getRootLogger().getAppender("stdout");
        a.setLayout(new PatternLayout("%d{dd/MM/yyyy HH:mm:ss} %5p [%t] %c{1}: %m%n"));
        
        if("1".equals(config.getConfig("commit-db"))) {
            db = new DbcpConnector(configPath + "/database.conf");
            db.start();
            CommitDbThread commitThread = new CommitDbThread();
            commitThread.start();            
        }
        
        server.start(configPath);
               
        LoadCacheProcess.reloadCache();
        MemManager.getInstance().loadDefaultSetup();
        log.info("================> HiCache started!");
    }

}
