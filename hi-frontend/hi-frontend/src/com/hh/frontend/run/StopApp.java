/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.run;

import com.hh.frontend.server.FrontendServer;

/**
 *
 * @author HienDM
 */
public class StopApp {
    
    public static void main(String[] args) {
        stop("");
    }
    
    public static void stop(String arg) {
        FrontendServer.getInstance().stop();
        System.exit(0);        
    }
    
}
