/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.server;

import com.hh.action.ReturnFilter;
import static com.hh.frontend.run.StartApp.config;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author HienDM
 */
public class LanguageFilter implements ReturnFilter {
    
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LanguageFilter.class.getSimpleName());
    public static Properties propVN = new Properties();
    public static Properties propUS = new Properties();
    public static String configPath;
    
    @Override
    public String execute(String content, String local) {
        try {
            log.info("cookie local: " + local);
            log.info("multi-language: " + config.getConfig("multi-language"));
            log.info("configPath: " + configPath);
            if("true".equals(config.getConfig("multi-language")) ) {
                loadLanguage(configPath);
                Set<Object> keys = getLanguage(local).keySet();
                for(Object key: keys){
                    content = content.replace("#:(" + key + "):", getLanguage(local).getProperty(key.toString()));
                }            
            }
        } catch (Exception ex) {
            FrontendServer.mainLogger.error("HHServer error: ", ex);
        } 
        return content;
    }
    
    public Properties getLanguage(String local) {
        if("US".equals(local)) return propUS;
        else return propVN;
    }
    
    public static void loadLanguage(String config) {
        configPath = config;
        String languageVN = configPath + "/language_vi_VN.properties";
        log.info("PATH_VN: " + languageVN);
        String languageUS = configPath + "/language_en_US.properties";
        log.info("PATH_US: " + languageUS);
        
        File file = new File(languageVN);
        if (file.exists()) {
            log.info("ADD LANGUAGE VN: " + file.getAbsolutePath());
            try (FileInputStream inputStream = new FileInputStream(languageVN)){
                propVN.load(inputStream);
            } catch (Exception ex) {
                log.error("Error when load process-client.conf", ex);
            }
        }        
        
        File fileUS = new File(languageUS);
        if (fileUS.exists()) {
            log.info("ADD LANGUAGE US: " + fileUS.getAbsolutePath());
            try (FileInputStream inputStream = new FileInputStream(languageUS)){
                propUS.load(inputStream);
            } catch (Exception ex) {
                log.error("Error when load process-client.conf", ex);
            }
        }                
    }
}
