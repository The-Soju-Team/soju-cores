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

package com.hh.xml.internal.ws.encoding.xml;

import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Codec;
import com.hh.xml.internal.ws.api.pipe.ContentType;
import com.hh.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.encoding.ContentTypeImpl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.hh.webservice.ws.WebServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class XMLCodec implements Codec {
    public static final String XML_APPLICATION_MIME_TYPE = "application/xml";

    public static final String XML_TEXT_MIME_TYPE = "text/xml";

    private static final ContentType contentType = new ContentTypeImpl(XML_TEXT_MIME_TYPE);

    private final WSBinding binding;

    public XMLCodec(WSBinding binding) {
        this.binding = binding;
    }

    public String getMimeType() {
        return XML_APPLICATION_MIME_TYPE;
    }

    public ContentType getStaticContentType(Packet packet) {
        return contentType;
    }

    public ContentType encode(Packet packet, OutputStream out) {
        XMLStreamWriter writer = XMLStreamWriterFactory.create(out);
        try {
            if (packet.getMessage().hasPayload()){
                packet.getMessage().writePayloadTo(writer);
                writer.flush();
            }
        } catch (XMLStreamException e) {
            throw new WebServiceException(e);
        }
        return contentType;
    }

    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        //TODO: not yet implemented
        throw new UnsupportedOperationException();
    }

    public Codec copy() {
        return this;
    }

    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        Message message = XMLMessage.create(contentType, in, binding);
        packet.setMessage(message);
    }

    public void decode(ReadableByteChannel in, String contentType, Packet packet) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
