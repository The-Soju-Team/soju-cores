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

package com.hh.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.message.AbstractHeaderImpl;
import com.hh.xml.internal.ws.util.xml.XmlUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPMessage;
import com.hh.webservice.soap.SOAPHeader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;

/**
 * Used to represent outbound endpoint reference header,
 * such as &lt;ReplyTo> and &lt;FaultTo>.
 * Used from {@link WSEndpointReference}.
 *
 * @author Kohsuke Kawaguchi
 * @see WSEndpointReference
 */
final class EPRHeader extends AbstractHeaderImpl {

    private final String nsUri,localName;
    private final WSEndpointReference epr;

    EPRHeader(QName tagName, WSEndpointReference epr) {
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
        this.epr = epr;
    }

    public @NotNull String getNamespaceURI() {
        return nsUri;
    }

    public @NotNull String getLocalPart() {
        return localName;
    }

    @Nullable
    public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
        try {
            XMLStreamReader sr = epr.read("EndpointReference"/*doesn't matter*/);
            while(sr.getEventType()!= XMLStreamConstants.START_ELEMENT)
                sr.next();

            return sr.getAttributeValue(nsUri,localName);
        } catch (XMLStreamException e) {
            // since we are reading from buffer, this can't happen.
            throw new AssertionError(e);
        }
    }

    public XMLStreamReader readHeader() throws XMLStreamException {
        return epr.read(localName);
    }

    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        epr.writeTo(localName, w);
    }

    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            // TODO what about in-scope namespaces
            // Not very efficient consider implementing a stream buffer
            // processor that produces a DOM node from the buffer.
            Transformer t = XmlUtil.newTransformer();
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null)
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            t.transform(epr.asSource(localName), new DOMResult(header));
        } catch (Exception e) {
            throw new SOAPException(e);
        }
    }

    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        epr.writeTo(localName,contentHandler,errorHandler,true);
    }
}
