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
import com.hh.webservice.soap.DetailEntry;
import com.hh.webservice.soap.Name;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;

public abstract class DetailEntryImpl
    extends ElementImpl
    implements DetailEntry {
    public DetailEntryImpl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }
    public DetailEntryImpl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }
}
