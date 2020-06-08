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

package com.hh.xml.internal.ws.client.dispatch;

import com.hh.xml.internal.ws.api.addressing.WSEndpointReference;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.client.WSPortInfo;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.client.WSServiceDelegate;
import com.hh.xml.internal.ws.client.PortInfo;
import com.hh.xml.internal.ws.message.saaj.SAAJMessage;
import com.hh.xml.internal.ws.resources.DispatchMessages;
import com.hh.xml.internal.ws.transport.Headers;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.MimeHeader;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPMessage;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.handler.MessageContext;

import java.util.Iterator;

/**
 * The <code>SOAPMessageDispatch</code> class provides support
 * for the dynamic invocation of a service endpoint operation using
 * the <code>SOAPMessage</code> class. The <code>javax.xml.ws.Service</code>
 * interface acts as a factory for the creation of <code>SOAPMessageDispatch</code>
 * instances.
 *
 * @author WS Development Team
 * @version 1.0
 */
public class SOAPMessageDispatch extends com.hh.xml.internal.ws.client.dispatch.DispatchImpl<SOAPMessage> {
    @Deprecated
    public SOAPMessageDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
    }

    public SOAPMessageDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
    }

    Packet createPacket(SOAPMessage arg) {
        Iterator iter = arg.getMimeHeaders().getAllHeaders();
        Headers ch = new Headers();
        while(iter.hasNext()) {
            MimeHeader mh = (MimeHeader) iter.next();
            ch.add(mh.getName(), mh.getValue());
        }
        Packet packet = new Packet(new SAAJMessage(arg));
        packet.invocationProperties.put(MessageContext.HTTP_REQUEST_HEADERS, ch);
        return packet;
    }

    SOAPMessage toReturnValue(Packet response) {
        try {

            //not sure if this is the correct way to deal with this.
            if ( response ==null || response.getMessage() == null )
                     throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
            else
                return response.getMessage().readAsSOAPMessage();
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        }
    }
}
