/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.aerospike.process.server;

import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.aerospike.run.ServerProcess;
import com.hh.aerospike.run.StartApp;

/**
 *
 * @author HienDM
 */
public class ReceiveCallBackProcess extends ServerProcess {
    
    public ReceiveCallBackProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        String storeName = "login_" + (String) msg.get("access-token");
        StartApp.hicache.createStore(storeName);
        StartApp.hicache.setStoreAttribute(
                storeName,
                "callback-url",
                msg.get("callback-url"));
    }
}
