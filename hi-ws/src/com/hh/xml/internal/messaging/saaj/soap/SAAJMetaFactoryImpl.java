/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.hh.xml.internal.messaging.saaj.soap;



import com.hh.webservice.soap.MessageFactory;
import com.hh.webservice.soap.SAAJMetaFactory;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPFactory;
import com.hh.webservice.soap.SOAPConstants;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;

public class SAAJMetaFactoryImpl extends SAAJMetaFactory {

    protected static final Logger log =
        Logger.getLogger(LogDomainConstants.SOAP_DOMAIN,
                         "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");

    protected  MessageFactory newMessageFactory(String protocol)
        throws SOAPException {
        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(protocol)) {
              return new com.hh.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl();
        } else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(protocol)) {
              return new com.hh.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl();
        } else if (SOAPConstants.DYNAMIC_SOAP_PROTOCOL.equals(protocol)) {
              return new com.hh.xml.internal.messaging.saaj.soap.dynamic.SOAPMessageFactoryDynamicImpl();
        } else {
            log.log(
                Level.SEVERE,
                "SAAJ0569.soap.unknown.protocol",
                new Object[] {protocol, "MessageFactory"});
            throw new SOAPException("Unknown Protocol: " + protocol +
                                        "  specified for creating MessageFactory");
        }
    }

    protected  SOAPFactory newSOAPFactory(String protocol)
        throws SOAPException {
        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(protocol)) {
            return new com.hh.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl();
        } else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(protocol)) {
            return new com.hh.xml.internal.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl();
        } else if (SOAPConstants.DYNAMIC_SOAP_PROTOCOL.equals(protocol)) {
            return new com.hh.xml.internal.messaging.saaj.soap.dynamic.SOAPFactoryDynamicImpl();
        } else {
            log.log(
                Level.SEVERE,
                "SAAJ0569.soap.unknown.protocol",
                new Object[] {protocol, "SOAPFactory"});
            throw new SOAPException("Unknown Protocol: " + protocol +
                                        "  specified for creating SOAPFactory");
        }
    }

}
