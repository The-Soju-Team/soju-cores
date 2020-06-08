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

package com.hh.xml.internal.ws.handler;

import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.binding.BindingImpl;

import com.hh.webservice.ws.handler.LogicalHandler;
import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.handler.Handler;
import com.hh.webservice.ws.WebServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author WS Development Team
 */
public class ClientLogicalHandlerTube extends HandlerTube {

    private WSBinding binding;
    private SEIModel seiModel;

    /**
     * Creates a new instance of LogicalHandlerTube
     */
    public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
        super(next, port);
        this.binding = binding;
        this.seiModel = seiModel;
    }

    /**
     * This constructor is used on client-side where, SOAPHandlerTube is created
     * first and then a LogicalHandlerTube is created with a handler to that
     * SOAPHandlerTube.
     * With this handle, LogicalHandlerTube can call
     * SOAPHandlerTube.closeHandlers()
     */
    public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube);
        this.binding = binding;
        this.seiModel = seiModel;
    }

    /**
     * Copy constructor for {@link com.sun.xml.internal.ws.api.pipe.Tube#copy(com.sun.xml.internal.ws.api.pipe.TubeCloner)}.
     */

    private ClientLogicalHandlerTube(ClientLogicalHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.seiModel = that.seiModel;
    }

    //should be overridden by DriverHandlerTubes
    @Override
    protected void initiateClosing(MessageContext mc) {
      close(mc);
      super.initiateClosing(mc);
    }

    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ClientLogicalHandlerTube(this, cloner);
    }

    void setUpProcessor() {
        // Take a snapshot, User may change chain after invocation, Same chain
        // should be used for the entire MEP
        handlers = new ArrayList<Handler>();
        List<LogicalHandler> logicalSnapShot= ((BindingImpl) binding).getHandlerConfig().getLogicalHandlers();
        if (!logicalSnapShot.isEmpty()) {
            handlers.addAll(logicalSnapShot);
            if (binding.getSOAPVersion() == null) {
                processor = new XMLHandlerProcessor(this, binding,
                        handlers);
            } else {
                processor = new SOAPHandlerProcessor(true, this, binding,
                        handlers);
            }
        }
    }


    MessageUpdatableContext getContext(Packet packet) {
        return new LogicalMessageContextImpl(binding, (seiModel!= null?seiModel.getJAXBContext():null), packet);
    }

    boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {

        boolean handlerResult;
        try {

            //CLIENT-SIDE
            handlerResult = processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, context, !isOneWay);
        } catch (WebServiceException wse) {
            remedyActionTaken = true;
            //no rewrapping
            throw wse;
        } catch (RuntimeException re) {
            remedyActionTaken = true;

            throw new WebServiceException(re);

        }
        if (!handlerResult) {
            remedyActionTaken = true;
        }
        return handlerResult;
    }

    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        try {

            //CLIENT-SIDE
            processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, context, handleFault);

        } catch (WebServiceException wse) {
            //no rewrapping
            throw wse;
        } catch (RuntimeException re) {

            throw new WebServiceException(re);

        }
    }
    void closeHandlers(MessageContext mc) {
        closeClientsideHandlers(mc);

    }
}
