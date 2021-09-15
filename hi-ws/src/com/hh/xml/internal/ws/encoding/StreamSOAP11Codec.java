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

package com.hh.xml.internal.ws.encoding;

import com.hh.xml.internal.stream.buffer.XMLStreamBuffer;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.pipe.ContentType;
import com.hh.xml.internal.ws.message.stream.StreamHeader;
import com.hh.xml.internal.ws.message.stream.StreamHeader11;

import javax.xml.stream.XMLStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * {@link StreamSOAPCodec} for SOAP 1.1.
 *
 * @author Paul.Sandoz@Sun.Com
 */
final class StreamSOAP11Codec extends StreamSOAPCodec {
    public static final String SOAP11_MIME_TYPE = "text/xml";
    public static final String SOAP11_CONTENT_TYPE = SOAP11_MIME_TYPE+"; charset=utf-8";

    private static final List<String> expectedContentTypes = Collections.singletonList(SOAP11_MIME_TYPE);

    /*package*/  StreamSOAP11Codec() {
        super(SOAPVersion.SOAP_11);
    }

    public String getMimeType() {
        return SOAP11_MIME_TYPE;
    }

    @Override
    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader11(reader, mark);
    }

    public static final ContentTypeImpl defaultContentType =
            new ContentTypeImpl(SOAP11_CONTENT_TYPE, "");

    @Override
    protected ContentType getContentType(String soapAction) {
        return new ContentTypeImpl(SOAP11_CONTENT_TYPE, soapAction);
    }

    protected List<String> getExpectedContentTypes() {
        return expectedContentTypes;
    }
}
