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
import com.hh.xml.internal.ws.encoding.xml.XMLMessage;
import com.hh.xml.internal.ws.encoding.xml.XMLMessage.MessageDataSource;
import com.hh.xml.internal.ws.message.source.PayloadSourceMessage;

import javax.activation.DataSource;
import javax.xml.namespace.QName;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.WebServiceException;

/**
 *
 * @author WS Development Team
 * @version 1.0
 */
public class DataSourceDispatch extends DispatchImpl<DataSource> {
    @Deprecated
    public DataSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
       super(port, mode, service, pipe, binding, epr );
    }

    public DataSourceDispatch(WSPortInfo portInfo, Service.Mode mode,BindingImpl binding, WSEndpointReference epr) {
       super(portInfo, mode, binding, epr );
    }

    Packet createPacket(DataSource arg) {

         switch (mode) {
            case PAYLOAD:
                throw new IllegalArgumentException("DataSource use is not allowed in Service.Mode.PAYLOAD\n");
            case MESSAGE:
                return new Packet(XMLMessage.create(arg, binding));
            default:
                throw new WebServiceException("Unrecognized message mode");
        }
    }

    DataSource toReturnValue(Packet response) {
        Message message = response.getMessage();
        return (message instanceof MessageDataSource)
                ? ((MessageDataSource)message).getDataSource()
                : XMLMessage.getDataSource(message, binding);
    }
}
