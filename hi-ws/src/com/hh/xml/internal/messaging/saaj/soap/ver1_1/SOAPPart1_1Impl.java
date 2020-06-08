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
package com.hh.xml.internal.messaging.saaj.soap.ver1_1;

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
import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;
import com.hh.xml.internal.messaging.saaj.util.XMLDeclarationParser;

public class SOAPPart1_1Impl extends SOAPPartImpl implements SOAPConstants {

    protected static final Logger log =
        Logger.getLogger(LogDomainConstants.SOAP_VER1_1_DOMAIN,
                         "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

    public SOAPPart1_1Impl() {
        super();
     }

    public SOAPPart1_1Impl(MessageImpl message) {
        super(message);
    }

    protected String getContentType() {
        return isFastInfoset() ? "application/fastinfoset" : "text/xml";
    }

    protected Envelope createEnvelopeFromSource() throws SOAPException {
        // Record the presence of xml declaration before the envelope gets
        // created.
        XMLDeclarationParser parser = lookForXmlDecl();
        Source tmp = source;
        source = null;
        EnvelopeImpl envelope =
            (EnvelopeImpl) EnvelopeFactory.createEnvelope(tmp, this);

        if (!envelope.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
            log.severe("SAAJ0304.ver1_1.msg.invalid.SOAP1.1");
            throw new SOAPException("InputStream does not represent a valid SOAP 1.1 Message");
        }

        if (parser != null && !omitXmlDecl) {
            envelope.setOmitXmlDecl("no");
            envelope.setXmlDecl(parser.getXmlDeclaration());
            envelope.setCharsetEncoding(parser.getEncoding());
        }
        return envelope;
    }

    protected Envelope createEmptyEnvelope(String prefix)
        throws SOAPException {
        return new Envelope1_1Impl(getDocument(), prefix, true, true);
    }

    protected SOAPPartImpl duplicateType() {
        return new SOAPPart1_1Impl();
    }

}
