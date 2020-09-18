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

package com.hh.xml.internal.ws.server.provider;

import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Messages;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.encoding.xml.XMLMessage;
import com.hh.xml.internal.ws.resources.ServerMessages;

import javax.activation.DataSource;
import javax.xml.transform.Source;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.http.HTTPException;

/**
 * @author Jitendra Kotamraju
 */
abstract class XMLProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T> {

    @Override
    protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
        Packet response = super.getResponse(request, e, port, binding);
        if (e instanceof HTTPException) {
            if (response.supports(MessageContext.HTTP_RESPONSE_CODE)) {
                response.put(MessageContext.HTTP_RESPONSE_CODE, ((HTTPException)e).getStatusCode());
            }
        }
        return response;
    }

    static XMLProviderArgumentBuilder createBuilder(ProviderEndpointModel model, WSBinding binding) {
        if (model.mode == Service.Mode.PAYLOAD) {
            return new PayloadSource();
        } else {
            if(model.datatype==Source.class)
                return new PayloadSource();
            if(model.datatype== DataSource.class)
                return new DataSourceParameter(binding);
            throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass,model.datatype));
        }
    }

    private static final class PayloadSource extends XMLProviderArgumentBuilder<Source> {
        public Source getParameter(Packet packet) {
            return packet.getMessage().readPayloadAsSource();
        }

        public Message getResponseMessage(Source source) {
            return Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
        }

        protected Message getResponseMessage(Exception e) {
            return XMLMessage.create(e);
        }
    }

    private static final class DataSourceParameter extends XMLProviderArgumentBuilder<DataSource> {
        private final WSBinding binding;

        DataSourceParameter(WSBinding binding) {
            this.binding = binding;
        }
        public DataSource getParameter(Packet packet) {
            Message msg = packet.getMessage();
            return (msg instanceof XMLMessage.MessageDataSource)
                    ? ((XMLMessage.MessageDataSource) msg).getDataSource()
                    : XMLMessage.getDataSource(msg, binding);
        }

        public Message getResponseMessage(DataSource ds) {
            return XMLMessage.create(ds, binding);
        }

        protected Message getResponseMessage(Exception e) {
            return XMLMessage.create(e);
        }
    }

}
