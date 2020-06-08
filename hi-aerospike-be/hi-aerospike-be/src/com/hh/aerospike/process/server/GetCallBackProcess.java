/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.aerospike.process.server;

import com.google.gson.Gson;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.aerospike.run.ServerProcess;
import com.hh.aerospike.run.StartApp;

/**
 *
 * @author HienDM
 */
public class GetCallBackProcess extends ServerProcess {
    public GetCallBackProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        String storeName = "login_" + (String) msg.get("access-token");
        LinkedTreeMap<String, String> data = new LinkedTreeMap();
        data.put("callback", (String)StartApp.hicache.getStringAttribute(storeName, "callback-url"));
        StartApp.hicache.deleteStore((String)msg.get("access-token"));
        String json = new Gson().toJson(data);
        returnStringToFrontend(msg, json);
    }
}
