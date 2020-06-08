/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.client;

import com.hh.connector.server.Server;
import com.hh.util.EncryptDecryptUtils;
import java.util.ArrayList;
import com.google.gson.internal.LinkedTreeMap;
import java.util.List;

/**
 *
 * @author Ha
 */
public class Connector {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Connector.class.getSimpleName());
    public LinkedTreeMap<String, List<NettyConnection>> mapNettyConnection = new LinkedTreeMap();
    public Server server;
    
    public Connector(Server server) {
        try {
            int count = 1;
            while(!server.config.getConnector("connector" + count).isEmpty()) {
                String connectorName = server.config.getConnector("connector" + count);
                String[] hosts = server.config.getConnector("connector" + count + ".hosts").split(",");
                String[] ports = server.config.getConnector("connector" + count + ".ports").split(",");
                String userName = server.config.getConnector("connector" + count + ".username");
                String password = server.config.getConnector("connector" + count + ".password");
                String ssl = server.config.getConnector("connector" + count + ".ssl");
                List<NettyConnection> lstConnection = new ArrayList();
                for(int i = 0; i < hosts.length; i++) {
                    NettyConnection connection = new NettyConnection(hosts[i], Integer.parseInt(ports[i]), ssl, server);
                    connection.connector = connectorName;
                    if(userName != null && !userName.isEmpty()) {
                        connection.token = EncryptDecryptUtils.encodeSHA256(userName + password);
                    }
                    lstConnection.add(connection);
                }
                mapNettyConnection.put(connectorName, lstConnection);
                count++;
            }
        } catch(Exception ex) {
            log.error("Error when init connector", ex);
        }
    }
    
    public boolean send(LinkedTreeMap message, String connectorName) {
        try {
            Integer id = 0;
            if(message.get("hi-message-id") != null) 
                id = Integer.parseInt(message.get("hi-message-id").toString());
            else 
                id = Integer.parseInt(message.get("id").toString());
            List<NettyConnection> lstConnection = mapNettyConnection.get(connectorName);
            log.info("Connection: " + lstConnection);
            log.info("Connection name : " + connectorName);
            int index = id % lstConnection.size();
            NettyConnection connection = lstConnection.get(index);
            if(!connection.isActive) {
                for(int i = 1; i <= lstConnection.size(); i++) {
                    int newIndex = (index + i) % lstConnection.size();
                    connection = lstConnection.get(newIndex);
                    if(connection.isActive) break;
                }
            }
            if(connection != null && connection.isActive) {
                connection.send(message);
                return true;
            }
            else log.error("Cannot get connection to send");
        } catch(Exception ex) {
            log.error("Error when send to connector", ex);
        }
        return false;
    }
    
}
