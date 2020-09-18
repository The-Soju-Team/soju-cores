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

package com.hh.xml.internal.ws.message.source;

import com.hh.xml.internal.bind.api.Bridge;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.pipe.Codecs;
import com.hh.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.hh.xml.internal.ws.api.message.HeaderList;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.streaming.SourceReaderFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPMessage;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

/**
 * Implementation of {@link Message} backed by {@link Source} where the Source
 * represents the complete message such as a SOAP envelope. It uses
 * {@link StreamSOAPCodec} to create a {@link Message} and uses it as a
 * delegate for all the methods.
 *
 * @author Vivek Pandey
 * @author Jitendra Kotamraju
 */
public class ProtocolSourceMessage extends Message {
    private final Message sm;

    public ProtocolSourceMessage(Source source, SOAPVersion soapVersion) {
        XMLStreamReader reader = SourceReaderFactory.createSourceReader(source, true);
        com.hh.xml.internal.ws.api.pipe.StreamSOAPCodec codec = Codecs.createSOAPEnvelopeXmlCodec(soapVersion);
        sm = codec.decode(reader);
    }

    public boolean hasHeaders() {
        return sm.hasHeaders();
    }

    public HeaderList getHeaders() {
        return sm.getHeaders();
    }

    public String getPayloadLocalPart() {
        return sm.getPayloadLocalPart();
    }

    public String getPayloadNamespaceURI() {
        return sm.getPayloadNamespaceURI();
    }

    public boolean hasPayload() {
        return sm.hasPayload();
    }

    public Source readPayloadAsSource() {
        return sm.readPayloadAsSource();
    }

    public XMLStreamReader readPayload() throws XMLStreamException {
        return sm.readPayload();
    }

    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        sm.writePayloadTo(sw);
    }

    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        sm.writeTo(sw);
    }

    public Message copy() {
        return sm.copy();
    }

    public Source readEnvelopeAsSource() {
        return sm.readEnvelopeAsSource();
    }

    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        return sm.readAsSOAPMessage();
    }

    public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
        return sm.readAsSOAPMessage(packet, inbound);
    }

    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        return (T)sm.readPayloadAsJAXB(unmarshaller);
    }

    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return sm.readPayloadAsJAXB(bridge);
    }

    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        sm.writeTo(contentHandler, errorHandler);
    }
}
