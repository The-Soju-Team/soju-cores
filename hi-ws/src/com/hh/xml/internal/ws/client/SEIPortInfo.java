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

package com.hh.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.binding.SOAPBindingImpl;
import com.hh.xml.internal.ws.model.SOAPSEIModel;

import com.hh.webservice.ws.WebServiceFeature;


/**
 * {@link PortInfo} that has {@link SEIModel}.
 *
 * This object is created statically when {@link WSServiceDelegate} is created
 * with an service interface.
 *
 * @author Kohsuke Kawaguchi
 */
final class SEIPortInfo extends PortInfo {
    public final Class sei;
    /**
     * Model of {@link #sei}.
     */
    public final SOAPSEIModel model;

    public SEIPortInfo(WSServiceDelegate owner, Class sei, SOAPSEIModel model, @NotNull WSDLPort portModel) {
        super(owner,portModel);
        this.sei = sei;
        this.model = model;
        assert sei!=null && model!=null;
    }

    public BindingImpl createBinding(WebServiceFeature[] webServiceFeatures, Class<?> portInterface) {
         BindingImpl bindingImpl = super.createBinding(webServiceFeatures,portInterface);
         if(bindingImpl instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl)bindingImpl).setPortKnownHeaders(model.getKnownHeaders());
         }
         //Not needed as set above in super.createBinding() call
         //bindingImpl.setFeatures(webServiceFeatures);
         return bindingImpl;
    }
}
