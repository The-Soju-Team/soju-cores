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

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.bind.api.Bridge;
import com.hh.xml.internal.ws.api.message.Attachment;
import com.hh.xml.internal.ws.util.ASCIIUtility;
import com.hh.xml.internal.ws.util.ByteArrayBuffer;
import com.hh.xml.internal.ws.encoding.DataSourceStreamingDataHandler;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import com.hh.webservice.soap.AttachmentPart;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import com.hh.webservice.ws.WebServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jitendra Kotamraju
 */
public final class JAXBAttachment implements Attachment, DataSource {

    private final String contentId;
    private final String mimeType;
    private final Object jaxbObject;
    private final Bridge bridge;

    public JAXBAttachment(@NotNull String contentId, Object jaxbObject, Bridge bridge, String mimeType) {
        this.contentId = contentId;
        this.jaxbObject = jaxbObject;
        this.bridge = bridge;
        this.mimeType = mimeType;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return mimeType;
    }

    public byte[] asByteArray() {
        ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            writeTo(bab);
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
        return bab.getRawData();
    }

    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(this);
    }

    public Source asSource() {
        return new StreamSource(asInputStream());
    }

    public InputStream asInputStream() {
        ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            writeTo(bab);
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
        return bab.newInputStream();
    }

    public void writeTo(OutputStream os) throws IOException {
        try {
            bridge.marshal(jaxbObject, os, null);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }

    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(asDataHandler());
        part.setContentId(contentId);
        saaj.addAttachmentPart(part);
    }

    public InputStream getInputStream() throws IOException {
        return asInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return null;
    }

}
