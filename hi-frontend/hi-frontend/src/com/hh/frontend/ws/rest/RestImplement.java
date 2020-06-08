/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.ws.rest;

import com.hh.server.HHServer;
import com.hh.server.RestImpl;

/**
 *
 * @author HienDM
 */
public class RestImplement extends RestImpl {
    
    @Override
    public void initRestService(HHServer server) {
        //--- Service HelloRest ---
//        server.addRsAplication("/hello", HelloRest.class);
        
        //--- Service Getinfo ---
        //...
    }
}
