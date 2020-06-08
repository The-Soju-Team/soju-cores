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

import javax.xml.namespace.QName;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPException;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class FaultElement1_1Impl extends FaultElementImpl {

    public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
        super(ownerDoc, qname);
    }

    public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc,
                               String localName) {
        super(ownerDoc, NameImpl.createFaultElement1_1Name(localName));
    }

    public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc,
                               String localName,
                               String prefix) {
        super(ownerDoc,
              NameImpl.createFaultElement1_1Name(localName, prefix));
    }

    protected boolean isStandardFaultElement() {
        String localName = elementQName.getLocalPart();
        if (localName.equalsIgnoreCase("faultcode") ||
            localName.equalsIgnoreCase("faultstring") ||
            localName.equalsIgnoreCase("faultactor")) {
            return true;
        }
        return false;
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        if (!isStandardFaultElement()) {
            FaultElement1_1Impl copy =
                new FaultElement1_1Impl((SOAPDocumentImpl) getOwnerDocument(), newName);
            return replaceElementWithSOAPElement(this,copy);
        } else {
            return super.setElementQName(newName);
        }
    }
}
