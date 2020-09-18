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

import java.util.logging.Logger;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.Name;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPException;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class HeaderElement1_2Impl extends HeaderElementImpl {

    private static final Logger log =
        Logger.getLogger(HeaderElement1_2Impl.class.getName(),
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

    public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }
    public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        HeaderElementImpl copy =
            new HeaderElement1_2Impl((SOAPDocumentImpl)getOwnerDocument(), newName);
        return replaceElementWithSOAPElement(this,copy);
    }

    protected NameImpl getRoleAttributeName() {
        return NameImpl.create("role", null, NameImpl.SOAP12_NAMESPACE);
    }

    // Actor equivalent to Role in SOAP 1.2
    protected NameImpl getActorAttributeName() {
        return getRoleAttributeName();
    }

    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, NameImpl.SOAP12_NAMESPACE);
    }

    // mustUnderstand attribute has literal value "true" or "false"
    protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
        return (mustUnderstand == true ? "true" : "false");
    }

    protected boolean getMustunderstandAttributeValue(String mu) {
        if (mu.equals("true") || mu.equals("1"))
            return true;
        return false;
    }

   protected NameImpl getRelayAttributeName() {
        return NameImpl.create("relay", null, NameImpl.SOAP12_NAMESPACE);
    }

    //relay attribute has literal value "true" or "false"
    protected String getRelayLiteralValue(boolean relay) {
        return (relay == true ? "true" : "false");
    }

    protected boolean getRelayAttributeValue(String relay) {
        if (relay.equals("true") || relay.equals("1"))
            return true;
        return false;
    }

    protected String getActorOrRole() {
        return getRole();
    }
}
