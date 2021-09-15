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
import com.hh.webservice.soap.DetailEntry;
import com.hh.webservice.soap.Name;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;

public class Detail1_1Impl extends DetailImpl {

    public Detail1_1Impl(SOAPDocumentImpl ownerDoc, String prefix) {
        super(ownerDoc, NameImpl.createDetail1_1Name(prefix));
    }
    public Detail1_1Impl(SOAPDocumentImpl ownerDoc) {
        super(ownerDoc, NameImpl.createDetail1_1Name());
    }
    protected DetailEntry createDetailEntry(Name name) {
        return new DetailEntry1_1Impl(
            (SOAPDocumentImpl) getOwnerDocument(),
            name);
    }
    protected DetailEntry createDetailEntry(QName name) {
        return new DetailEntry1_1Impl(
            (SOAPDocumentImpl) getOwnerDocument(),
            name);
    }

}
