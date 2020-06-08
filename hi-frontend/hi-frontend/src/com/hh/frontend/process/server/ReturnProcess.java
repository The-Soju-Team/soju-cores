/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.process.server;

import com.hh.connector.process.BaseProcess;
import com.hh.connector.server.Server;
import com.hh.web.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;

/**
 *
 * @author HienDM
 */
public class ReturnProcess extends BaseProcess {
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReturnProcess.class.getSimpleName());
    public ReturnProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) {
        try {
            String messageId = msg.get("hi-message-id").toString();
            HttpUtils hu = (HttpUtils) HttpUtils.popResponseMessage(messageId);
            if(hu == null) hu = new HttpUtils();
            hu.returnToClient(hu, msg);
        } catch(Exception ex) {
            log.error("Error when return client ", ex);
        }
    }
    
}
