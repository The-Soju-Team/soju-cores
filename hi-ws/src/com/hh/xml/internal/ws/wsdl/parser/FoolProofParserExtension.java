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


import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLService;
import com.hh.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * {@link WSDLParserExtension} filter that checks if
 * another {@link WSDLParserExtension} is following the contract.
 *
 * <p>
 * This code protects the JAX-WS RI from broken extensions.
 *
 * <p>
 * For now it just checks if {@link XMLStreamReader} is placed
 * at the expected start/end element.
 *
 * @author Kohsuke Kawaguchi
 */
final class FoolProofParserExtension extends DelegatingParserExtension {

    public FoolProofParserExtension(WSDLParserExtension core) {
        super(core);
    }

    private QName pre(XMLStreamReader xsr) {
        return xsr.getName();
    }

    private boolean post(QName tagName, XMLStreamReader xsr, boolean result) {
        if(!tagName.equals(xsr.getName()))
            return foundFool();
        if(result) {
            if(xsr.getEventType()!=XMLStreamConstants.END_ELEMENT)
                foundFool();
        } else {
            if(xsr.getEventType()!=XMLStreamConstants.START_ELEMENT)
                foundFool();
        }
        return result;
    }

    private boolean foundFool() {
        throw new AssertionError("XMLStreamReader is placed at the wrong place after invoking "+core);
    }

    public boolean serviceElements(WSDLService service, XMLStreamReader reader) {
        return post(pre(reader),reader,super.serviceElements(service, reader));
    }

    public boolean portElements(WSDLPort port, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portElements(port, reader));
    }

    public boolean definitionsElements(XMLStreamReader reader) {
        return post(pre(reader),reader,super.definitionsElements(reader));
    }

    public boolean bindingElements(WSDLBoundPortType binding, XMLStreamReader reader) {
        return post(pre(reader),reader,super.bindingElements(binding, reader));
    }

    public boolean portTypeElements(WSDLPortType portType, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portTypeElements(portType, reader));
    }

    public boolean portTypeOperationElements(WSDLOperation operation, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portTypeOperationElements(operation, reader));
    }

    public boolean bindingOperationElements(WSDLBoundOperation operation, XMLStreamReader reader) {
        return post(pre(reader),reader,super.bindingOperationElements(operation, reader));
    }

    public boolean messageElements(WSDLMessage msg, XMLStreamReader reader) {
        return post(pre(reader),reader,super.messageElements(msg, reader));
    }

    public boolean portTypeOperationInputElements(WSDLInput input, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portTypeOperationInputElements(input, reader));
    }

    public boolean portTypeOperationOutputElements(WSDLOutput output, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portTypeOperationOutputElements(output, reader));
    }

    public boolean portTypeOperationFaultElements(WSDLFault fault, XMLStreamReader reader) {
        return post(pre(reader),reader,super.portTypeOperationFaultElements(fault, reader));
    }

    public boolean bindingOperationInputElements(WSDLBoundOperation operation, XMLStreamReader reader) {
        return super.bindingOperationInputElements(operation, reader);
    }

    public boolean bindingOperationOutputElements(WSDLBoundOperation operation, XMLStreamReader reader) {
        return post(pre(reader),reader,super.bindingOperationOutputElements(operation, reader));
    }

    public boolean bindingOperationFaultElements(WSDLBoundFault fault, XMLStreamReader reader) {
        return post(pre(reader),reader,super.bindingOperationFaultElements(fault, reader));
    }
}
