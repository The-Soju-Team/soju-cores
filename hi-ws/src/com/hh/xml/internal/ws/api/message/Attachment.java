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

package com.hh.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;

import javax.activation.DataHandler;
import com.hh.webservice.soap.SOAPMessage;
import com.hh.webservice.soap.SOAPException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Attachment.
 */
public interface Attachment {

    /**
     * Content ID of the attachment. Uniquely identifies an attachment.
     *
     * @return
     *      The content ID like "foo-bar-zot@abc.com", without
     *      surrounding '&lt;' and '>' used as the transfer syntax.
     */
    @NotNull String getContentId();

    /**
     * Gets the MIME content-type of this attachment.
     */
    String getContentType();

    /**
     * Gets the attachment as an exact-length byte array.
     */
    byte[] asByteArray();

    /**
     * Gets the attachment as a {@link DataHandler}.
     */
    DataHandler asDataHandler();

    /**
     * Gets the attachment as a {@link Source}.
     * Note that there's no guarantee that the attachment is actually an XML.
     */
    Source asSource();

    /**
     * Obtains this attachment as an {@link InputStream}.
     */
    InputStream asInputStream();

    /**
     * Writes the contents of the attachment into the given stream.
     */
    void writeTo(OutputStream os) throws IOException;

    /**
     * Writes this attachment to the given {@link SOAPMessage}.
     */
    void writeTo(SOAPMessage saaj) throws SOAPException;
}
