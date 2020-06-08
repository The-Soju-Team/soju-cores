/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.webservice.websocket;

import com.hh.web.HttpUtils;
import java.net.Socket;

/**
 *
 * @author hiendm1
 */
public interface IWebsocketConnection {
    public abstract void onReceive(byte[] data);
    
    public abstract void onConnect();
    
    public abstract void onDisconnect();
    
    public void send(String data);
    
    public void send(byte[] data);
    
    public void disconnect();
    
    public void setChannel(Socket socket);
    
    public void setHttpUtils(HttpUtils hu);
}
