/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.process.TimerTask;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HienDM
 */
public class ClearMemTask extends TimerTask {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ClearMemTask.class.getSimpleName());
    public ClearMemTask(List lstParam) {
        super(lstParam);
    }
    
    @Override
    public void process(Object message) throws Exception {
        clearMem();
    }
    
    public static void clearMem() {
        // Clear cache session
        if (MemManager.cacheSession != null) {
            List<String> lstRemove = new ArrayList();
            for (Map.Entry<String, LinkedTreeMap> entry : MemManager.cacheSession.entrySet()) {
                Object timeout = entry.getValue().get(MemManager.TIMEOUT_FIELD);
                if (timeout != null && timeout instanceof Long) {
                    if (new Date().getTime() > (Long) timeout) {
                        lstRemove.add(entry.getKey());
                    }
                }
            }
            for (String key : lstRemove) {
                MemManager.cacheSession.remove(key);
            }
        }

        // Clear cache 
        if (MemManager.cache != null) {
            List<List> lstRemove = new ArrayList();
            for (Map.Entry<String, LinkedTreeMap> entry : MemManager.cache.entrySet()) {
                LinkedTreeMap space = entry.getValue();
                for (Object item : space.entrySet()) {
                    Map.Entry<String, Object> store = (Map.Entry<String, Object>) item;
                    if(store.getValue() instanceof LinkedTreeMap) {
                        Object timeout = ((LinkedTreeMap)store.getValue()).get(MemManager.TIMEOUT_FIELD);
                        if (timeout != null && timeout instanceof Long) {
                            long currentTime = new Date().getTime();
                            if (currentTime > (Long) timeout) {
                                List lstRow = new ArrayList();
                                lstRow.add(entry.getKey());
                                lstRow.add(store.getKey());
                                lstRemove.add(lstRow);
                            }
                        }  
                    }
                }
            }
            
            for (List row : lstRemove) {
                LinkedTreeMap space = MemManager.cache.get(row.get(0));
                if(space != null) space.remove(row.get(1));
            }
        }        
    }
}
