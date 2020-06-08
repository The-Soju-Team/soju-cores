/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.run;

import com.hh.connector.process.TimerProcess;
import com.hh.frontend.server.FrontendServer;
import com.hh.frontend.server.LanguageFilter;
import com.hh.util.ConfigUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author HienDM
 */
public class StartApp {
    public static ConfigUtils config;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        start("etc");
    }
    
    public static void start(String configPath) {
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
        
        Properties prop = new Properties();
        if(!"".equals(config.getConfig("MAX_REQ_TIME"))) prop.setProperty("DEFAULT_MAX_REQ_TIME", config.getConfig("MAX_REQ_TIME"));
        if(!"".equals(config.getConfig("MAX_RSP_TIME"))) prop.setProperty("DEFAULT_MAX_RSP_TIME", config.getConfig("MAX_RSP_TIME"));
        
        ConsoleAppender a = (ConsoleAppender) org.apache.log4j.Logger.getRootLogger().getAppender("stdout");
        a.setLayout(new PatternLayout("%d{dd/MM/yyyy HH:mm:ss} %5p [%t] %c{1}: %m%n"));        

        FrontendServer.getInstance().start(configPath);    
        LanguageFilter.loadLanguage(configPath);
    }

}
