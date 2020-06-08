/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.container.run;

/**
 *
 * @author HienDM
 */
public class StopApp {
    
    public static void main(String[] args) throws Exception {
        Container.execute("stop");
        System.exit(0);
    }
    
}
