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

package com.hh.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.resources.ServerMessages;
import com.hh.xml.internal.ws.fault.SOAPFaultBuilder;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;

/**
 * This class abstracts the process of identifying the wsdl operation from a SOAP Message request.
 * This is primarily for dispatching the request messages to an endpoint method.
 *
 * Different implementations of {@link WSDLOperationFinder} are used underneath to identify the wsdl operation based on
 * if AddressingFeature is enabled or not.
 *
 * @author Rama Pulavarthi
 */
public class OperationDispatcher {
    private List<WSDLOperationFinder> opFinders;
    private WSBinding binding;

    public OperationDispatcher(@NotNull WSDLPort wsdlModel, @NotNull WSBinding binding, @Nullable SEIModel seiModel) {
        this.binding = binding;
        opFinders = new ArrayList<WSDLOperationFinder>();
        if (binding.getAddressingVersion() != null) {
            opFinders.add(new ActionBasedOperationFinder(wsdlModel, binding, seiModel));
        }
        opFinders.add(new PayloadQNameBasedOperationFinder(wsdlModel, binding, seiModel));
        opFinders.add(new SOAPActionBasedOperationFinder(wsdlModel, binding, seiModel));

    }

    /**
     *
     * @param request Packet
     * @return QName of the wsdl operation.
     * @throws DispatchException if a unique operartion cannot be associated with this packet.
     */
    public @NotNull QName getWSDLOperationQName(Packet request) throws DispatchException {
        QName opName;
        for(WSDLOperationFinder finder: opFinders) {
            opName = finder.getWSDLOperationQName(request);
            if(opName != null)
                return opName;
        }
        //No way to dispatch this request
        String err = MessageFormat.format("Request=[SOAPAction={0},Payload='{'{1}'}'{2}]",
                request.soapAction, request.getMessage().getPayloadNamespaceURI(),
                request.getMessage().getPayloadLocalPart());
        String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(err);
        Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(
                binding.getSOAPVersion(), faultString, binding.getSOAPVersion().faultCodeClient);
        throw new DispatchException(faultMsg);

    }
}
