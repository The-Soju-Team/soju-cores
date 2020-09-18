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
* @author JAX-RPC RI Development Team
*/
package com.hh.xml.internal.messaging.saaj.soap.ver1_2;

import java.io.IOException;
import java.io.InputStream;

import com.hh.webservice.soap.*;

import com.hh.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.hh.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import com.hh.xml.internal.messaging.saaj.soap.MessageImpl;

public class SOAPMessageFactory1_2Impl extends MessageFactoryImpl {

    public SOAPMessage createMessage() throws SOAPException {
        return new Message1_2Impl();
    }

    public SOAPMessage createMessage(boolean isFastInfoset,
        boolean acceptFastInfoset) throws SOAPException
    {
        return new Message1_2Impl(isFastInfoset, acceptFastInfoset);
    }

    public SOAPMessage createMessage(MimeHeaders headers, InputStream in)
        throws IOException, SOAPExceptionImpl {
        if ((headers == null) || (getContentType(headers) == null)) {
            headers = new MimeHeaders();
            headers.setHeader("Content-Type", SOAPConstants.SOAP_1_2_CONTENT_TYPE);
        }
        MessageImpl msg = new Message1_2Impl(headers, in);
        msg.setLazyAttachments(lazyAttachments);
        return msg;
    }
}
