/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
  
@Path("/rs/hello")    
public class HelloRest {    
    @GET
    public String get() {
        return "Welcome to Jersey Restfull!";
    }    
} 