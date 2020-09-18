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

package com.hh.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.api.EndpointAddress;
import com.hh.xml.internal.ws.api.WSService;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.policy.PolicyMap;

import com.hh.webservice.ws.handler.PortInfo;

/**
 * JAX-WS RI's extension to {@link PortInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface WSPortInfo extends PortInfo {
    /**
     * Returns {@link WSService} object that owns this port.
     */
    @NotNull WSService getOwner();

    /**
     * Returns the same information as {@link #getBindingID()}
     * but in a strongly-typed fashion
     */
    @NotNull BindingID getBindingId();

    /**
     * Gets the endpoint address of this port.
     */
    @NotNull EndpointAddress getEndpointAddress();

    /**
     * Gets the {@link WSDLPort} object that represents this port,
     * if {@link WSService} is configured with WSDL. Otherwise null.
     */
    @Nullable WSDLPort getPort();

    /**
     * Gives the PolicMap that captures the Policy for the PortInfo
     *
     * @return PolicyMap
     *
     * @deprecated
     * Do not use this method as the PolicyMap API is not final yet and might change in next few months.
     */

    public PolicyMap getPolicyMap();
}
