/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.frontend.ws.soap;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
 
public class SoapClient{
 
    public static void main(String[] args) throws Exception {
 
	URL url = new URL("http://localhost:8070/ws/hello?wsdl");
        QName qname = new QName("http://soap.ws.frontend.hh.com/", "SoapApiImplService");
 
        Service service = Service.create(url, qname);
 
        SoapApi hello = service.getPort(SoapApi.class);
 
         System.out.println(hello.postRequest("/","{}"));
 
    }
 
}
