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
import com.hh.xml.internal.ws.encoding.xml.XMLMessage;
import com.hh.xml.internal.ws.message.source.PayloadSourceMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import com.hh.webservice.ws.Dispatch;
import com.hh.webservice.ws.Service.Mode;
import java.io.IOException;

/**
 * {@link Dispatch} implementation for {@link Source} and XML/HTTP binding.
 *
 * @author Kathy Walsh
 * @author Kohsuke Kawaguchi
 * @see SOAPSourceDispatch
 */
final class RESTSourceDispatch extends DispatchImpl<Source> {
    @Deprecated
    public RESTSourceDispatch(QName port, Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert isXMLHttp(binding);
    }

    public RESTSourceDispatch(WSPortInfo portInfo, Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        assert isXMLHttp(binding);
    }

    @Override
    Source toReturnValue(Packet response) {
        Message msg = response.getMessage();
        try {
            return new StreamSource(XMLMessage.getDataSource(msg, binding).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Packet createPacket(Source msg) {
        Message message;

        if(msg==null)
            message = Messages.createEmpty(soapVersion);
        else
            message = new PayloadSourceMessage(null, msg, setOutboundAttachments(), soapVersion);

        return new Packet(message);
    }
}
