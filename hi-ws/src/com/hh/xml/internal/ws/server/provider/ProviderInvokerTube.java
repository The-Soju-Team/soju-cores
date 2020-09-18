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

package com.hh.xml.internal.ws.server.provider;

import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.server.AsyncProvider;
import com.hh.xml.internal.ws.api.server.Invoker;
import com.hh.xml.internal.ws.binding.SOAPBindingImpl;
import com.hh.xml.internal.ws.server.InvokerTube;

import com.hh.webservice.ws.Provider;

/**
 * This {@link Tube} is used to invoke the {@link Provider} and {@link AsyncProvider} endpoints.
 *
 * @author Jitendra Kotamraju
 */
public abstract class ProviderInvokerTube<T> extends InvokerTube<Provider<T>> {

    protected ProviderArgumentsBuilder<T> argsBuilder;

    /*package*/ ProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker);
        this.argsBuilder = argsBuilder;
    }

    public static <T> ProviderInvokerTube<T>
    create(Class<T> implType, WSBinding binding, Invoker invoker) {

        ProviderEndpointModel<T> model = new ProviderEndpointModel<T>(implType, binding);
        ProviderArgumentsBuilder<?> argsBuilder = ProviderArgumentsBuilder.create(model, binding);
        if (binding instanceof SOAPBindingImpl) {
            //set portKnownHeaders on Binding, so that they can be used for MU processing
            ((SOAPBindingImpl) binding).setMode(model.mode);
        }

        return model.isAsync ? new AsyncProviderInvokerTube(invoker, argsBuilder)
            : new SyncProviderInvokerTube(invoker, argsBuilder);
    }
}
