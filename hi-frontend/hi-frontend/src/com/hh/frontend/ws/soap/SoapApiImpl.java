/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.ws.soap;

import com.google.gson.internal.LinkedTreeMap;
import com.hh.action.HttpFilter;
import com.hh.connector.netty.server.ServerDecoder;
import com.hh.frontend.server.FrontendServer;
import com.hh.net.httpserver.HttpExchange;
import com.hh.server.HHServer;
import com.hh.web.HttpUtils;
import com.hh.webservice.ws.WebServiceContext;
import com.hh.webservice.ws.handler.MessageContext;
import java.util.Date;
import javax.annotation.Resource;
import javax.jws.WebService;


@WebService(endpointInterface = "com.hh.frontend.ws.soap.SoapApi")
public class SoapApiImpl implements SoapApi {
    @Resource
    WebServiceContext wsContext;
    
    @Override
    public String postRequest(String path, String jsondata) {
        try {
            MessageContext msgx = wsContext.getMessageContext();
            HttpExchange exchange = (HttpExchange) msgx.get("com.sun.xml.internal.ws.http.exchange");
            HttpUtils hu = new HttpUtils(exchange);
            String filterClass = FrontendServer.getInstance().hhserver.handler.filterClass;
            if (filterClass != null && !filterClass.trim().isEmpty()) {
                hu.setFilter((HttpFilter) Class.forName(filterClass).newInstance());
            }
            if(hu.doFilter()) {
                LinkedTreeMap message = ServerDecoder.byteArrayToMap(jsondata.getBytes());
                hu.sendToProcess(message);
                Object lock = new Date().getTime();
                SoapImplement.cacheResponse.put(message.get("hi-message-id"), lock);
                synchronized(lock) {
                    lock.wait();
                    return (String)SoapImplement.cacheResponse.getIfPresent(message.get("hi-message-id"));
                }
            }
        } catch (Exception ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
        return "";
    }

}