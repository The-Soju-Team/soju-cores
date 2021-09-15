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
import com.hh.xml.internal.ws.api.handler.MessageHandler;
import com.hh.xml.internal.ws.api.message.Attachment;
import com.hh.xml.internal.ws.api.message.AttachmentSet;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.client.HandlerConfiguration;
import com.hh.xml.internal.ws.message.DataHandlerAttachment;

import javax.activation.DataHandler;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.handler.Handler;
import java.util.*;

/**
 * @author Rama Pulavarthi
 */
public class ServerMessageHandlerTube extends HandlerTube{
    private SEIModel seiModel;
    private WSBinding binding;
    private Set<String> roles;

    public ServerMessageHandlerTube(SEIModel seiModel, WSBinding binding, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube);
        this.seiModel = seiModel;
        this.binding = binding;
        setUpHandlersOnce();
    }

    /**
     * Copy constructor for {@link com.sun.xml.internal.ws.api.pipe.Tube#copy(com.sun.xml.internal.ws.api.pipe.TubeCloner)}.
     */
    private ServerMessageHandlerTube(ServerMessageHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
        this.binding = that.binding;
        this.handlers = that.handlers;
        this.roles = that.roles;
    }

    private void setUpHandlersOnce() {
        handlers = new ArrayList<Handler>();
        HandlerConfiguration handlerConfig = ((BindingImpl) binding).getHandlerConfig();
        List<MessageHandler> msgHandlersSnapShot= handlerConfig.getMessageHandlers();
        if (!msgHandlersSnapShot.isEmpty()) {
            handlers.addAll(msgHandlersSnapShot);
            roles = new HashSet<String>();
            roles.addAll(handlerConfig.getRoles());
        }
    }

    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        //Lets copy all the MessageContext.OUTBOUND_ATTACHMENT_PROPERTY to the message
        Map<String, DataHandler> atts = (Map<String, DataHandler>) context.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
        AttachmentSet attSet = packet.getMessage().getAttachments();
        for(String cid : atts.keySet()){
            if (attSet.get(cid) == null) { // Otherwise we would be adding attachments twice
                Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
                attSet.add(att);
            }
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

    void setUpProcessor() {
        if(!handlers.isEmpty()) {
            processor = new SOAPHandlerProcessor(false, this, binding, handlers);
        }
    }

    void closeHandlers(MessageContext mc) {
        closeServersideHandlers(mc);

    }
    MessageUpdatableContext getContext(Packet packet) {
       MessageHandlerContextImpl context = new MessageHandlerContextImpl(seiModel, binding, port, packet, roles);
       return context;
    }

    //should be overridden by DriverHandlerTubes
    @Override
    protected void initiateClosing(MessageContext mc) {
      close(mc);
      super.initiateClosing(mc);
    }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ServerMessageHandlerTube(this, cloner);
    }
}
