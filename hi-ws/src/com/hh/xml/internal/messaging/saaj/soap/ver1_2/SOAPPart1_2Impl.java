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

/**
*
* @author SAAJ RI Development Team
*/
package com.hh.xml.internal.messaging.saaj.soap.ver1_2;

import com.hh.xml.internal.messaging.saaj.soap.SOAPPartImpl;
import com.hh.xml.internal.messaging.saaj.soap.EnvelopeFactory;
import com.hh.xml.internal.messaging.saaj.soap.MessageImpl;
import com.hh.xml.internal.messaging.saaj.soap.Envelope;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hh.webservice.soap.SOAPConstants;
import com.hh.webservice.soap.SOAPException;
import javax.xml.transform.Source;

import com.hh.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.hh.xml.internal.messaging.saaj.util.XMLDeclarationParser;

public class SOAPPart1_2Impl extends SOAPPartImpl implements SOAPConstants{

    protected static final Logger log =
        Logger.getLogger(SOAPPart1_2Impl.class.getName(),
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

    public SOAPPart1_2Impl() {
        super();
    }

    public SOAPPart1_2Impl(MessageImpl message) {
        super(message);
    }

    protected String getContentType() {
        return "application/soap+xml";
    }

    protected Envelope createEmptyEnvelope(String prefix) throws SOAPException {
        return new Envelope1_2Impl(getDocument(), prefix, true, true);
    }

    protected Envelope createEnvelopeFromSource() throws SOAPException {
        XMLDeclarationParser parser = lookForXmlDecl();
        Source tmp = source;
        source = null;
        EnvelopeImpl envelope = (EnvelopeImpl)EnvelopeFactory.createEnvelope(tmp, this);
        if (!envelope.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            log.severe("SAAJ0415.ver1_2.msg.invalid.soap1.2");
            throw new SOAPException("InputStream does not represent a valid SOAP 1.2 Message");
        }

        if (parser != null) { //can be null if source was a DomSource and not StreamSource
            if (!omitXmlDecl) {
                envelope.setOmitXmlDecl("no");
                envelope.setXmlDecl(parser.getXmlDeclaration());
                envelope.setCharsetEncoding(parser.getEncoding());
            }
        }
        return envelope;

    }

    protected SOAPPartImpl duplicateType() {
        return new SOAPPart1_2Impl();
    }

}
