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
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.client.WSPortInfo;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.client.WSServiceDelegate;
import com.hh.xml.internal.ws.client.PortInfo;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.Dispatch;
import com.hh.webservice.ws.Service.Mode;

/**
 * {@link Dispatch} implementation for {@link Message}.
 *
 * @author Kohsuke Kawaguchi
 * @since 2.1.1
 */
public class MessageDispatch extends DispatchImpl<Message> {
    @Deprecated
    public MessageDispatch(QName port, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, Mode.MESSAGE, service, pipe, binding, epr);
    }

    public MessageDispatch(WSPortInfo portInfo, BindingImpl binding, WSEndpointReference epr) {
            super(portInfo, Mode.MESSAGE, binding, epr);
    }

    @Override
    Message toReturnValue(Packet response) {
        return response.getMessage();
    }

    @Override
    Packet createPacket(Message msg) {
        return new Packet(msg);
    }
}
