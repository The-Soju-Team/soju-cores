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
import com.hh.webservice.soap.Name;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPException;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.DetailEntryImpl;

public class DetailEntry1_1Impl extends DetailEntryImpl {

    public DetailEntry1_1Impl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }
    public DetailEntry1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        DetailEntryImpl copy =
            new DetailEntry1_1Impl((SOAPDocumentImpl) getOwnerDocument(), newName);
        return replaceElementWithSOAPElement(this,copy);
    }
}
