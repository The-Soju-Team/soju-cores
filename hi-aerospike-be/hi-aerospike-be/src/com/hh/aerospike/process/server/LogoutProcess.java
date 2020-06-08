/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.aerospike.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.aerospike.run.AuthenFilter;
import com.hh.aerospike.run.ServerProcess;
import com.hh.aerospike.run.StartApp;
import com.hh.connector.server.Server;
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
