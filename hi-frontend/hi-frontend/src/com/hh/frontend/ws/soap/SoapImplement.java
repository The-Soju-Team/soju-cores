/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.ws.soap;

import com.hh.server.HHServer;
import com.hh.server.SoapImpl;
import com.hh.webservice.ws.soap.SOAPBinding;

/**
 *
 * @author HienDM
 */
public class SoapImplement extends SoapImpl {
    
    @Override
    public void initSoapService(HHServer server) throws Exception {
        //--- Service HelloWorld ---
        server.addSoapAplication(SOAPBinding.SOAP11HTTP_BINDING, "/soapapi", SoapApiImpl.class);     
        
        //--- Service GetInfo ---
        //...
        
    }
}
