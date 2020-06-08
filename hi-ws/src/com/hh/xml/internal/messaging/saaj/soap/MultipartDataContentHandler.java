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

package com.hh.xml.internal.messaging.saaj.soap;

import java.io.*;
import java.awt.datatransfer.DataFlavor;
import javax.activation.*;
import com.hh.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.hh.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.hh.xml.internal.messaging.saaj.util.ByteOutputStream;

public class MultipartDataContentHandler implements DataContentHandler {
    private ActivationDataFlavor myDF = new ActivationDataFlavor(
            com.hh.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart.class,
            "multipart/mixed",
            "Multipart");

    /**
     * Return the DataFlavors for this <code>DataContentHandler</code>.
     *
     * @return The DataFlavors
     */
    public DataFlavor[] getTransferDataFlavors() { // throws Exception;
        return new DataFlavor[] { myDF };
    }

    /**
     * Return the Transfer Data of type DataFlavor from InputStream.
     *
     * @param df The DataFlavor
     * @param ins The InputStream corresponding to the data
     * @return String object
     */
    public Object getTransferData(DataFlavor df, DataSource ds) {
        // use myDF.equals to be sure to get ActivationDataFlavor.equals,
        // which properly ignores Content-Type parameters in comparison
        if (myDF.equals(df))
            return getContent(ds);
        else
            return null;
    }

    /**
     * Return the content.
     */
    public Object getContent(DataSource ds) {
        try {
            return new MimeMultipart(
                ds, new ContentType(ds.getContentType()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Write the object to the output stream, using the specific MIME type.
     */
    public void writeTo(Object obj, String mimeType, OutputStream os)
                        throws IOException {
        if (obj instanceof MimeMultipart) {
            try {
                //TODO: temporarily allow only ByteOutputStream
                // Need to add writeTo(OutputStream) on MimeMultipart
                ByteOutputStream baos = null;
                if (os instanceof ByteOutputStream) {
                    baos = (ByteOutputStream)os;
                } else {
                    throw new IOException("Input Stream expected to be a com.sun.xml.internal.messaging.saaj.util.ByteOutputStream, but found " +
                        os.getClass().getName());
                }
                ((MimeMultipart)obj).writeTo(baos);
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
        }
    }
}
