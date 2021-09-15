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

package com.hh.xml.internal.ws.encoding.fastinfoset;

import com.hh.xml.internal.ws.api.pipe.ContentType;
import com.hh.xml.internal.ws.api.pipe.Codec;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.hh.xml.internal.ws.encoding.ContentTypeImpl;
import com.hh.xml.internal.ws.message.stream.StreamHeader;
import com.hh.xml.internal.ws.message.stream.StreamHeader11;
import com.hh.xml.internal.stream.buffer.XMLStreamBuffer;

import javax.xml.stream.XMLStreamReader;

/**
 * A codec that converts SOAP 1.1 messages infosets to fast infoset
 * documents.
 *
 * @author Paul.Sandoz@Sun.Com
 */
final class FastInfosetStreamSOAP11Codec extends FastInfosetStreamSOAPCodec {
    /*package*/ FastInfosetStreamSOAP11Codec(StreamSOAPCodec soapCodec, boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_11, retainState,
                (retainState) ? FastInfosetMIMETypes.STATEFUL_SOAP_11 : FastInfosetMIMETypes.SOAP_11);
    }

    private FastInfosetStreamSOAP11Codec(FastInfosetStreamSOAP11Codec that) {
        super(that);
    }

    public Codec copy() {
        return new FastInfosetStreamSOAP11Codec(this);
    }

    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader11(reader, mark);
    }

    protected ContentType getContentType(String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            return _defaultContentType;
        } else {
            return new ContentTypeImpl(_defaultContentType.getContentType(), soapAction);
        }
    }
}
