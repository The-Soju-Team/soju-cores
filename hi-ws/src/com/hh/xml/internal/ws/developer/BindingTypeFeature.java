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

package com.hh.xml.internal.ws.developer;

import com.hh.xml.internal.ws.api.BindingID;

import com.hh.webservice.ws.WebServiceFeature;

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Using this feature, the application could override the binding used by
 * the runtime(usually determined from WSDL).
 *
 * @author Jitendra Kotamraju
 */
@ManagedData
public final class BindingTypeFeature extends WebServiceFeature {

    public static final String ID = "http://jax-ws.dev.java.net/features/binding";

    private final String bindingId;

    public BindingTypeFeature(String bindingId) {
        this.bindingId = bindingId;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public String getBindingId() {
        return bindingId;
    }

}
