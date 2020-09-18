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

package com.hh.xml.internal.ws.wsdl.writer;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.api.model.CheckedException;
import com.hh.xml.internal.ws.api.model.JavaMethod;
import com.hh.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.hh.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

import com.hh.webservice.ws.Action;
import com.hh.webservice.ws.FaultAction;
import com.hh.webservice.ws.soap.AddressingFeature;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * @author Arun Gupta
 * @author Rama Pulavarthi
 */
public class W3CAddressingWSDLGeneratorExtension extends WSDLGeneratorExtension {
    private boolean enabled;
    private boolean required = false;

    @Override
    public void start(WSDLGenExtnContext ctxt) {
        WSBinding binding = ctxt.getBinding();
        TypedXmlWriter root = ctxt.getRoot();
        enabled = binding.isFeatureEnabled(AddressingFeature.class);
        if (!enabled)
            return;
        AddressingFeature ftr = binding.getFeature(AddressingFeature.class);
        required = ftr.isRequired();
        root._namespace(AddressingVersion.W3C.wsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
    }

    @Override
    public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        if (!enabled)
            return;

        Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.input().equals("")) {
            addAttribute(input, a.input());
        } else {
            if (method.getBinding().getSOAPAction().equals("")) {
                //hack: generate default action for interop with .Net3.0 when soapAction is non-empty
                String defaultAction = getDefaultAction(method);
                addAttribute(input, defaultAction);
            }
        }
    }

    protected static final String getDefaultAction(JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        String delim = "/";
        // TODO: is this the correct way to find the separator ?
        try {
            URI uri = new URI(tns);
            if(uri.getScheme().equalsIgnoreCase("urn"))
                delim = ":";
        } catch (URISyntaxException e) {
            LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        if (tns.endsWith(delim))
            tns = tns.substring(0, tns.length() - 1);
        //this assumes that fromjava case there won't be input name.
        // if there is input name in future, then here name=inputName
        //else use operation name as follows.
        String name = (method.getMEP().isOneWay())?method.getOperationName():method.getOperationName()+"Request";

        return new StringBuilder(tns).append(delim).append(
                method.getOwner().getPortTypeName().getLocalPart()).append(
                delim).append(name).toString();
    }

    @Override
    public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        if (!enabled)
            return;

        Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.output().equals("")) {
            addAttribute(output, a.output());
        }
    }

    @Override
    public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
        if (!enabled)
            return;

        Action a = method.getSEIMethod().getAnnotation(Action.class);
        Class[] exs = method.getSEIMethod().getExceptionTypes();

        if (exs == null)
            return;

        if (a != null && a.fault() != null) {
            for (FaultAction fa : a.fault()) {
                if (fa.className().getName().equals(ce.getExceptionClass().getName())) {
                    if (fa.value().equals(""))
                        return;

                    addAttribute(fault, fa.value());
                    return;
                }
            }
        }
    }

    private void addAttribute(TypedXmlWriter writer, String attrValue) {
        writer._attribute(AddressingVersion.W3C.wsdlActionTag, attrValue);
    }

    @Override
    public void addBindingExtension(TypedXmlWriter binding) {
        if (!enabled)
            return;
        UsingAddressing ua = binding._element(AddressingVersion.W3C.wsdlExtensionTag, UsingAddressing.class);
        /*
        Do not generate wsdl:required=true
        if(required) {
            ua.required(true);
        }
        */
    }
     private static final Logger LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());
}
