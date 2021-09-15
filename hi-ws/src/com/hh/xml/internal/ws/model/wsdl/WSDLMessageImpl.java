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

package com.hh.xml.internal.ws.model.wsdl;

import com.hh.xml.internal.ws.api.model.wsdl.WSDLMessage;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;

/**
 * Provides abstraction for wsdl:message
 * @author Vivek Pandey
 */
public final class WSDLMessageImpl extends AbstractExtensibleImpl implements WSDLMessage {
    private final QName name;
    private final ArrayList<WSDLPartImpl> parts;

    /**
     * @param name wsdl:message name attribute value
     */
    public WSDLMessageImpl(XMLStreamReader xsr,QName name) {
        super(xsr);
        this.name = name;
        this.parts = new ArrayList<WSDLPartImpl>();
    }

    public QName getName() {
        return name;
    }

    public void add(WSDLPartImpl part){
        parts.add(part);
    }

    public Iterable<WSDLPartImpl> parts(){
        return parts;
    }
}
