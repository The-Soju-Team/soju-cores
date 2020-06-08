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
import com.hh.xml.internal.bind.api.BridgeContext;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.api.addressing.WSEndpointReference;
import com.hh.xml.internal.ws.api.message.Header;
import com.hh.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Set;

/**
 * Partial default implementation of {@link Header}.
 *
 * <p>
 * This is meant to be a convenient base class
 * for {@link Header}-derived classes.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractHeaderImpl implements Header {

    protected AbstractHeaderImpl() {
    }

    /**
     * @deprecated
     */
    public final <T> T readAsJAXB(Bridge<T> bridge, BridgeContext context) throws JAXBException {
        return readAsJAXB(bridge);
    }

    public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        try {
            return (T)unmarshaller.unmarshal(readHeader());
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
        try {
            return bridge.unmarshal(readHeader());
        } catch (XMLStreamException e) {
            throw new JAXBException(e);
        }
    }

    /**
     * Default implementation that copies the infoset. Not terribly efficient.
     */
    public WSEndpointReference readAsEPR(AddressingVersion expected) throws XMLStreamException {
        XMLStreamReader xsr = readHeader();
        WSEndpointReference epr = new WSEndpointReference(xsr, expected);
        XMLStreamReaderFactory.recycle(xsr);
        return epr;
    }

    public boolean isIgnorable(@NotNull SOAPVersion soapVersion, @NotNull Set<String> roles) {
        // check mustUnderstand
        String v = getAttribute(soapVersion.nsUri, "mustUnderstand");
        if(v==null || !parseBool(v)) return true;

        // now role
        return !roles.contains(getRole(soapVersion));
    }

    public @NotNull String getRole(@NotNull SOAPVersion soapVersion) {
        String v = getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if(v==null)
            v = soapVersion.implicitRole;
        return v;
    }

    public boolean isRelay() {
        String v = getAttribute(SOAPVersion.SOAP_12.nsUri,"relay");
        if(v==null) return false;   // on SOAP 1.1 message there shouldn't be such an attribute, so this works fine
        return parseBool(v);
    }

    public String getAttribute(QName name) {
        return getAttribute(name.getNamespaceURI(),name.getLocalPart());
    }

    /**
     * Parses a string that looks like <tt>xs:boolean</tt> into boolean.
     *
     * This method assumes that the whilespace normalization has already taken place.
     */
    protected final boolean parseBool(String value) {
        if(value.length()==0)
            return false;

        char ch = value.charAt(0);
        return ch=='t' || ch=='1';
    }

    public String getStringContent() {
        try {
            XMLStreamReader xsr = readHeader();
            xsr.nextTag();
            return xsr.getElementText();
        } catch (XMLStreamException e) {
            return null;
        }
    }

    protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
}
