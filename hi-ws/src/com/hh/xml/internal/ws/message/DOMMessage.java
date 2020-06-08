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

package com.hh.xml.internal.ws.message;

import com.sun.istack.internal.FragmentContentHandler;
import com.hh.xml.internal.bind.api.Bridge;
import com.hh.xml.internal.bind.unmarshaller.DOMScanner;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.message.HeaderList;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.AttachmentSet;
import com.hh.xml.internal.ws.streaming.DOMStreamReader;
import com.hh.xml.internal.ws.util.DOMUtil;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import com.hh.webservice.ws.WebServiceException;

/**
 * {@link Message} backed by a DOM {@link Element} that represents the payload.
 *
 * @author Kohsuke Kawaguchi
 */
public final class DOMMessage extends AbstractMessageImpl {
    private HeaderList headers;
    private final Element payload;

    public DOMMessage(SOAPVersion ver, Element payload) {
        this(ver,null,payload);
    }

    public DOMMessage(SOAPVersion ver, HeaderList headers, Element payload) {
        this(ver,headers,payload,null);
    }

    public DOMMessage(SOAPVersion ver, HeaderList headers, Element payload, AttachmentSet attachments) {
        super(ver);
        this.headers = headers;
        this.payload = payload;
        this.attachmentSet = attachments;
        assert payload!=null;
    }
    /**
     * This constructor is a convenience and called by the {@link #copy}
     */
    private DOMMessage(DOMMessage that) {
        super(that);
        this.headers = HeaderList.copy(that.headers);
        this.payload = that.payload;
    }

    public boolean hasHeaders() {
        return getHeaders().size() > 0;
    }

    public HeaderList getHeaders() {
        if (headers == null)
            headers = new HeaderList();

        return headers;
    }

    public String getPayloadLocalPart() {
        return payload.getLocalName();
    }

    public String getPayloadNamespaceURI() {
        return payload.getNamespaceURI();
    }

    public boolean hasPayload() {
        return true;
    }

    public Source readPayloadAsSource() {
        return new DOMSource(payload);
    }

    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        if(hasAttachments())
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
        try {
            return (T)unmarshaller.unmarshal(payload);
        } finally{
            unmarshaller.setAttachmentUnmarshaller(null);
        }
    }

    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(payload,
            hasAttachments()? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    }

    public XMLStreamReader readPayload() throws XMLStreamException {
        DOMStreamReader dss = new DOMStreamReader();
        dss.setCurrentNode(payload);
        dss.nextTag();
        assert dss.getEventType()==XMLStreamReader.START_ELEMENT;
        return dss;
    }

    public void writePayloadTo(XMLStreamWriter sw) {
        try {
            if (payload != null)
                DOMUtil.serializeNode(payload, sw);
        } catch (XMLStreamException e) {
            throw new WebServiceException(e);
        }
    }

    protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        if(fragment)
            contentHandler = new FragmentContentHandler(contentHandler);
        DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(payload);
    }

    public Message copy() {
        return new DOMMessage(this);
    }
}
