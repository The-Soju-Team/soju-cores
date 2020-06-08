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
import com.hh.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.hh.xml.internal.ws.util.QNameMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementaiton of {@link WSDLOperation}
 *
 * @author Vivek Pandey
 */
public final class WSDLOperationImpl extends AbstractExtensibleImpl implements WSDLOperation {
    private final QName name;
    private String parameterOrder;
    private WSDLInputImpl input;
    private WSDLOutputImpl output;
    private final List<WSDLFaultImpl> faults;
    private final QNameMap<WSDLFaultImpl> faultMap;
    protected Iterable<WSDLMessageImpl> messages;
    private final WSDLPortType owner;

    public WSDLOperationImpl(XMLStreamReader xsr,WSDLPortTypeImpl owner, QName name) {
        super(xsr);
        this.name = name;
        this.faults = new ArrayList<WSDLFaultImpl>();
        this.faultMap = new QNameMap<WSDLFaultImpl>();
        this.owner = owner;
    }

    public QName getName() {
        return name;
    }

    public String getParameterOrder() {
        return parameterOrder;
    }

    public void setParameterOrder(String parameterOrder) {
        this.parameterOrder = parameterOrder;
    }

    public WSDLInputImpl getInput() {
        return input;
    }

    public void setInput(WSDLInputImpl input) {
        this.input = input;
    }

    public WSDLOutputImpl getOutput() {
        return output;
    }

    public boolean isOneWay() {
        return output == null;
    }

    public void setOutput(WSDLOutputImpl output) {
        this.output = output;
    }

    public Iterable<WSDLFaultImpl> getFaults() {
        return faults;
    }

    public WSDLFault getFault(QName faultDetailName) {
        WSDLFaultImpl fault = faultMap.get(faultDetailName);
        if(fault != null)
            return fault;

        for(WSDLFaultImpl fi:faults){
            assert fi.getMessage().parts().iterator().hasNext();
            WSDLPartImpl part = fi.getMessage().parts().iterator().next();
            if(part.getDescriptor().name().equals(faultDetailName)){
                faultMap.put(faultDetailName, fi);
                return fi;
            }
        }
        return null;
    }

    WSDLPortType getOwner() {
        return owner;
    }

    @NotNull
    public QName getPortTypeName() {
        return owner.getName();
    }

    public void addFault(WSDLFaultImpl fault) {
        faults.add(fault);
    }

    public void freez(WSDLModelImpl root) {
        assert input != null;
        input.freeze(root);
        if(output != null)
            output.freeze(root);
        for(WSDLFaultImpl fault : faults){
            fault.freeze(root);
        }
    }
}
