/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.authen.process.server;

import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.authen.run.ServerProcess;

/**
 *
 * @author HienDM
 */
public class SsoProcess extends ServerProcess {
    
    public SsoProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        returnStringToFrontend(msg, "");
    }
    
}
