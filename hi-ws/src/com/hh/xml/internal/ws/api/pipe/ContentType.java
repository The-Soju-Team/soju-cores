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

package com.hh.xml.internal.ws.api.pipe;

/**
 * A Content-Type transport header that will be returned by {@link Codec#encode(com.sun.xml.internal.ws.api.message.Packet, java.io.OutputStream)}.
 * It will provide the Content-Type header and also take care of SOAP 1.1 SOAPAction header.
 *
 * TODO: rename to ContentMetadata?
 *
 * @author Vivek Pandey
 */
public interface ContentType {
    /**
     * Gives non-null Content-Type header value.
     */
    public String getContentType();

    /**
     * Gives SOAPAction transport header value. It will be non-null only for SOAP 1.1 messages. In other cases
     * it MUST be null. The SOAPAction transport header should be written out only when its non-null.
     *
     * @return It can be null, in that case SOAPAction header should be written.
     */
    public String getSOAPActionHeader();

    /**
     * Controls the Accept transport header, if the transport supports it.
     * Returning null means the transport need not add any new header.
     *
     * <p>
     * We realize that this is not an elegant abstraction, but
     * this would do for now. If another person comes and asks for
     * a similar functionality, we'll define a real abstraction.
     */
    public String getAcceptHeader();
}
