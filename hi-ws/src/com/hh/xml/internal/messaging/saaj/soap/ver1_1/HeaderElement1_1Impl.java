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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.Name;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPElement;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;

public class HeaderElement1_1Impl extends HeaderElementImpl {

    protected static final Logger log =
        Logger.getLogger(LogDomainConstants.SOAP_VER1_1_DOMAIN,
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

    public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }
    public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        HeaderElementImpl copy =
            new HeaderElement1_1Impl((SOAPDocumentImpl) getOwnerDocument(), newName);
        return replaceElementWithSOAPElement(this,copy);
    }

    protected NameImpl getActorAttributeName() {
        return NameImpl.create("actor", null, NameImpl.SOAP11_NAMESPACE);
    }

    // role not supported by SOAP 1.1
    protected NameImpl getRoleAttributeName() {
        log.log(
            Level.SEVERE,
            "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1",
            new String[] { "Role" });
        throw new UnsupportedOperationException("Role not supported by SOAP 1.1");
    }

    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, NameImpl.SOAP11_NAMESPACE);
    }

    // mustUnderstand attribute has literal value "1" or "0"
    protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
        return (mustUnderstand == true ? "1" : "0");
    }

    protected boolean getMustunderstandAttributeValue(String mu) {
        if ("1".equals(mu) || "true".equalsIgnoreCase(mu))
            return true;
        return false;
    }

    // relay not supported by SOAP 1.1
    protected NameImpl getRelayAttributeName() {
        log.log(
            Level.SEVERE,
            "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1",
            new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    protected String getRelayLiteralValue(boolean relayAttr) {
        log.log(
            Level.SEVERE,
            "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1",
            new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    protected boolean getRelayAttributeValue(String mu) {
        log.log(
            Level.SEVERE,
            "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1",
            new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    protected String getActorOrRole() {
        return getActor();
    }

}
