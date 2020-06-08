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

import com.hh.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.hh.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.hh.xml.internal.ws.api.model.JavaMethod;
import com.hh.xml.internal.ws.api.model.CheckedException;
import com.hh.xml.internal.txw2.TypedXmlWriter;
import static com.hh.xml.internal.ws.addressing.W3CAddressingMetadataConstants.*;
import com.hh.xml.internal.ws.model.JavaMethodImpl;
import com.hh.xml.internal.ws.model.CheckedExceptionImpl;
import com.hh.xml.internal.ws.addressing.WsaActionUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * This extension class generates wsam:Action values for input, output and faults in the generated wsdl.
 *
 * @author Rama Pulavarthi
 */
public class W3CAddressingMetadataWSDLGeneratorExtension extends
        WSDLGeneratorExtension {

    @Override
    public void start(WSDLGenExtnContext ctxt) {
        TypedXmlWriter root = ctxt.getRoot();
        root._namespace(WSAM_NAMESPACE_NAME, WSAM_PREFIX_NAME);
    }

    @Override
    public void addOperationInputExtension(TypedXmlWriter input,
                                           JavaMethod method) {
        input._attribute(WSAM_ACTION_QNAME, getInputAction(method));
    }

    @Override
    public void addOperationOutputExtension(TypedXmlWriter output,
                                            JavaMethod method) {
        output._attribute(WSAM_ACTION_QNAME, getOutputAction(method));
    }

    @Override
    public void addOperationFaultExtension(TypedXmlWriter fault,
                                           JavaMethod method, CheckedException ce) {
        fault._attribute(WSAM_ACTION_QNAME, getFaultAction(method, ce));
    }


    private static final String getInputAction(JavaMethod method) {
        String inputaction = ((JavaMethodImpl)method).getInputAction();
        if (inputaction.equals("")) {
            // Calculate default action
            inputaction = getDefaultInputAction(method);
        }
        return inputaction;
    }

    protected static final String getDefaultInputAction(JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        String delim = getDelimiter(tns);
        if (tns.endsWith(delim))
            tns = tns.substring(0, tns.length() - 1);
        //this assumes that fromjava case there won't be input name.
        // if there is input name in future, then here name=inputName
        //else use operation name as follows.
        String name = (method.getMEP().isOneWay()) ?
                method.getOperationName() : method.getOperationName() + "Request";

        return new StringBuilder(tns).append(delim).append(
                method.getOwner().getPortTypeName().getLocalPart()).append(
                delim).append(name).toString();
    }

    private static final String getOutputAction(JavaMethod method) {
        String outputaction = ((JavaMethodImpl)method).getOutputAction();
        if(outputaction.equals(""))
            outputaction = getDefaultOutputAction(method);
        return outputaction;
    }

    protected static final String getDefaultOutputAction(JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        String delim = getDelimiter(tns);
        if (tns.endsWith(delim))
            tns = tns.substring(0, tns.length() - 1);
        //this assumes that fromjava case there won't be output name.
        // if there is input name in future, then here name=outputName
        //else use operation name as follows.
        String name = method.getOperationName() + "Response";

        return new StringBuilder(tns).append(delim).append(
                method.getOwner().getPortTypeName().getLocalPart()).append(
                delim).append(name).toString();
    }


    private static final String getDelimiter(String tns) {
        String delim = "/";
        // TODO: is this the correct way to find the separator ?
        try {
            URI uri = new URI(tns);
            if ((uri.getScheme() != null) && uri.getScheme().equalsIgnoreCase("urn"))
                delim = ":";
        } catch (URISyntaxException e) {
            LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        return delim;

    }

    private static final String getFaultAction(JavaMethod method,
                                               CheckedException ce) {
        String faultaction = ((CheckedExceptionImpl)ce).getFaultAction();
        if (faultaction.equals("")) {
            faultaction = getDefaultFaultAction(method,ce);
        }
        return faultaction;
    }

    protected static final String getDefaultFaultAction(JavaMethod method, CheckedException ce) {
        return WsaActionUtil.getDefaultFaultAction(method,ce);
    }

    private static final Logger LOGGER =
            Logger.getLogger(W3CAddressingMetadataWSDLGeneratorExtension.class.getName());
}
