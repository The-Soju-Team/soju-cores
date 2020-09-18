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
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Messages;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.client.WSPortInfo;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.client.WSServiceDelegate;
import com.hh.xml.internal.ws.client.PortInfo;
import com.hh.xml.internal.ws.message.source.PayloadSourceMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import com.hh.webservice.ws.Service.Mode;
import com.hh.webservice.ws.WebServiceException;


/**
 * The <code>SOAPSourceDispatch</code> class provides support
 * for the dynamic invocation of a service endpoint operation using XML
 * constructs. The <code>javax.xml.ws.Service</code>
 * interface acts as a factory for the creation of <code>SOAPSourceDispatch</code>
 * instances.
 *
 * @author WS Development Team
 * @version 1.0
 * @see RESTSourceDispatch
 */
final class SOAPSourceDispatch extends DispatchImpl<Source> {
    @Deprecated
    public SOAPSourceDispatch(QName port, Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert !isXMLHttp(binding);
    }

    public SOAPSourceDispatch(WSPortInfo portInfo, Mode mode, BindingImpl binding, WSEndpointReference epr) {
            super(portInfo, mode, binding, epr);
            assert !isXMLHttp(binding);
    }


    Source toReturnValue(Packet response) {
        Message msg = response.getMessage();

        switch (mode) {
        case PAYLOAD:
            return msg.readPayloadAsSource();
        case MESSAGE:
            return msg.readEnvelopeAsSource();
        default:
            throw new WebServiceException("Unrecognized dispatch mode");
        }
    }

    @Override
    Packet createPacket(Source msg) {

        final Message message;

        if (msg == null)
            message = Messages.createEmpty(soapVersion);
        else {
            switch (mode) {
            case PAYLOAD:
                message = new PayloadSourceMessage(null, msg, setOutboundAttachments(), soapVersion);
                break;
            case MESSAGE:
                message = Messages.create(msg, soapVersion);
                break;
            default:
                throw new WebServiceException("Unrecognized message mode");
            }
        }

        return new Packet(message);
    }


}
