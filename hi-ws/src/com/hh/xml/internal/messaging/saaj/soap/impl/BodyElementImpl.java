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

/**
 * All elements of the SOAP-ENV:BODY.
 *
 * @author Anil Vijendran (akv@eng.sun.com)
 */
public abstract class BodyElementImpl
    extends ElementImpl
    implements SOAPBodyElement {

    public BodyElementImpl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }

    public BodyElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public void setParentElement(SOAPElement element) throws SOAPException {
        if (! (element instanceof SOAPBody)) {
            log.severe("SAAJ0101.impl.parent.of.body.elem.mustbe.body");
            throw new SOAPException("Parent of a SOAPBodyElement has to be a SOAPBody");
        }
        super.setParentElement(element);
    }


}
