/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.webservice.websocket;

import com.hh.server.HHServer;
import com.hh.web.HttpUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author hiendm1
 */
public abstract class BaseWebsocket implements IWebsocketConnection {
    public HttpUtils httpUtils;
    public Socket socket; 
    
    @Override
    public void setChannel(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void setHttpUtils(HttpUtils hu) {
        this.httpUtils = hu;
    }    

    @Override
    public abstract void onReceive(byte[] data);

    @Override
    public abstract void onConnect();

    @Override
    public abstract void onDisconnect();

    @Override
    public void send(String data) {
        try {
            OutputStream os = socket.getOutputStream();
            byte[] byteData = WebsocketFrame.encodeWebsocketFrame(data.getBytes());
            os.write(byteData);
            os.flush();
        } catch (IOException ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }  
    }

    @Override
    public void send(byte[] data) {
        try {
            OutputStream os = socket.getOutputStream();
            byte[] byteData = WebsocketFrame.encodeWebsocketFrame(data);
            os.write(byteData);
            os.flush();
        } catch (IOException ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }        
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
    }
    
}
