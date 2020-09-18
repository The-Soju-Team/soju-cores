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

package com.hh.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.message.Packet;

import com.hh.webservice.ws.WebServiceContext;

/**
 * {@link WebServiceContext} that exposes JAX-WS RI specific additions.
 *
 * <p>
 * {@link WebServiceContext} instances that JAX-WS injects always
 * implement this interface.
 *
 * <p>
 * The JAX-WS RI may add methods on this interface, so do not implement
 * this interface in your code, or risk {@link LinkageError}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface WSWebServiceContext extends WebServiceContext {
    /**
     * Obtains the request packet that is being processed.
     * @return Packet for the request
     */
    @Nullable Packet getRequestPacket();
}
