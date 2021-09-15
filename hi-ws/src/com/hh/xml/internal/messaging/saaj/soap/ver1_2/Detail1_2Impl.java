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
import com.hh.webservice.soap.*;

import com.hh.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.hh.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class Detail1_2Impl extends DetailImpl {

    protected static final Logger log =
        Logger.getLogger(Detail1_2Impl.class.getName(),
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

    public Detail1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createSOAP12Name("Detail", prefix));
    }

    public Detail1_2Impl(SOAPDocumentImpl ownerDocument) {
        super(ownerDocument, NameImpl.createSOAP12Name("Detail"));
    }

    protected DetailEntry createDetailEntry(Name name) {
        return new DetailEntry1_2Impl(
            ((SOAPDocument) getOwnerDocument()).getDocument(),
            name);
    }

    protected DetailEntry createDetailEntry(QName name) {
        return new DetailEntry1_2Impl(
            ((SOAPDocument) getOwnerDocument()).getDocument(),
            name);
    }

    /*
     * Override setEncodingStyle of ElementImpl to restrict adding encodingStyle
     * attribute to SOAP Detail (SOAP 1.2 spec, part 1, section 5.1.1)
     */
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0403.ver1_2.no.encodingStyle.in.detail");
        throw new SOAPExceptionImpl("EncodingStyle attribute cannot appear in Detail");
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
