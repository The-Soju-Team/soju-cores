package com.hh.socket.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.connector.server.Server;
import com.hh.web.HttpUtils;


public class BackendWebSocketHandler implements WebSocketHandler
{
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerDecoder.class.getSimpleName());    
    
    @Override
    public void onWebSocketConnect(WebSocket sock, final WebSocketRequest request, final Server server)
    {
        sock.onMessage(new WebSocket.Callback() {
            @Override
            public void call(WebSocket sock) {
                // Do something
                try {
                    String msg = sock.recv();
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    LinkedTreeMap obj = gson.fromJson(msg, LinkedTreeMap.class);
                    obj.put("protocol-type", "websocket");
                    obj.put("socket", sock);
                    HttpUtils hu = new HttpUtils();
                    hu.path = "/" + request.getPath();
                    hu.server = server;
                    hu.sendToProcess(obj);
                } catch (Exception ex) {
                    log.error("Error websocket convert bytes: ", ex);
                }
            }
        });
    }
}
