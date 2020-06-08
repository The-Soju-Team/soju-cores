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

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.NextAction;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.client.HandlerConfiguration;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.soap.SOAPFaultException;
import java.util.HashSet;
import java.util.Set;

/**
 * Performs soap mustUnderstand processing for clients.
 *
 * @author Rama Pulavarthi
 */
public class ClientMUTube extends MUTube {

    public ClientMUTube(WSBinding binding, Tube next) {
        super(binding, next);
    }

    protected ClientMUTube(ClientMUTube that, TubeCloner cloner) {
        super(that,cloner);
    }

    /**
     * Do MU Header Processing on incoming message (response)
     *
     * @return
     *         if all the headers in the packet are understood, returns an action to
     *         call the previous pipes with response packet
     * @throws SOAPFaultException
     *         if all the headers in the packet are not understood, throws SOAPFaultException
     */
    @Override @NotNull
    public NextAction processResponse(Packet response) {
        if (response.getMessage() == null) {
            return super.processResponse(response);
        }
        HandlerConfiguration handlerConfig = response.handlerConfig;

//        Set<QName> knownHeaders;
//        Set<String> roles;

//        if (handlerConfig != null) {
//            knownHeaders = handlerConfig.getKnownHeaders();
//            roles = handlerConfig.getRoles();
//        } else {
//            roles = soapVersion.implicitRoleSet;
//            knownHeaders = new HashSet<QName>();
//        }
//        Set<QName> misUnderstoodHeaders = getMisUnderstoodHeaders(
//                response.getMessage().getHeaders(), roles,
//                knownHeaders);
        if (handlerConfig == null) {
            //Use from binding instead of defaults in case response packet does not have it,
            //may have been changed from the time of invocation, it ok as its only fallback case.
            handlerConfig = binding.getHandlerConfig();
        }
        Set<QName> misUnderstoodHeaders = getMisUnderstoodHeaders(response.getMessage().getHeaders(), handlerConfig.getRoles(),handlerConfig.getHandlerKnownHeaders());
        if((misUnderstoodHeaders == null) || misUnderstoodHeaders.isEmpty()) {
            return super.processResponse(response);
        }
        throw createMUSOAPFaultException(misUnderstoodHeaders);
    }

    public ClientMUTube copy(TubeCloner cloner) {
        return new ClientMUTube(this,cloner);
    }

}
