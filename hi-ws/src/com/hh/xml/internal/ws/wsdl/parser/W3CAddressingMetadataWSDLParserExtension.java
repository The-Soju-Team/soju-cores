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

package com.hh.xml.internal.ws.wsdl.parser;

import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

/**
 * W3C WS-Addressing Runtime WSDL parser extension that parses
 * WS-Addressing Metadata wsdl extensibility elements
 * This mainly reads wsam:Action element on input/output/fault messages in wsdl.
 *
 * @author Rama Pulavarthi
 */
public class W3CAddressingMetadataWSDLParserExtension extends W3CAddressingWSDLParserExtension {

    String METADATA_WSDL_EXTN_NS = "http://www.w3.org/2007/05/addressing/metadata";
    QName METADATA_WSDL_ACTION_TAG = new QName(METADATA_WSDL_EXTN_NS, "Action", "wsam");
    @Override
    public boolean bindingElements(WSDLBoundPortType binding, XMLStreamReader reader) {
        return false;
    }

    @Override
    public boolean portElements(WSDLPort port, XMLStreamReader reader) {
        return false;
    }

    @Override
    public boolean bindingOperationElements(WSDLBoundOperation operation, XMLStreamReader reader) {
        return false;
    }

    
    protected void patchAnonymousDefault(WSDLBoundPortTypeImpl binding) {
    }

    @Override
    protected String getNamespaceURI() {
        return METADATA_WSDL_EXTN_NS;
    }

    @Override
    protected QName getWsdlActionTag() {
        return  METADATA_WSDL_ACTION_TAG;
    }
}
