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

package com.hh.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.PropertySet;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.handler.MessageContext;

/**
 * Properties exposed from {@link WSDLPort} for {@link MessageContext}.
 * Donot add this satellite if {@link WSDLPort} is null.
 *
 * @author Jitendra Kotamraju
 */
public final class WSDLProperties extends PropertySet {

    private static final PropertyMap model;
    static {
        model = parse(WSDLProperties.class);
    }

    private final @NotNull WSDLPort port;

    public WSDLProperties(@NotNull WSDLPort port) {
        this.port = port;
    }

    @Property(MessageContext.WSDL_SERVICE)
    public QName getWSDLService() {
        return port.getOwner().getName();
    }

    @Property(MessageContext.WSDL_PORT)
    public QName getWSDLPort() {
        return port.getName();
    }

    @Property(MessageContext.WSDL_INTERFACE)
    public QName getWSDLPortType() {
        return port.getBinding().getPortTypeName();
    }

    @Override
    protected PropertyMap getPropertyMap() {
        return model;
    }

}
