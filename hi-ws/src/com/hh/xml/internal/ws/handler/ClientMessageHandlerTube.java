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

import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.handler.MessageHandler;
import com.hh.xml.internal.ws.api.message.Attachment;
import com.hh.xml.internal.ws.api.message.AttachmentSet;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
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
public class ClientMessageHandlerTube extends HandlerTube {
    private SEIModel seiModel;
    private WSBinding binding;
    private Set<String> roles;

    /**
     * Creates a new instance of MessageHandlerTube
     */
    public ClientMessageHandlerTube(@Nullable SEIModel seiModel, WSBinding binding, WSDLPort port, Tube next) {
        super(next, port);
        this.seiModel = seiModel;
        this.binding = binding;
    }

    /**
     * Copy constructor for {@link com.sun.xml.internal.ws.api.pipe.Tube#copy(com.sun.xml.internal.ws.api.pipe.TubeCloner)}.
     */
    private ClientMessageHandlerTube(ClientMessageHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
        this.binding = that.binding;
    }

    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ClientMessageHandlerTube(this, cloner);
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

    boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {
        boolean handlerResult;

        //Lets copy all the MessageContext.OUTBOUND_ATTACHMENT_PROPERTY to the message
        Map<String, DataHandler> atts = (Map<String, DataHandler>) context.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
        AttachmentSet attSet = packet.getMessage().getAttachments();
        for(String cid : atts.keySet()){
            if (attSet.get(cid) == null) {  // Otherwise we would be adding attachments twice
                Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
                attSet.add(att);
            }
        }

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

    void closeHandlers(MessageContext mc) {
        closeClientsideHandlers(mc);

    }

    void setUpProcessor() {
       // Take a snapshot, User may change chain after invocation, Same chain
        // should be used for the entire MEP
        handlers = new ArrayList<Handler>();
        HandlerConfiguration handlerConfig = ((BindingImpl) binding).getHandlerConfig();
        List<MessageHandler> msgHandlersSnapShot= handlerConfig.getMessageHandlers();
        if (!msgHandlersSnapShot.isEmpty()) {
            handlers.addAll(msgHandlersSnapShot);
            roles = new HashSet<String>();
            roles.addAll(handlerConfig.getRoles());
            processor = new SOAPHandlerProcessor(true, this, binding, handlers);
        }
    }



    MessageUpdatableContext getContext(Packet p) {
        MessageHandlerContextImpl context = new MessageHandlerContextImpl(seiModel, binding, port, packet,roles);
        return context;
    }


}
