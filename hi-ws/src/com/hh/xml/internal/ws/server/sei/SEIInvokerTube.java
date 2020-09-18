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

package com.hh.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.NextAction;
import com.hh.xml.internal.ws.api.server.Invoker;
import com.hh.xml.internal.ws.client.sei.MethodHandler;
import com.hh.xml.internal.ws.model.AbstractSEIModelImpl;
import com.hh.xml.internal.ws.model.JavaMethodImpl;
import com.hh.xml.internal.ws.server.InvokerTube;
import com.hh.xml.internal.ws.server.WSEndpointImpl;
import com.hh.xml.internal.ws.resources.ServerMessages;
import com.hh.xml.internal.ws.fault.SOAPFaultBuilder;
import com.hh.xml.internal.ws.wsdl.DispatchException;
import com.hh.xml.internal.ws.util.QNameMap;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.text.MessageFormat;

/**
 * This pipe is used to invoke SEI based endpoints.
 *
 * @author Jitendra Kotamraju
 */
public class SEIInvokerTube extends InvokerTube {

    /**
     * For each method on the port interface we have
     * a {@link MethodHandler} that processes it.
     */
    private final WSBinding binding;
    private final AbstractSEIModelImpl model;

    //store WSDL Operation to EndpointMethodHandler map
    private final QNameMap<EndpointMethodHandler> wsdlOpMap;
    public SEIInvokerTube(AbstractSEIModelImpl model,Invoker invoker, WSBinding binding) {
        super(invoker);
        this.binding = binding;
        this.model = model;
        wsdlOpMap = new QNameMap<EndpointMethodHandler>();
        for(JavaMethodImpl jm: model.getJavaMethods()) {
            wsdlOpMap.put(jm.getOperation().getName(),new EndpointMethodHandler(this,jm,binding));
        }
    }

    /**
     * This binds the parameters for SEI endpoints and invokes the endpoint method. The
     * return value, and response Holder arguments are used to create a new {@link Message}
     * that traverses through the Pipeline to transport.
     */
    public @NotNull NextAction processRequest(@NotNull Packet req) {
        QName wsdlOp;
        try {
            wsdlOp = ((WSEndpointImpl) getEndpoint()).getOperationDispatcher().getWSDLOperationQName(req);
            Packet res = wsdlOpMap.get(wsdlOp).invoke(req);
            assert res != null;
            return doReturnWith(res);
        } catch (DispatchException e) {
            return doReturnWith(req.createServerResponse(e.fault, model.getPort(), null, binding));
        }
    }

    public @NotNull NextAction processResponse(@NotNull Packet response) {
        throw new IllegalStateException("InovkerPipe's processResponse shouldn't be called.");
    }

    public @NotNull NextAction processException(@NotNull Throwable t) {
        throw new IllegalStateException("InovkerPipe's processException shouldn't be called.");
    }

}
