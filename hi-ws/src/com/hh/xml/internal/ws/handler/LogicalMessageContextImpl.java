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

import com.hh.xml.internal.ws.api.message.AttachmentSet;
import com.hh.xml.internal.ws.api.message.HeaderList;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.message.EmptyMessageImpl;
import com.hh.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.transform.Source;

import com.hh.webservice.ws.LogicalMessage;
import com.hh.webservice.ws.handler.LogicalMessageContext;
import javax.xml.bind.JAXBContext;

/**
 * Implementation of LogicalMessageContext. This class is used at runtime
 * to pass to the handlers for processing logical messages.
 *
 * <p>This Class delegates most of the fuctionality to Packet
 *
 * @see Packet
 *
 * @author WS Development Team
 */
class LogicalMessageContextImpl extends MessageUpdatableContext implements LogicalMessageContext {
    private LogicalMessageImpl lm;
    private WSBinding binding;
    private JAXBContext defaultJaxbContext;
    public LogicalMessageContextImpl(WSBinding binding, JAXBContext defaultJAXBContext, Packet packet) {
        super(packet);
        this.binding = binding;
        this.defaultJaxbContext = defaultJAXBContext;
    }

    public LogicalMessage getMessage() {
        if(lm == null)
            lm = new LogicalMessageImpl(defaultJaxbContext, packet);
        return lm;
    }

    void setPacketMessage(Message newMessage){
        if(newMessage != null) {
            packet.setMessage(newMessage);
            lm = null;
        }
    }


    protected void updateMessage() {
        //If LogicalMessage is not acccessed, its not modified.
        if(lm != null) {
            //Check if LogicalMessageImpl has changed, if so construct new one
            // Packet are handled through MessageContext
            if(lm.isPayloadModifed()){
                Message msg = packet.getMessage();
                Message updatedMsg = lm.getMessage(msg.getHeaders(),msg.getAttachments(),binding);
                packet.setMessage(updatedMsg);
            }
            lm = null;
        }

    }

}
