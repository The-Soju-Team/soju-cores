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

import com.hh.xml.internal.ws.api.model.ParameterBinding;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;

import javax.xml.stream.XMLStreamReader;

/**
 * Implementation of {@link WSDLPart}
 *
 * @author Vivek Pandey
 */
public final class WSDLPartImpl extends AbstractObjectImpl implements WSDLPart {
    private final String name;
    private ParameterBinding binding;
    private int index;
    private final WSDLPartDescriptor descriptor;

    public WSDLPartImpl(XMLStreamReader xsr, String partName, int index, WSDLPartDescriptor descriptor) {
        super(xsr);
        this.name = partName;
        this.binding = ParameterBinding.UNBOUND;
        this.index = index;
        this.descriptor = descriptor;

    }

    public String getName() {
        return name;
    }

    public ParameterBinding getBinding() {
        return binding;
    }

    public void setBinding(ParameterBinding binding) {
        this.binding = binding;
    }

    public int getIndex() {
        return index;
    }

    //need to set the index in case of rpclit to reorder the body parts
    public void setIndex(int index){
        this.index = index;
    }

    boolean isBody(){
        return binding.isBody();
    }

    public WSDLPartDescriptor getDescriptor() {
        return descriptor;
    }
}
