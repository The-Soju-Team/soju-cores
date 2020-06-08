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

/**
*
* @author SAAJ RI Development Team
*/
package com.hh.xml.internal.messaging.saaj.soap.ver1_1;

import com.hh.webservice.soap.Detail;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPFault;

import javax.xml.namespace.QName;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;

public class SOAPFactory1_1Impl extends SOAPFactoryImpl {
    protected SOAPDocumentImpl createDocument() {
        return (new SOAPPart1_1Impl()).getDocument();
    }

    public Detail createDetail() throws SOAPException {
        return new Detail1_1Impl(createDocument());
    }

    public SOAPFault createFault(String reasonText, QName faultCode)
        throws SOAPException {
        if (faultCode == null) {
            throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
        }
        if (reasonText == null) {
            throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
        }
        Fault1_1Impl fault = new Fault1_1Impl(createDocument(), null);
        fault.setFaultCode(faultCode);
        fault.setFaultString(reasonText);
        return fault;
    }

    public SOAPFault createFault() throws SOAPException {
        Fault1_1Impl fault = new Fault1_1Impl(createDocument(), null);
        fault.setFaultCode(fault.getDefaultFaultCode());
        fault.setFaultString("Fault string, and possibly fault code, not set");
        return fault;
    }
}
