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

import com.hh.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Vivek Pandey
 */
public final class WSDLInputImpl extends AbstractExtensibleImpl implements WSDLInput {
    private String name;
    private QName messageName;
    private WSDLOperationImpl operation;
    private WSDLMessageImpl message;
    private String action;
    private boolean defaultAction = true;

    public WSDLInputImpl(XMLStreamReader xsr,String name, QName messageName, WSDLOperationImpl operation) {
        super(xsr);
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }

    public String getName() {
        if(name != null)
            return name;

        return (operation.isOneWay())?operation.getName().getLocalPart():operation.getName().getLocalPart()+"Request";
    }

    public WSDLMessage getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    @NotNull
    public WSDLOperation getOperation() {
        return operation;
    }

    public QName getQName() {
        return new QName(operation.getName().getNamespaceURI(), getName());
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
    }

    void freeze(WSDLModelImpl parent) {
        message = parent.getMessage(messageName);
    }
}
