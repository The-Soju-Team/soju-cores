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

package com.hh.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.pipe.NextAction;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.resources.AddressingMessages;

import com.hh.webservice.ws.WebServiceException;

/**
 * WsaClientTube appears in the Tubeline only if addressing is enabled.
 * This tube checks the validity of addressing headers in the incoming messages
 * based on the WSDL model.
 * @author Rama Pulavarthi
 * @author Arun Gupta
 */
public class WsaClientTube extends WsaTube {
    // capture if the request expects a reply so that it can be used to
    // determine if its oneway for response validation.
    protected boolean expectReply = true;
    public WsaClientTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(wsdlPort, binding, next);
    }

    public WsaClientTube(WsaClientTube that, TubeCloner cloner) {
        super(that, cloner);
    }

    public WsaClientTube copy(TubeCloner cloner) {
        return new WsaClientTube(this, cloner);
    }

    @Override
    public @NotNull NextAction processRequest(Packet request) {
        expectReply = request.expectReply;
        return doInvoke(next,request);
   }

    @Override
    public @NotNull NextAction processResponse(Packet response) {
        // if one-way then, no validation
        if (response.getMessage() != null) {
            response = validateInboundHeaders(response);
            response.addSatellite(new WsaPropertyBag(addressingVersion,soapVersion,response));
        }

        return doReturnWith(response);
    }


    @Override
    protected void validateAction(Packet packet) {
        //There may not be a WSDL operation.  There may not even be a WSDL.
        //For instance this may be a RM CreateSequence message.
        WSDLBoundOperation wbo = getWSDLBoundOperation(packet);

        if (wbo == null)    return;

        String gotA = packet.getMessage().getHeaders().getAction(addressingVersion, soapVersion);
        if (gotA == null)
            throw new WebServiceException(AddressingMessages.VALIDATION_CLIENT_NULL_ACTION());

        String expected = helper.getOutputAction(packet);

        if (expected != null && !gotA.equals(expected))
            throw new ActionNotSupportedException(gotA);
    }

}
