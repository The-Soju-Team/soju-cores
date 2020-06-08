/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.hh.action.BaseAction;
import com.hh.action.DefaultReturnFilter;
import com.hh.web.ServerActor;
import com.hh.webservice.websocket.WebsocketActor;
import com.hh.web.HttpUtils;
import com.hh.net.httpserver.HttpExchange;
import com.hh.net.httpserver.HttpHandler;
import com.hh.webservice.jersey.HandlerContainer;
import com.hh.webservice.websocket.IWebsocketConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Application;
import com.hh.action.HttpFilter;
import com.hh.action.ReturnFilter;
import com.hh.connector.server.Server;
import java.util.UUID;

/**
 *
 * @author vtsoft
 */
public class BaseHandler implements HttpHandler {
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaseHandler.class.getSimpleName());
    public String filterClass;
    public String returnFilterClass;
    private String wsConnectionClass;
    public HashMap<String, HandlerContainer> rsPath = new HashMap();
    public Server server = null;

    public BaseHandler() {
    }

    public void setFilter(String filterClass) {
        this.filterClass = filterClass;
    }
    
    public void setReturnFilter(String filterClass) {
        this.returnFilterClass = filterClass;
    }    

    public void setWebsocketConnection(String wsConnectionClass) {
        this.wsConnectionClass = wsConnectionClass;
    }

    public void addRestHandler(String path, Application app) {
        HandlerContainer hc = new HandlerContainer(app);
        rsPath.put(path, hc);
    }
    
    public void setConnector(Server server) {
        this.server = server;
    }    

    @Override
    public void handle(HttpExchange he) {
        HttpUtils hu = new HttpUtils(he);
        try {
            URI original = he.getRequestURI();
            String path = "";
            if (original.getRawPath() != null) {
                path = original.getRawPath();
            }
            
            if(path.equals(HHServer.config.getConfig("reload-browser-cache-action"))) {
                HHServer.reloadBrowserCache();
                (new BaseAction(hu)).returnString("Reload browser cache successful!");
                return;
            }
                        
            if(hu.httpExchange.getRequestHeaders().get(HttpUtils.ACCESS_TOKEN) == null) {
                if(hu.getHHCookie() == null || hu.getHHCookie().trim().isEmpty()) {
                    String accessToken = UUID.randomUUID().toString();
                    hu.httpExchange.getResponseHeaders().set(HttpUtils.ACCESS_TOKEN, accessToken);
                    hu.addHHCookie(accessToken, true, "true".equals(HHServer.config.getConfig("http-ssl")));
                }
            }
            
            // Neu la web service restfull
            if (path.startsWith("/rs/")) {
                if (filterClass != null && !filterClass.trim().isEmpty()) {
                    hu.setFilter((HttpFilter) Class.forName(filterClass).newInstance());
                }
                if (!hu.doFilter()) {
                    return;
                }

                for (Map.Entry<String, HandlerContainer> entry : rsPath.entrySet()) {
                    if (path.startsWith(entry.getKey())) {
                        entry.getValue().handle(he);
                        break;
                    }
                }
            } else { 
                List<String> wsHeader = he.getRequestHeaders().get("upgrade");
                // Neu la web socket
                if (wsHeader != null && !wsHeader.isEmpty() && wsHeader.get(0).equals("websocket")) {
                    if (wsConnectionClass != null && !wsConnectionClass.trim().isEmpty()) {
                        IWebsocketConnection wsConnection = (IWebsocketConnection) Class.forName(wsConnectionClass).newInstance();
                        hu.setWebsocketConnection(wsConnection);
                        wsConnection.setHttpUtils(hu);
//                        HashMap ssoUser = (HashMap) hu.getSessionAttribute("sso_username");
//                        hu.setSessionAttribute("ws_id" + ssoUser.get("user_id"), wsConnection);
                    }
                    hu.server = server;
                    hu.path = hu.httpExchange.getRequestURI().getRawPath();
                    WebsocketActor wa = new WebsocketActor(hu);
                    wa.onReceive();
                } else { // Neu la HttpRequest thuong
                    if (filterClass != null && !filterClass.trim().isEmpty()) {
                        hu.setFilter((HttpFilter) Class.forName(filterClass).newInstance());
                    }
                    
                    if (returnFilterClass != null && !returnFilterClass.trim().isEmpty()) {
                        hu.setReturnFilter((ReturnFilter) Class.forName(returnFilterClass).newInstance());
                    } else {
                        hu.setReturnFilter(new DefaultReturnFilter());
                    }   
                    
                    ServerActor sa = new ServerActor(hu, server);
                    sa.onReceive();
                }
            }
        } catch (Exception ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
    }
}
