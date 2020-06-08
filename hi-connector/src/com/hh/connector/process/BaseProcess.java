/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.process;

import akka.actor.UntypedActor;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author HienDM
 */
public class BaseProcess extends UntypedActor {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaseProcess.class.getSimpleName());
    public ChannelHandlerContext ctx;
    public Server server;
    
    public BaseProcess(ChannelHandlerContext ctx, Server server) {
        this.ctx = ctx;
        this.server = server;
    }
    
    @Override
    public void onReceive(Object obj) {
        try {        
            try {
                LinkedTreeMap message = (LinkedTreeMap)obj;
                process(message);
            } catch(Exception ex) {
                log.error("Error receive message", ex);
                try {
                    getContext().stop(getSelf());
                } catch (Exception e) {}            
            } 
        } catch (Throwable e) {
            log.error("Co loi Fatal ", e);
        }   
    }
    
    public void process(LinkedTreeMap msg) throws Exception {
        
    }
}
