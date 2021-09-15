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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ha
 */
public class ReceiveCallBackProcess extends ServerProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReceiveCallBackProcess.class.getSimpleName());
    private static org.apache.log4j.Logger logKafka = org.apache.log4j.Logger.getLogger("kafkaLogger");

    public ReceiveCallBackProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }

    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        logKafka.info((new Gson()).toJson(msg));
        StartApp.hicache.deleteStoreAttribute((String) msg.get("access-token"), "sso_username");
        String storeName = "login_" + (String) msg.get("access-token");
        StartApp.hicache.createStore(storeName, 86400000l);
        log.info("Receive Callback URL: " + msg.get("callback-url"));
        List lstData = new ArrayList();
        lstData.add(msg.get("callback-url"));
        lstData.add(msg.get("server-code"));
        StartApp.hicache.setStoreAttribute(
                storeName,
                "callback-url",
                lstData);
    }
}
