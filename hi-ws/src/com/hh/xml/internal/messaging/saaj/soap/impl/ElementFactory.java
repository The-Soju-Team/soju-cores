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

package com.hh.xml.internal.messaging.saaj.soap.impl;

import com.hh.webservice.soap.Name;
import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPFault;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.Header1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_2.Body1_2Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.FaultElement1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.Header1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.Body1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.Envelope1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.hh.xml.internal.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import javax.xml.namespace.QName;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;


public class ElementFactory {
    public static SOAPElement createElement(
        SOAPDocumentImpl ownerDocument,
        Name name) {
        return createElement(
            ownerDocument,
            name.getLocalName(),
            name.getPrefix(),
            name.getURI());
    }
    public static SOAPElement createElement(
        SOAPDocumentImpl ownerDocument,
        QName name) {
        return createElement(
            ownerDocument,
            name.getLocalPart(),
            name.getPrefix(),
            name.getNamespaceURI());
    }

    public static SOAPElement createElement(
        SOAPDocumentImpl ownerDocument,
        String localName,
        String prefix,
        String namespaceUri) {


        if (ownerDocument == null) {
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                ownerDocument = new SOAPPart1_1Impl().getDocument();
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                ownerDocument = new SOAPPart1_2Impl().getDocument();
            } else {
                ownerDocument = new SOAPDocumentImpl(null);
            }
        }

        SOAPElement newElement =
            createNamedElement(ownerDocument, localName, prefix, namespaceUri);

        return newElement != null
            ? newElement
            : new ElementImpl(
                ownerDocument,
                namespaceUri,
                NameImpl.createQName(prefix, localName));
    }

    public static SOAPElement createNamedElement(
        SOAPDocumentImpl ownerDocument,
        String localName,
        String prefix,
        String namespaceUri) {

        if (prefix == null) {
            prefix = NameImpl.SOAP_ENVELOPE_PREFIX;
        }

        if (localName.equalsIgnoreCase("Envelope")) {
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                return new Envelope1_1Impl(ownerDocument, prefix);
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                return new Envelope1_2Impl(ownerDocument, prefix);
            }
        }
        if (localName.equalsIgnoreCase("Body")) {
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                return new Body1_1Impl(ownerDocument, prefix);
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                return new Body1_2Impl(ownerDocument, prefix);
            }
        }
        if (localName.equalsIgnoreCase("Header")) {
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                return new Header1_1Impl(ownerDocument, prefix);
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                return new Header1_2Impl(ownerDocument, prefix);
            }
        }
        if (localName.equalsIgnoreCase("Fault")) {
            SOAPFault fault = null;
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                fault = new Fault1_1Impl(ownerDocument, prefix);
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                fault = new Fault1_2Impl(ownerDocument, prefix);
            }

            if (fault != null) {
//                try {
//                    fault.addNamespaceDeclaration(
//                        NameImpl.SOAP_ENVELOPE_PREFIX,
//                        SOAPConstants.URI_NS_SOAP_ENVELOPE);
//                    fault.setFaultCode(
//                        NameImpl.create(
//                            "Server",
//                            NameImpl.SOAP_ENVELOPE_PREFIX,
//                            SOAPConstants.URI_NS_SOAP_ENVELOPE));
//                    fault.setFaultString(
//                        "Fault string, and possibly fault code, not set");
//                } catch (SOAPException e) {
//                }
                return fault;
            }

        }
        if (localName.equalsIgnoreCase("Detail")) {
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                return new Detail1_1Impl(ownerDocument, prefix);
            } else if (NameImpl.SOAP12_NAMESPACE.equals(namespaceUri)) {
                return new Detail1_2Impl(ownerDocument, prefix);
            }
        }
        if (localName.equalsIgnoreCase("faultcode")
            || localName.equalsIgnoreCase("faultstring")
            || localName.equalsIgnoreCase("faultactor")) {
            // SOAP 1.2 does not have fault(code/string/actor)
            // So there is no else case required
            if (NameImpl.SOAP11_NAMESPACE.equals(namespaceUri)) {
                return new FaultElement1_1Impl(ownerDocument,
                                               localName,
                                               prefix);
            }
        }

        return null;
    }

    protected static void invalidCreate(String msg) {
        throw new TreeException(msg);
    }
}
