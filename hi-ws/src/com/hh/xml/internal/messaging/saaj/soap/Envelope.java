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

import com.hh.webservice.soap.SOAPEnvelope;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Source;

/**
 * Different implementations for SOAP Envelope must all implement this
 * interface.
 *
 * @author Anil Vijendran (akv@eng.sun.com)
 */
public interface Envelope extends SOAPEnvelope {
    /**
     * Get the content as a JAXP Source.
     */
    Source getContent();

    /**
     * Output the content.
     */
    void output(OutputStream out) throws IOException;

    /**
     * Output the content.
     */
    void output(OutputStream out, boolean isFastInfoset) throws IOException;
}
