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
import java.util.logging.Level;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.*;

import com.hh.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class Envelope1_2Impl extends EnvelopeImpl {

    protected static final Logger log =
        Logger.getLogger(Envelope1_2Impl.class.getName(),
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

    public Envelope1_2Impl(SOAPDocumentImpl ownerDoc, String prefix) {
        super(ownerDoc, NameImpl.createEnvelope1_2Name(prefix));
    }

    public Envelope1_2Impl(
        SOAPDocumentImpl ownerDoc,
        String prefix,
        boolean createHeader,
        boolean createBody)
        throws SOAPException {
        super(
            ownerDoc,
            NameImpl.createEnvelope1_2Name(prefix),
            createHeader,
            createBody);
    }

    protected NameImpl getBodyName(String prefix) {
        return NameImpl.createBody1_2Name(prefix);
    }

    protected NameImpl getHeaderName(String prefix) {
        return NameImpl.createHeader1_2Name(prefix);
    }

    /*
     * Override setEncodingStyle of ElementImpl to restrict adding encodingStyle
     * attribute to SOAP Envelope (SOAP 1.2 spec, part 1, section 5.1.1)
     */
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0404.ver1_2.no.encodingStyle.in.envelope");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Envelope");
    }

    /*
     * Override addAttribute of ElementImpl to restrict adding encodingStyle
     * attribute to SOAP Envelope (SOAP 1.2 spec, part 1, section 5.1.1)
     */
    public SOAPElement addAttribute(Name name, String value)
        throws SOAPException {
        if (name.getLocalName().equals("encodingStyle")
            && name.getURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }

    public SOAPElement addAttribute(QName name, String value)
        throws SOAPException {
        if (name.getLocalPart().equals("encodingStyle")
            && name.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }


    /*
     * Override addChildElement method to ensure that no element
     * is added after body in SOAP 1.2.
     */
    public SOAPElement addChildElement(Name name) throws SOAPException {
        // check if body already exists
        if (getBody() != null) {
            log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl(
                "Body must be the last element in" + " SOAP Envelope");
        }
        return super.addChildElement(name);
    }

    public SOAPElement addChildElement(QName name) throws SOAPException {
        // check if body already exists
        if (getBody() != null) {
            log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl(
                "Body must be the last element in" + " SOAP Envelope");
        }
        return super.addChildElement(name);
    }


    /*
     * Ideally we should be overriding other addChildElement() methods as well
     * but we are not adding them here since internally all those call the
     * method addChildElement(Name name).
     * In future, if this behaviour changes, then we would need to override
     * all the rest of them as well.
     *
     */

    public SOAPElement addTextNode(String text) throws SOAPException {
        log.log(
            Level.SEVERE,
            "SAAJ0416.ver1_2.adding.text.not.legal",
            getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Envelope is not legal");
    }
}
