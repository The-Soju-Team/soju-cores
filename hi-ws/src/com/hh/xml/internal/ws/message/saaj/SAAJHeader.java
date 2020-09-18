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

package com.hh.xml.internal.ws.message.saaj;

import com.hh.xml.internal.ws.api.message.Header;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.message.DOMHeader;
import com.sun.istack.internal.NotNull;

import com.hh.webservice.soap.SOAPHeaderElement;

/**
 * {@link Header} for {@link SOAPHeaderElement}.
 *
 * @author Vivek Pandey
 */
public final class SAAJHeader extends DOMHeader<SOAPHeaderElement> {
    public SAAJHeader(SOAPHeaderElement header) {
        // we won't rely on any of the super class method that uses SOAPVersion,
        // so we can just pass in a dummy version
        super(header);
    }

    public
    @NotNull
    String getRole(@NotNull SOAPVersion soapVersion) {
        String v = getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if(v==null || v.equals(""))
            v = soapVersion.implicitRole;
        return v;
    }
}
