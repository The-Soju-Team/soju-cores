/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.hh.util.ConfigUtils;
import com.hh.web.PageFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author HienDM
 */
public class WebImpl {
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WebImpl.class.getSimpleName());
    private static WebImpl instance;
    public List<String> lstConnector = new ArrayList();
    private static HashMap<String, PageFactory> pageFactory = new HashMap();
    private static HashMap<String, String> actionConnector = new HashMap();
    
    public static WebImpl getInstance() {
        if (instance == null) {
            instance = new WebImpl();
        }
        return instance;
    }    
    
    public void initWebHandler() {
        reload();
    }
    
    public void reload() {
        initConnector();
        initPages();
        initActions();
    }
    
    public void initConnector() {
        lstConnector = new ArrayList();
        int count = 1;
        while (!HHServer.config.getConnector("connector" + count).isEmpty()) {
            lstConnector.add(HHServer.config.getConnector("connector" + count));
            count++;
        }
    }
    
    public void initPages() {
        pageFactory = new HashMap();
        for (String connectorName : lstConnector) {
            int count = 1;
            while (!HHServer.config.getPage(connectorName + ".page" + count).isEmpty()) {
                String page = HHServer.config.getPage(connectorName + ".page" + count);
                String template = HHServer.config.getPage(connectorName + ".page" + count + ".template");
                if(template != null && !template.isEmpty()) {
                    PageFactory pf = new PageFactory(template);
                    int childCount = 1;
                    while (!HHServer.config.getPage(connectorName + ".page" + count + ".child" + childCount).isEmpty()) {
                        String[] childPage = HHServer.config.getPage(connectorName + ".page" + count + ".child" + childCount).split(",");
                        pf.addChildPage(childPage[0], childPage[1]);
                        childCount++;
                    }
                    addPageFactory(page, pf);
                }
                count++;
            }
        }
    }
    
    public void initActions() {
        actionConnector = new HashMap();
        for (String connectorName : lstConnector) {
            int count = 1;
            while (!HHServer.config.getPage(connectorName + ".page" + count).isEmpty()) {
                String action = HHServer.config.getPage(connectorName + ".page" + count);
                addActionConnector(action, connectorName);
                count++;
            }
        }
    }
    
    public void addPageFactory(String page, PageFactory factory) {
        pageFactory.put(page, factory);
    }
    
    public void addActionConnector(String action, String connector) {
        actionConnector.put(action, connector);
    }    
    
    public static PageFactory getPageFactory(String page) {
        return pageFactory.get(page);
    }
    
    public static String getConnectorFromAction(String action) {
        return actionConnector.get(action);
    }
}
