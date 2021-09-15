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

package com.hh.xml.internal.ws.wsdl;

import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.xml.namespace.QName;

/**
 * Extensions if this class will be used for dispatching the request message to the correct endpoint method by
 * identifying the wsdl operation associated with the request.
 *
 * @See OperationDispatcher
 *
 * @author Rama Pulavarthi
 */
public abstract class WSDLOperationFinder {
    protected final WSDLPort wsdlModel;
    protected final WSBinding binding;
    protected final SEIModel seiModel;

    public WSDLOperationFinder(@NotNull WSDLPort wsdlModel, @NotNull WSBinding binding, @Nullable SEIModel seiModel) {
        this.wsdlModel = wsdlModel;
        this.binding = binding;
        this.seiModel= seiModel;
    }

    /**
     * This methods returns the QName of the WSDL operation correponding to a request Packet.
     * An implementation should return null when it cannot dispatch to a unique method based on the information it processes.
     * In such case, other OperationFinders are queried to resolve a WSDL operation.
     * It should throw an instance of DispatchException if it finds incorrect information in the packet.
     *
     * @param request  Request Packet that is used to find the associated WSDLOperation
     * @return QName of the WSDL Operation that this request correponds to.
     *          null when it cannot find a unique operation to dispatch to.
     * @throws DispatchException When the information in the Packet is invalid
     */
    public abstract QName getWSDLOperationQName(Packet request) throws DispatchException;
}
