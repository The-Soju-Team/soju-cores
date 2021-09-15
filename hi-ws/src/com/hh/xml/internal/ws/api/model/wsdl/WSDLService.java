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

package com.hh.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;

import javax.xml.namespace.QName;

/**
 * Abstracts wsdl:service.
 *
 * @author Vivek Pandey
 */
public interface WSDLService extends WSDLObject, WSDLExtensible {
    /**
     * Gets the {@link WSDLModel} that owns this service.
     */
    @NotNull
    WSDLModel getParent();

    /**
     * Gets the name of the wsdl:service@name attribute value as local name and wsdl:definitions@targetNamespace
     * as the namespace uri.
     */
    @NotNull
    QName getName();

    /**
     * Gets the {@link WSDLPort} for a given port name
     *
     * @param portName non-null operationName
     * @return null if a {@link WSDLPort} is not found
     */
    WSDLPort get(QName portName);

    /**
     * Gets the first {@link WSDLPort} if any, or otherwise null.
     */
    WSDLPort getFirstPort();

    /**
     * Gives all the {@link WSDLPort} in a wsdl:service {@link WSDLService}
     */
    Iterable<? extends WSDLPort> getPorts();
}
