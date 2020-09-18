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

import com.hh.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPortType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.util.Hashtable;
import java.util.Map;

/**
 * Provides implementation of {@link WSDLPortType}
 *
 * @author Vivek Pandey
 */
public final class WSDLPortTypeImpl  extends AbstractExtensibleImpl implements WSDLPortType {
    private QName name;
    private final Map<String, WSDLOperationImpl> portTypeOperations;
    private WSDLModelImpl owner;

    public WSDLPortTypeImpl(XMLStreamReader xsr,WSDLModelImpl owner, QName name) {
        super(xsr);
        this.name = name;
        this.owner = owner;
        portTypeOperations = new Hashtable<String, WSDLOperationImpl>();
    }

    public QName getName() {
        return name;
    }

    public WSDLOperationImpl get(String operationName) {
        return portTypeOperations.get(operationName);
    }

    public Iterable<WSDLOperationImpl> getOperations() {
        return portTypeOperations.values();
    }

    /**
     * Populates the Map that holds operation name as key and {@link WSDLOperation} as the value.
     * @param opName Must be non-null
     * @param ptOp  Must be non-null
     * @throws NullPointerException if either opName or ptOp is null
     */
    public void put(String opName, WSDLOperationImpl ptOp){
        portTypeOperations.put(opName, ptOp);
    }

    WSDLModelImpl getOwner(){
        return owner;
    }

    void freeze() {
        for(WSDLOperationImpl op : portTypeOperations.values()){
            op.freez(owner);
        }
    }
}
