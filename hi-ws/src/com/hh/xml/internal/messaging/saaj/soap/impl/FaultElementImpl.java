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

import java.util.logging.Level;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPFaultElement;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public abstract class FaultElementImpl
    extends ElementImpl
    implements SOAPFaultElement {

    protected FaultElementImpl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
        super(ownerDoc, qname);
    }

    protected FaultElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    protected abstract boolean isStandardFaultElement();

    public SOAPElement setElementQName(QName newName) throws SOAPException {
            log.log(Level.SEVERE,
                    "SAAJ0146.impl.invalid.name.change.requested",
                    new Object[] {elementQName.getLocalPart(),
                                  newName.getLocalPart()});
            throw new SOAPException("Cannot change name for "
                                    + elementQName.getLocalPart() + " to "
                                    + newName.getLocalPart());
    }

}
