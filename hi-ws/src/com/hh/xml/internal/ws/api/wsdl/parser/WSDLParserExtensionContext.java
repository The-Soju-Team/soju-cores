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

package com.hh.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.hh.xml.internal.ws.api.server.Container;
import com.hh.xml.internal.ws.api.policy.PolicyResolver;

/**
 * Provides contextual information for {@link WSDLParserExtension}s.
 *
 * @author Vivek Pandey
 * @author Fabian Ritzmann
 */
public interface WSDLParserExtensionContext {
    /**
     * Returns true if the WSDL parsing is happening on the client side. Returns false means
     * its started on the server side.
     */
    boolean isClientSide();

    /**
     * Gives the {@link WSDLModel}. The WSDLModel may not be complete until
     * {@link WSDLParserExtension#finished(WSDLParserExtensionContext)} is called.
     */
    WSDLModel getWSDLModel();

    /**
     * Provides the {@link Container} in which this service or client is running.
     * May return null.
     *
     * @return The container in which this service or client is running.
     */
    @NotNull Container getContainer();


    /**
     * Provides the PolicyResolver
     */
    @NotNull
    PolicyResolver getPolicyResolver();
}
