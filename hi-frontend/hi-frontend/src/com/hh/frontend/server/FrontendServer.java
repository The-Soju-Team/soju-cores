/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.server;

import com.hh.connector.server.Server;
import com.hh.server.HHServer;
import static com.hh.server.HHServer.config;
import com.hh.socket.websocket.DefaultWebSocketConfig;
import com.hh.socket.websocket.NettyWebSocketService;
import com.hh.util.ConfigUtils;

/**
 *
 * @author HienDM
 */
public class FrontendServer {
    public static org.apache.log4j.Logger mainLogger = org.apache.log4j.Logger.getLogger(FrontendServer.class.getSimpleName());
    public static FrontendServer frontendServer;
    public HHServer hhserver;
    public Server server = new Server();
    
    public static FrontendServer getInstance() {
        if (frontendServer == null) frontendServer = new FrontendServer();
        return frontendServer;
    }
    
    public void start(String configPath) {
        try {           
            HHServer.setConfig(new ConfigUtils(configPath));
            hhserver = new HHServer();
            hhserver.setFilter(AuthenticateFilter.class)
                    .setReturnFilter(LanguageFilter.class)
                    .setConnector(server);
//                    .setRestImpl(new RestImplement())
//                    .setSoapImpl(new SoapImplement());
            
            hhserver.start();
            server.start(configPath);
            
            String wsPort = config.getConfig("websocket-port");
            if(!"".equals(wsPort)) {
                NettyWebSocketService ss = new NettyWebSocketService();
                ss.setConnector(server);
                DefaultWebSocketConfig wsc = new DefaultWebSocketConfig();
                wsc.setSslEnable(false);
                wsc.setPort(wsPort);
                ss.setConf(wsc);
                ss.start();
                mainLogger.info("Web Socket Server is run on " + wsPort);
            }    
        } catch(Exception ex) {
            mainLogger.error("HHServer error: ", ex);
        }
    }
 
    public void stop() {
        try {
            hhserver.stop();
            server.stop();
        } catch(Exception ex) {
            mainLogger.error("HHServer error: ", ex);
        }        
    }
}
