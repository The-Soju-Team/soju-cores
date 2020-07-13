/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import com.viettel.authen.run.AuthenFilter;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.StartApp;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author HienDM
 */
public class LogoutProcess extends ServerProcess {
    
    public LogoutProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap message) throws Exception {
        StartApp.hicache.deleteStore((String)message.get("access-token"));
        AuthenFilter.sendCallBackURL(message);
        LinkedTreeMap data = new LinkedTreeMap();
        data.put("response_code", "TIMEOUT");
        ServerProcess.returnStringToFrontend(message, new Gson().toJson(data), server);
    }
}
