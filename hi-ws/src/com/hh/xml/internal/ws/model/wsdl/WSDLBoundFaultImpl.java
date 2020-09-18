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

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;

import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

/**
 * @author Vivek Pandey
 */
public class WSDLBoundFaultImpl extends AbstractExtensibleImpl implements WSDLBoundFault {
    private final String name;
    private WSDLFault fault;
    private WSDLBoundOperationImpl owner;

    public WSDLBoundFaultImpl(XMLStreamReader xsr, String name, WSDLBoundOperationImpl owner) {
        super(xsr);
        this.name = name;
        this.owner = owner;
    }

    public
    @NotNull
    String getName() {
        return name;
    }

    public QName getQName() {
        if(owner.getOperation() != null){
            return new QName(owner.getOperation().getName().getNamespaceURI(), name);
        }
        return null;
    }

    public WSDLFault getFault() {
        return fault;
    }

    @NotNull
    public WSDLBoundOperation getBoundOperation() {
        return owner;
    }

    void freeze(WSDLBoundOperationImpl root) {
        assert root != null;
        WSDLOperation op = root.getOperation();
        if (op != null) {
            for (WSDLFault f : op.getFaults()) {
                if (f.getName().equals(name)) {
                    this.fault = f;
                    break;
                }
            }
        }
    }
}
