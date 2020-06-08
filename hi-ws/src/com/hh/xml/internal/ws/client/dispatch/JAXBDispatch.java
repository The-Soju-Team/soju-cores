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

import com.hh.xml.internal.bind.api.JAXBRIContext;
import com.hh.xml.internal.ws.api.addressing.WSEndpointReference;
import com.hh.xml.internal.ws.api.message.Header;
import com.hh.xml.internal.ws.api.message.Headers;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Messages;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.client.WSPortInfo;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.client.WSServiceDelegate;
import com.hh.xml.internal.ws.client.PortInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.WebServiceException;

/**
 * The <code>JAXBDispatch</code> class provides support
 * for the dynamic invocation of a service endpoint operation using
 * JAXB objects. The <code>javax.xml.ws.Service</code>
 * interface acts as a factory for the creation of <code>JAXBDispatch</code>
 * instances.
 *
 * @author WS Development Team
 * @version 1.0
 */
public class JAXBDispatch extends DispatchImpl<Object> {

    private final JAXBContext jaxbcontext;

    @Deprecated
    public JAXBDispatch(QName port, JAXBContext jc, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, service, pipe, binding, epr);
        this.jaxbcontext = jc;
    }

    public JAXBDispatch(WSPortInfo portInfo, JAXBContext jc, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        this.jaxbcontext = jc;
    }

    Object toReturnValue(Packet response) {
        try {
            Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
            Message msg = response.getMessage();
            switch (mode) {
                case PAYLOAD:
                    return msg.<Object>readPayloadAsJAXB(unmarshaller);
                case MESSAGE:
                    Source result = msg.readEnvelopeAsSource();
                    return unmarshaller.unmarshal(result);
                default:
                    throw new WebServiceException("Unrecognized dispatch mode");
            }
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }


    Packet createPacket(Object msg) {
        assert jaxbcontext != null;

        try {
            Marshaller marshaller = jaxbcontext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);

            Message message = (msg == null) ? Messages.createEmpty(soapVersion): Messages.create(marshaller, msg, soapVersion);
            return new Packet(message);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }

    public void setOutboundHeaders(Object... headers) {
        if(headers==null)
            throw new IllegalArgumentException();
        Header[] hl = new Header[headers.length];
        for( int i=0; i<hl.length; i++ ) {
            if(headers[i]==null)
                throw new IllegalArgumentException();
            // TODO: handle any JAXBContext.
            hl[i] = Headers.create((JAXBRIContext)jaxbcontext,headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}
