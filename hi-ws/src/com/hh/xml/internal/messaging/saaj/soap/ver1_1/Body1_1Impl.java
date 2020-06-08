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

import java.util.Locale;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.*;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.BodyImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class Body1_1Impl extends BodyImpl {
    public Body1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
            super(ownerDocument, NameImpl.createBody1_1Name(prefix));
    }

    public SOAPFault addSOAP12Fault(QName faultCode, String faultReason, Locale locale) {
        // log message here
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    protected NameImpl getFaultName(String name) {
        // Ignore name
        return NameImpl.createFault1_1Name(null);
    }

    protected SOAPBodyElement createBodyElement(Name name) {
        return new BodyElement1_1Impl(
            ((SOAPDocument) getOwnerDocument()).getDocument(),
            name);
    }

    protected SOAPBodyElement createBodyElement(QName name) {
        return new BodyElement1_1Impl(
            ((SOAPDocument) getOwnerDocument()).getDocument(),
            name);
    }

    protected QName getDefaultFaultCode() {
        return new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Server");
    }

    protected boolean isFault(SOAPElement child) {
        // SOAP 1.1 faults always use the default name
        return child.getElementName().equals(getFaultName(null));
    }

    protected SOAPFault createFaultElement() {
        return new Fault1_1Impl(
            ((SOAPDocument) getOwnerDocument()).getDocument(), getPrefix());
    }

}
