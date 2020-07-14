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
import com.hh.rdbms.DbcpConnector;
import com.hh.util.ConfigUtils;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author HienDM
 */
public class StartApp {
    public static ConfigUtils config;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StartApp.class.getSimpleName());
    public static Server server = new Server();
    public static HiCacheSession hicache;
    public static DbcpConnector database;    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        start("etc");
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
        ConsoleAppender a = (ConsoleAppender) org.apache.log4j.Logger.getRootLogger().getAppender("stdout");
        a.setLayout(new PatternLayout("%d{dd/MM/yyyy HH:mm:ss} %5p [%t] %c{1}: %m%n"));     
        
        database = new DbcpConnector(configPath + "/database.conf");
        database.start();   
        
//        server.setServerFilter(new AuthenFilter());
        server.start(configPath);

        ServerProcess.setHicacheConnector("hicache");
        ServerProcess.setFrontendConnector("frontend");   
        
        UpdateTransToDBThread t = new UpdateTransToDBThread();
        t.start();
        
        server.setLoadAfterReady(new LoadAfterReady() {
            @Override
            public void process() throws Exception {
                Thread.sleep(5000);
                hicache.connect("root", "root", "authen");
                ServerProcess.defaultSetup();
                if("1".equals(config.getConfig("update-credential"))) {
                    ServerProcess.updateCredentialFromDatabase();            
                }

                (new Thread(new Runnable(){

                    @Override
                    public void run() {
                        while (true) {
                            ServerProcess.loadIPBasedPermissionsFromDatabase();
                            try {
                                Thread.sleep(60000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })).start();
            }
        });
                
//        Properties props = new Properties();
//        props.load(new FileInputStream(configPath + "/log4j.properties"));
//        PropertyConfigurator.configure(props);               
    }
}
