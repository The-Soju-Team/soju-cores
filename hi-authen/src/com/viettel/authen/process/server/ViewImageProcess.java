/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.StartApp;
import com.hh.connector.server.Server;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Ha
 */
public class ViewImageProcess extends ServerProcess {

    public ViewImageProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }

    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        String fileName = (String) msg.get("filename");
        if (fileName == null) {
            LinkedTreeMap<String, String> data = new LinkedTreeMap();
            data.put("data", "404 (Not Found)");
            String json = new Gson().toJson(data);
            returnStringToFrontend(msg, json);
            return;
        }
        String filePath = "";
        if (fileName.contains("_")) {
            String[] arrData = fileName.split("_");
            long createTime = Long.parseLong(arrData[0]);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(createTime));
            filePath = StartApp.config.getConfig("DataDirectory") + File.separator + "user";
            filePath = filePath.replace("/", File.separator);
            filePath = filePath.replace("\\", File.separator);
            filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator
                    + (c.get(Calendar.MONTH) + 1) + File.separator + c.get(Calendar.DAY_OF_MONTH)
                    + File.separator;
            filePath = filePath + fileName;
        }
        File tmpFile = new File(filePath);
        if (!tmpFile.exists()) {
            filePath = StartApp.config.getConfig("DataDirectory") + File.separator + "user" + File.separator + "no-image.jpg";
        }
        returnFileToFrontend(msg, filePath);
    }
}
