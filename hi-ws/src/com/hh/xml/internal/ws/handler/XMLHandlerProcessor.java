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

/*
 * LogicalHandlerProcessor.java
 *
 * Created on February 8, 2006, 5:40 PM
 *
 */

package com.hh.xml.internal.ws.handler;

import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Messages;
import java.util.List;
import com.hh.webservice.ws.ProtocolException;
import com.hh.webservice.ws.handler.Handler;
import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.http.HTTPException;

/**
 * This is used only for XML/HTTP binding
 * @author WS Development Team
 */
final class XMLHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {

    /**
     * Creates a new instance of LogicalHandlerProcessor
     */
    public XMLHandlerProcessor(HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
        super(owner, binding, chain);
    }

    /*
     * TODO: This is valid only for XML/HTTP binding
     * Empty the XML message
     */
    final void insertFaultMessage(C context,
            ProtocolException exception) {
        if(exception instanceof HTTPException) {
            context.put(MessageContext.HTTP_RESPONSE_CODE,((HTTPException)exception).getStatusCode());
        }
        if (context != null) {
            // non-soap case
            context.setPacketMessage(Messages.createEmpty(binding.getSOAPVersion()));
        }
    }
}
