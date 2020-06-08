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
package com.hh.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.Name;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.hh.xml.internal.messaging.saaj.SOAPExceptionImpl;

public class FaultElement1_2Impl extends FaultElementImpl {

    public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
        super(ownerDoc, qname);
    }

    public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, String localName) {
        super(ownerDoc, NameImpl.createSOAP12Name(localName));
    }

    protected boolean isStandardFaultElement() {
        String localName = elementQName.getLocalPart();
        if (localName.equalsIgnoreCase("code") ||
            localName.equalsIgnoreCase("reason") ||
            localName.equalsIgnoreCase("node") ||
            localName.equalsIgnoreCase("role")) {
            return true;
        }
        return false;
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        if (!isStandardFaultElement()) {
            FaultElement1_2Impl copy =
                new FaultElement1_2Impl((SOAPDocumentImpl) getOwnerDocument(), newName);
            return replaceElementWithSOAPElement(this,copy);
        } else {
            return super.setElementQName(newName);
        }
    }

    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0408.ver1_2.no.encodingStyle.in.fault.child");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on a Fault child element");
    }

    public SOAPElement addAttribute(Name name, String value)
        throws SOAPException {
        if (name.getLocalName().equals("encodingStyle")
            && name.getURI().equals(NameImpl.SOAP12_NAMESPACE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }

    public SOAPElement addAttribute(QName name, String value)
        throws SOAPException {
        if (name.getLocalPart().equals("encodingStyle")
            && name.getNamespaceURI().equals(NameImpl.SOAP12_NAMESPACE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
}
