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

package com.hh.xml.internal.ws.protocol.soap;

import com.hh.xml.internal.ws.api.SOAPVersion;
import static com.hh.xml.internal.ws.api.SOAPVersion.SOAP_11;
import static com.hh.xml.internal.ws.api.SOAPVersion.SOAP_12;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.api.message.Header;
import com.hh.xml.internal.ws.api.message.HeaderList;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.binding.SOAPBindingImpl;
import com.hh.xml.internal.ws.message.DOMHeader;
import com.hh.xml.internal.ws.fault.SOAPFaultBuilder;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPException;
import com.hh.webservice.soap.SOAPFault;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.soap.SOAPBinding;
import com.hh.webservice.ws.soap.SOAPFaultException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Rama Pulavarthi
 */

abstract class MUTube extends AbstractFilterTubeImpl {

    private static final String MU_FAULT_DETAIL_LOCALPART = "NotUnderstood";
    private final static QName MU_HEADER_DETAIL = new QName(SOAPVersion.SOAP_12.nsUri, MU_FAULT_DETAIL_LOCALPART);
    //TODO: change
    protected static final Logger logger = Logger.getLogger(com.hh.xml.internal.ws.util.Constants.LoggingDomain + ".soap.decoder");
    private final static String MUST_UNDERSTAND_FAULT_MESSAGE_STRING =
            "One or more mandatory SOAP header blocks not understood";

    protected final SOAPVersion soapVersion;
    protected SOAPBindingImpl binding;

    protected MUTube(WSBinding binding, Tube next) {
        super(next);
        // MUPipe should n't be used for bindings other than SOAP.
        if (!(binding instanceof SOAPBinding)) {
            throw new WebServiceException(
                    "MUPipe should n't be used for bindings other than SOAP.");
        }
        this.binding = (SOAPBindingImpl) binding;
        this.soapVersion = binding.getSOAPVersion();
    }

    protected MUTube(MUTube that, TubeCloner cloner) {
        super(that, cloner);
        binding = that.binding;
        soapVersion = that.soapVersion;
    }

    /**
     * @param headers HeaderList that needs MU processing
     * @param roles        Roles configured on the Binding. Required Roles supposed to be assumbed a by a
     *                     SOAP Binding implementation are added.
     * @param handlerKnownHeaders Set of headers that the handlerchain associated with the binding understands
     * @return returns the headers that have mustUnderstand attribute and are not understood
     *         by the binding.
     */
    public final Set<QName> getMisUnderstoodHeaders(HeaderList headers, Set<String> roles,
                                                    Set<QName> handlerKnownHeaders) {
        Set<QName> notUnderstoodHeaders = null;
        for (int i = 0; i < headers.size(); i++) {
            if (!headers.isUnderstood(i)) {
                Header header = headers.get(i);
                if (!header.isIgnorable(soapVersion, roles)) {
                    QName qName = new QName(header.getNamespaceURI(), header.getLocalPart());
                    // see if the binding can understand it
                    if (!binding.understandsHeader(qName)) {
                        if (!handlerKnownHeaders.contains(qName)) {
                            logger.info("Element not understood=" + qName);
                            if (notUnderstoodHeaders == null)
                                notUnderstoodHeaders = new HashSet<QName>();
                            notUnderstoodHeaders.add(qName);
                        }
                    }
                }
            }
        }
        return notUnderstoodHeaders;
    }

    /**
     * @param notUnderstoodHeaders
     * @return SOAPfaultException with SOAPFault representing the MustUnderstand SOAP Fault.
     *         notUnderstoodHeaders are added in the fault detail.
     */
    final SOAPFaultException createMUSOAPFaultException(Set<QName> notUnderstoodHeaders) {
        try {
            SOAPFault fault = soapVersion.saajSoapFactory.createFault(
                MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                soapVersion.faultCodeMustUnderstand);
            fault.setFaultString("MustUnderstand headers:" +
                notUnderstoodHeaders + " are not understood");
            return new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        }
    }

    /**
     * This should be used only in ServerMUPipe
     *
     * @param notUnderstoodHeaders
     * @return Message representing a SOAPFault
     *         In SOAP 1.1, notUnderstoodHeaders are added in the fault Detail
     *         in SOAP 1.2, notUnderstoodHeaders are added as the SOAP Headers
     */

    final Message createMUSOAPFaultMessage(Set<QName> notUnderstoodHeaders) {
        try {
            String faultString = MUST_UNDERSTAND_FAULT_MESSAGE_STRING;
            if (soapVersion == SOAP_11) {
                faultString = "MustUnderstand headers:" + notUnderstoodHeaders + " are not understood";
            }
            Message  muFaultMessage = SOAPFaultBuilder.createSOAPFaultMessage(
                    soapVersion,faultString,soapVersion.faultCodeMustUnderstand);

            if (soapVersion == SOAP_12) {
                addHeader(muFaultMessage, notUnderstoodHeaders);
            }
            return muFaultMessage;
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        }
    }

    private static void addHeader(Message m, Set<QName> notUnderstoodHeaders) throws SOAPException {
        for (QName qname : notUnderstoodHeaders) {
            SOAPElement soapEl = SOAP_12.saajSoapFactory.createElement(MU_HEADER_DETAIL);
            soapEl.addNamespaceDeclaration("abc", qname.getNamespaceURI());
            soapEl.setAttribute("qname", "abc:" + qname.getLocalPart());
            Header header = new DOMHeader<Element>(soapEl);
            m.getHeaders().add(header);
        }
    }
}
