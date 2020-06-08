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

import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.addressing.WSEndpointReference;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.hh.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import static com.hh.xml.internal.ws.addressing.W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED;
import static com.hh.xml.internal.ws.addressing.W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED;
import com.hh.xml.internal.ws.resources.AddressingMessages;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import com.hh.webservice.ws.soap.AddressingFeature;
import com.hh.webservice.ws.WebServiceException;

/**
 * @author Rama Pulavarthi
 */
public class W3CWsaServerTube extends WsaServerTube{
    private final AddressingFeature af;

    public W3CWsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
        super(endpoint, wsdlPort, binding, next);
        af = binding.getFeature(AddressingFeature.class);
    }

    public W3CWsaServerTube(W3CWsaServerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.af = that.af;
    }

    @Override
    public W3CWsaServerTube copy(TubeCloner cloner) {
        return new W3CWsaServerTube(this, cloner);
    }

    @Override
    protected void checkMandatoryHeaders(
            Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo,
            boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo,
                foundFaultTo, foundMessageId, foundRelatesTo);

        // find Req/Response or Oneway using WSDLModel(if it is availabe)
        WSDLBoundOperation wbo = getWSDLBoundOperation(packet);
        // Taking care of protocol messages as they do not have any corresponding operations
        if (wbo != null) {
            // if two-way and no wsa:MessageID is found
            if (!wbo.getOperation().isOneWay() && !foundMessageId) {
                throw new MissingAddressingHeaderException(addressingVersion.messageIDTag,packet);
            }
        }

    }

    @Override
    protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
        return getResponseRequirement(wbo) ==  WSDLBoundOperation.ANONYMOUS.required;
    }

    private WSDLBoundOperation.ANONYMOUS getResponseRequirement(@Nullable WSDLBoundOperation wbo) {
        try {
            if (af.getResponses() == AddressingFeature.Responses.ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.required;
            } else if (af.getResponses() == AddressingFeature.Responses.NON_ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.prohibited;
            }
        } catch (NoSuchMethodError e) {
            //Ignore error, defaut to optional
        }
        //wsaw wsdl binding case will have some value set on wbo
        return wbo != null ? wbo.getAnonymous() : WSDLBoundOperation.ANONYMOUS.optional;
    }

    @Override
    protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
        String replyToValue = null;
        String faultToValue = null;

        if (replyTo != null)
            replyToValue = replyTo.getAddress();

        if (faultTo != null)
            faultToValue = faultTo.getAddress();
        WSDLBoundOperation.ANONYMOUS responseRequirement = getResponseRequirement(wbo);

        switch (responseRequirement) {
            case prohibited:
                if (replyToValue != null && replyToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);

                if (faultToValue != null && faultToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
                break;
            case required:
                if (replyToValue != null && !replyToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, ONLY_ANONYMOUS_ADDRESS_SUPPORTED);

                if (faultToValue != null && !faultToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
                break;
            default:
                // ALL: no check
        }
    }

    /*
     @Override
    protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
        return getResponseRequirement(wbo) ==  AddressingFeature.Responses.ANONYMOUS;
    }

    private AddressingFeature.Responses getResponseRequirement(@Nullable WSDLBoundOperation wbo) {
        if (af.getResponses() == AddressingFeature.Responses.ALL && wbo != null) {
            //wsaw wsdl binding case will have some value set on wbo
            WSDLBoundOperation.ANONYMOUS anon = wbo.getAnonymous();
            if (wbo.getAnonymous() == WSDLBoundOperation.ANONYMOUS.required)
                return AddressingFeature.Responses.ANONYMOUS;
            else if (wbo.getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited)
                return AddressingFeature.Responses.NON_ANONYMOUS;
            else
                return AddressingFeature.Responses.ALL;

        } else
            return af.getResponses();
    }

    @Override
    protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
        String replyToValue = null;
        String faultToValue = null;

        if (replyTo != null)
            replyToValue = replyTo.getAddress();

        if (faultTo != null)
            faultToValue = faultTo.getAddress();
        AddressingFeature.Responses responseRequirement = getResponseRequirement(wbo);

        switch (responseRequirement) {
            case NON_ANONYMOUS:
                if (replyToValue != null && replyToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);

                if (faultToValue != null && faultToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
                break;
            case ANONYMOUS:
                if (replyToValue != null && !replyToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, ONLY_ANONYMOUS_ADDRESS_SUPPORTED);

                if (faultToValue != null && !faultToValue.equals(addressingVersion.anonymousUri))
                    throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
                break;
            default:
                // ALL: no check
        }
    }
    */
}
