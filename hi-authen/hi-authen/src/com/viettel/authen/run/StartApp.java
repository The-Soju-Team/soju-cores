/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.run;

import com.hh.cache.process.client.HiCacheSession;
import com.hh.connector.process.TimerProcess;
import com.hh.connector.server.LoadAfterReady;
import com.hh.connector.server.Server;
import com.hh.util.ConfigUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;

/**
 *
 * @author HienDM
 */
public class StartApp {
    public static ConfigUtils config;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StartApp.class.getSimpleName());
    public static Server server = new Server();
    public static HiCacheSession hicache;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        start("../etc");
    }
    
    public static void start(String configPath) throws Exception {
        config = new ConfigUtils(configPath);
        
        if("true".equals(config.getConfig("always-refresh-config"))) {
            List lstParam = new ArrayList();
            lstParam.add(configPath);
            TimerProcess timer = new TimerProcess(ReloadConfigTask.class, lstParam, 10000l);
            timer.start();
        }
        
        String logLevel = config.getConfig("log-level");
        if (logLevel.isEmpty()) {
            logLevel = "ERROR";
        }
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.toLevel(logLevel));
        
        server.setServerFilter(new AuthenFilter());
        server.start(configPath);

        ServerProcess.setHicacheConnector("hicache");
        ServerProcess.setFrontendConnector("frontend");   
        
        server.setLoadAfterReady(new LoadAfterReady() {
            @Override
            public void process() throws Exception {
                Thread.sleep(5000);
                hicache.connect("root", "root", "authen");   
                ServerProcess.defaultSetup();
            }
        });
    }
}
