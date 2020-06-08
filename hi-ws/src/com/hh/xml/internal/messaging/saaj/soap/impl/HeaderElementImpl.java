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

package com.hh.xml.internal.messaging.saaj.soap.impl;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.*;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public abstract class HeaderElementImpl
    extends ElementImpl
    implements SOAPHeaderElement {

    protected static Name RELAY_ATTRIBUTE_LOCAL_NAME =
        NameImpl.createFromTagName("relay");
    protected static Name MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME =
        NameImpl.createFromTagName("mustUnderstand");

    public HeaderElementImpl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }
    public HeaderElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    protected abstract NameImpl getActorAttributeName();
    protected abstract NameImpl getRoleAttributeName();
    protected abstract NameImpl getMustunderstandAttributeName();
    protected abstract boolean getMustunderstandAttributeValue(String str);
    protected abstract String getMustunderstandLiteralValue(boolean mu);
    protected abstract NameImpl getRelayAttributeName();
    protected abstract boolean getRelayAttributeValue(String str);
    protected abstract String getRelayLiteralValue(boolean mu);
    protected abstract String getActorOrRole();


    public void setParentElement(SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPHeader)) {
            log.severe("SAAJ0130.impl.header.elem.parent.mustbe.header");
            throw new SOAPException("Parent of a SOAPHeaderElement has to be a SOAPHeader");
        }

        super.setParentElement(element);
    }

    public void setActor(String actorUri) {
        try {
            removeAttribute(getActorAttributeName());
            addAttribute((Name) getActorAttributeName(), actorUri);
        } catch (SOAPException ex) {
        }
    }

    //SOAP 1.2 supports Role
    public void setRole(String roleUri) throws SOAPException {
        // runtime exception thrown if called for SOAP 1.1
        removeAttribute(getRoleAttributeName());
        addAttribute((Name) getRoleAttributeName(), roleUri);
    }


    Name actorAttNameWithoutNS = NameImpl.createFromTagName("actor");

    public String getActor() {
        String actor = getAttributeValue(getActorAttributeName());
        return actor;
    }

    Name roleAttNameWithoutNS = NameImpl.createFromTagName("role");

    public String getRole() {
        // runtime exception thrown for 1.1
        String role = getAttributeValue(getRoleAttributeName());
        return role;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        try {
            removeAttribute(getMustunderstandAttributeName());
            addAttribute(
                (Name) getMustunderstandAttributeName(),
                getMustunderstandLiteralValue(mustUnderstand));
        } catch (SOAPException ex) {
        }
    }

    public boolean getMustUnderstand() {
        String mu = getAttributeValue(getMustunderstandAttributeName());

        if (mu != null)
            return getMustunderstandAttributeValue(mu);

        return false;
    }

    public void setRelay(boolean relay) throws SOAPException {
        // runtime exception thrown for 1.1
        removeAttribute(getRelayAttributeName());
        addAttribute(
            (Name) getRelayAttributeName(),
            getRelayLiteralValue(relay));
    }

    public boolean getRelay() {
        String mu = getAttributeValue(getRelayAttributeName());
        if (mu != null)
            return getRelayAttributeValue(mu);

        return false;
    }
}
