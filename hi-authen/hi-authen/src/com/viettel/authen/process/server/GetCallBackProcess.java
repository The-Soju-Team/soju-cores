/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.StartApp;
import com.viettel.authen.run.UpdateTransToDBThread;
import java.util.UUID;

/**
 *
 * @author Ha
 */
public class GetCallBackProcess extends ServerProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GetCallBackProcess.class.getSimpleName());
    private static org.apache.log4j.Logger logKafka = org.apache.log4j.Logger.getLogger("kafkaLogger");

    public GetCallBackProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        logKafka.info((new Gson()).toJson(msg));
        String storeName = "login_" + (String) msg.get("app-token");
        log.info("Set session callback-url: " + storeName);
        String newCookie = UUID.randomUUID().toString();
        StartApp.hicache.createStore(newCookie, 86400000l);
        StartApp.hicache.setStoreAttribute(newCookie, "callback-url", storeName);
        msg.put("cookie", newCookie);
        returnRedirectToFrontend(msg, "/authen");
    }
}
