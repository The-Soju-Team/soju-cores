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

import com.hh.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
import com.hh.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.hh.xml.internal.messaging.saaj.util.ParserPool;
import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import com.hh.webservice.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.hh.xml.internal.messaging.saaj.SOAPExceptionImpl;

import com.hh.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;

/**
 * EnvelopeFactory creates SOAP Envelope objects using different
 * underlying implementations.
 */
public class EnvelopeFactory {

    protected static final Logger
        log = Logger.getLogger(LogDomainConstants.SOAP_DOMAIN,
        "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");

    private static ParserPool parserPool = new ParserPool(5);

    public static Envelope createEnvelope(Source src, SOAPPartImpl soapPart)
        throws SOAPException
    {
        // Insert SAX filter to disallow Document Type Declarations since
        // they are not legal in SOAP
        SAXParser saxParser = null;
        if (src instanceof StreamSource) {
            if (src instanceof JAXMStreamSource) {
                try {
                    if (!SOAPPartImpl.lazyContentLength) {
                        ((JAXMStreamSource) src).reset();
                    }
                } catch (java.io.IOException ioe) {
                    log.severe("SAAJ0515.source.reset.exception");
                    throw new SOAPExceptionImpl(ioe);
                }
            }
            try {
                saxParser = parserPool.get();
            } catch (Exception e) {
                log.severe("SAAJ0601.util.newSAXParser.exception");
                throw new SOAPExceptionImpl(
                    "Couldn't get a SAX parser while constructing a envelope",
                    e);
            }
            InputSource is = SAXSource.sourceToInputSource(src);
            if (is.getEncoding()== null && soapPart.getSourceCharsetEncoding() != null) {
                is.setEncoding(soapPart.getSourceCharsetEncoding());
            }
            XMLReader rejectFilter;
            try {
                rejectFilter = new RejectDoctypeSaxFilter(saxParser);
            } catch (Exception ex) {
                log.severe("SAAJ0510.soap.cannot.create.envelope");
                throw new SOAPExceptionImpl(
                    "Unable to create envelope from given source: ",
                    ex);
            }
            src = new SAXSource(rejectFilter, is);
        }

        try {
            Transformer transformer =
                EfficientStreamingTransformer.newTransformer();
            DOMResult result = new DOMResult(soapPart);
            transformer.transform(src, result);

            Envelope env = (Envelope) soapPart.getEnvelope();
            return env;
        } catch (Exception ex) {
            if (ex instanceof SOAPVersionMismatchException) {
                throw (SOAPVersionMismatchException) ex;
            }
            log.severe("SAAJ0511.soap.cannot.create.envelope");
            throw new SOAPExceptionImpl(
                "Unable to create envelope from given source: ",
                ex);
        } finally {
            if (saxParser != null) {
                parserPool.returnParser(saxParser);
            }
        }
    }
}
