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
 * SOAPHandlerProcessor.java
 *
 * Created on February 8, 2006, 5:43 PM
 *
 *
 */

package com.hh.xml.internal.ws.handler;

import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Messages;
import com.hh.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.hh.xml.internal.ws.encoding.soap.SOAPConstants;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import com.hh.webservice.ws.ProtocolException;
import com.hh.webservice.ws.handler.Handler;

/**
 *
 * @author WS Development Team
 */
final class SOAPHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {

    /**
     * Creates a new instance of SOAPHandlerProcessor
     */
    public SOAPHandlerProcessor(boolean isClient, HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
        super(owner, binding, chain);
        this.isClient = isClient;
    }

    /**
     * Replace the message in the given message context with a
     * fault message. If the context already contains a fault
     * message, then return without changing it.
     *
     * <p>This method should only be called during a request,
     * because during a response an exception from a handler
     * is dispatched rather than replacing the message with
     * a fault. So this method can use the MESSAGE_OUTBOUND_PROPERTY
     * to determine whether it is being called on the client
     * or the server side. If this changes in the spec, then
     * something else will need to be passed to the method
     * to determine whether the fault code is client or server.
     */
    final void insertFaultMessage(C context,
        ProtocolException exception) {
        try {
            if(!context.getPacketMessage().isFault()) {
                Message faultMessage = Messages.create(binding.getSOAPVersion(),
                        exception,determineFaultCode(binding.getSOAPVersion()));
                context.setPacketMessage(faultMessage);
            }
        } catch (Exception e) {
            // severe since this is from runtime and not handler
            logger.log(Level.SEVERE,
                "exception while creating fault message in handler chain", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Figure out if the fault code local part is client,
     * server, sender, receiver, etc. This is called by
     * insertFaultMessage.
     */
    private QName determineFaultCode(SOAPVersion soapVersion) {
        return isClient ? soapVersion.faultCodeClient : soapVersion.faultCodeServer;
    }

}
