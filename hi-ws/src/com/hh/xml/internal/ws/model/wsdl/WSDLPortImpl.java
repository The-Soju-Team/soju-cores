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

import com.hh.xml.internal.ws.api.EndpointAddress;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.addressing.WSEndpointReference;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.resources.ClientMessages;
import com.hh.xml.internal.ws.util.exception.LocatableWebServiceException;
import com.hh.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import com.hh.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * Implementation of {@link WSDLPort}
 *
 * @author Vivek Pandey
 */
public final class WSDLPortImpl extends AbstractFeaturedObjectImpl implements WSDLPort {
    private final QName name;
    private EndpointAddress address;
    private final QName bindingName;
    private final WSDLServiceImpl owner;
    private WSEndpointReference epr;

    /**
     * To be set after the WSDL parsing is complete.
     */
    private WSDLBoundPortTypeImpl boundPortType;

    public WSDLPortImpl(XMLStreamReader xsr,WSDLServiceImpl owner, QName name, QName binding) {
        super(xsr);
        this.owner = owner;
        this.name = name;
        this.bindingName = binding;
    }

    public QName getName() {
        return name;
    }

    public QName getBindingName() {
        return bindingName;
    }

    public EndpointAddress getAddress() {
        return address;
    }

    public WSDLServiceImpl getOwner() {
        return owner;
    }

    /**
     * Only meant for {@link RuntimeWSDLParser} to call.
     */
    public void setAddress(EndpointAddress address) {
        assert address!=null;
        this.address = address;
    }

    /**
     * Only meant for {@link RuntimeWSDLParser} to call.
     */
    public void setEPR(@NotNull WSEndpointReference epr) {
        assert epr!=null;
        this.addExtension(epr);
        this.epr = epr;
    }

    public @Nullable WSEndpointReference getEPR() {
        return epr;
    }
    public WSDLBoundPortTypeImpl getBinding() {
        return boundPortType;
    }

    public SOAPVersion getSOAPVersion(){
        return boundPortType.getSOAPVersion();
    }

    void freeze(WSDLModelImpl root) {
        boundPortType = root.getBinding(bindingName);
        if(boundPortType==null) {
            throw new LocatableWebServiceException(
                ClientMessages.UNDEFINED_BINDING(bindingName), getLocation());
        }
        if(features == null)
            features =  new WebServiceFeatureList();
        features.setParentFeaturedObject(boundPortType);
        notUnderstoodExtensions.addAll(boundPortType.notUnderstoodExtensions);
    }
}
