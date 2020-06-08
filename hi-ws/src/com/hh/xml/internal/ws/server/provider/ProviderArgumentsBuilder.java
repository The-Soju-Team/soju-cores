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

package com.hh.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;

import com.hh.webservice.ws.soap.SOAPBinding;

/**
 * @author Jitendra Kotamraju
 */

abstract class ProviderArgumentsBuilder<T> {

    /**
     * Creates a fault {@link Message} from method invocation's exception
     */
    protected abstract Message getResponseMessage(Exception e);

    /**
     * Creates {@link Message} from method invocation's return value
     */
    protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
        Message message = getResponseMessage(e);
        Packet response = request.createServerResponse(message,port,null,binding);
        return response;
    }

    /**
     * Binds {@link com.hh.xml.internal.ws.api.message.Message} to method invocation parameter
     * @param packet
     */
    protected abstract T getParameter(Packet packet);

    protected abstract Message getResponseMessage(T returnValue);

    /**
     * Creates {@link Packet} from method invocation's return value
     */
    protected Packet getResponse(Packet request, @Nullable T returnValue, WSDLPort port, WSBinding binding) {
        Message message = null;
        if (returnValue != null) {
            message = getResponseMessage(returnValue);
        }
        Packet response = request.createServerResponse(message,port,null,binding);
        return response;
    }

    public static ProviderArgumentsBuilder<?> create(ProviderEndpointModel model, WSBinding binding) {
        return (binding instanceof SOAPBinding) ? SOAPProviderArgumentBuilder.create(model, binding.getSOAPVersion())
                : XMLProviderArgumentBuilder.createBuilder(model, binding);
    }

}
