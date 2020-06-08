package com.hh.web;

import com.hh.connector.server.Server;
import com.hh.server.HHServer;
import com.hh.server.WebImpl;
import com.hh.util.FileUtils;
import java.io.File;
import java.net.URI;
import java.util.HashMap;

public class ServerActor {
    private static HashMap mapConfig;
    private HttpUtils httpUtil;
    
    public ServerActor(HttpUtils hu, Server server) {
        this.httpUtil = hu;
        this.httpUtil.server = server;
    }
    
    public void onReceive() {
        try {
            handleRequest();
        } catch(Exception ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
//            try {
//                httpUtil.sendStringResponse(404, "HHServer error (ServerActor.java:27)");
//            } catch (Exception e) {
//                HHServer.mainLogger.error("HHServer error: ", e);
//            }
        } finally {
            if(httpUtil.isAutoClose() && httpUtil.httpExchange != null) {
                httpUtil.httpExchange.close();
                httpUtil.httpExchange = null;
            }
        }
    }
    
    private void handleRequest() throws Exception {
        URI original = httpUtil.httpExchange.getRequestURI();
        String path = "";
        if (original.getRawPath() != null) {
            path = original.getRawPath();
        }

        httpUtil.path = path;
        httpUtil.contextPath = path.substring(0, path.indexOf("/"));
        String webAction = WebImpl.getConnectorFromAction(path);

        if(webAction == null) { // Nếu không phải action
            String sharePath = "";
            if(path.length() > 0) sharePath = path.substring(1);
            if(FileUtils.checkSafeFileName(path) 
                    && sharePath.contains("/")
                    && (sharePath.length() > sharePath.indexOf("/") + 7)                     
                    && sharePath.substring(sharePath.indexOf("/"), sharePath.indexOf("/") + 7).equals("/share/")) { 
                //Nếu là file javascript, css, image...
                File file = new File("../app" + path);
                if (!file.isFile()) {
                    httpUtil.sendNotFoundResponse();
                } else {
                    httpUtil.sendStaticFile(file, path);
                }
            } else {
                httpUtil.sendNotFoundResponse();
            }
        } else {
            if (!httpUtil.doFilter()) return;
            httpUtil.sendToProcess(path);
        }
    }
}
