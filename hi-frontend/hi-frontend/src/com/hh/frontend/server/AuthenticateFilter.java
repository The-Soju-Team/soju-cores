/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.server;

import com.hh.web.HttpUtils;
import com.hh.action.HttpFilter;
import java.util.List;

/**
 *
 * @author HienDM
 */
public class AuthenticateFilter implements HttpFilter {
    @Override
    public boolean execute(HttpUtils hu) {
        try {
            if(hu.httpExchange.getRequestHeaders().get(HttpUtils.ACCESS_TOKEN) != null) {
                List<String> lstToken = hu.httpExchange.getRequestHeaders().get(HttpUtils.ACCESS_TOKEN);
                String accessToken = null;
                if(lstToken != null) accessToken = lstToken.get(0);
                if(accessToken == null) accessToken = hu.getHHCookie();
                if(accessToken != null) {
                    hu.parameters.put(HttpUtils.ACCESS_TOKEN, accessToken);
                }
            }
        } catch (Exception ex) {
            FrontendServer.mainLogger.error("HHServer error: ", ex);
        } 
        return true;
    }
}
