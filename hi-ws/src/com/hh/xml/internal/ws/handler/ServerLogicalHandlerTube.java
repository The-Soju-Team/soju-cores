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
import com.hh.xml.internal.ws.api.message.AttachmentSet;
import com.hh.xml.internal.ws.api.message.Attachment;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.message.DataHandlerAttachment;

import com.hh.webservice.ws.handler.LogicalHandler;
import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.handler.Handler;
import com.hh.webservice.ws.WebServiceException;
import javax.activation.DataHandler;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author WS Development Team
 */
public class ServerLogicalHandlerTube extends HandlerTube {

    private WSBinding binding;
    private SEIModel seiModel;
    /**
     * Creates a new instance of LogicalHandlerTube
     */
    public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
        super(next, port);
        this.binding = binding;
        this.seiModel = seiModel;
        setUpHandlersOnce();
    }

    /**
     * This constructor is used on client-side where, SOAPHandlerTube is created
     * first and then a LogicalHandlerTube is created with a handler to that
     * SOAPHandlerTube.
     * With this handle, LogicalHandlerTube can call
     * SOAPHandlerTube.closeHandlers()
     */
    public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube);
        this.binding = binding;
        this.seiModel = seiModel;
        setUpHandlersOnce();
    }

    /**
     * Copy constructor for {@link com.sun.xml.internal.ws.api.pipe.Tube#copy(com.sun.xml.internal.ws.api.pipe.TubeCloner)}.
     */

    private ServerLogicalHandlerTube(ServerLogicalHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.seiModel = that.seiModel;
        this.handlers = that.handlers;
    }

    //should be overridden by DriverHandlerTubes
    @Override
    protected void initiateClosing(MessageContext mc) {
         if (binding.getSOAPVersion() != null) {
            super.initiateClosing(mc);
        } else {
            close(mc);
            super.initiateClosing(mc);
        }
    }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ServerLogicalHandlerTube(this, cloner);
    }

    private void setUpHandlersOnce() {
        handlers = new ArrayList<Handler>();
        List<LogicalHandler> logicalSnapShot= ((BindingImpl) binding).getHandlerConfig().getLogicalHandlers();
        if (!logicalSnapShot.isEmpty()) {
            handlers.addAll(logicalSnapShot);
        }
    }

    void setUpProcessor() {
        if (!handlers.isEmpty()) {
            if (binding.getSOAPVersion() == null) {
                processor = new XMLHandlerProcessor(this, binding,
                        handlers);
            } else {
                processor = new SOAPHandlerProcessor(false, this, binding, handlers);
            }
        }
    }

    MessageUpdatableContext getContext(Packet packet) {
        return new LogicalMessageContextImpl(binding, (seiModel!= null?seiModel.getJAXBContext():null), packet);
    }

    boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {

        boolean handlerResult;
        try {
            //SERVER-SIDE
            handlerResult = processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, context, !isOneWay);

        } catch (RuntimeException re) {
            remedyActionTaken = true;
            throw re;
        }
        if (!handlerResult) {
            remedyActionTaken = true;
        }
        return handlerResult;
    }

    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        //Lets copy all the MessageContext.OUTBOUND_ATTACHMENT_PROPERTY to the message
        Map<String, DataHandler> atts = (Map<String, DataHandler>) context.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
        AttachmentSet attSet = packet.getMessage().getAttachments();
        for(String cid : atts.keySet()){
            Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
            attSet.add(att);
        }

        try {
            //SERVER-SIDE
            processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, context, handleFault);

        } catch (WebServiceException wse) {
            //no rewrapping
            throw wse;
        } catch (RuntimeException re) {
            throw re;
        }
    }

    void closeHandlers(MessageContext mc) {
        closeServersideHandlers(mc);

    }
}
