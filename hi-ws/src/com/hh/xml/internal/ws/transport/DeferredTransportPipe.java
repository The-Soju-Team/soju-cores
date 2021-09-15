/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.ws.transport;

import com.hh.xml.internal.ws.api.EndpointAddress;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.hh.xml.internal.ws.api.pipe.NextAction;
import com.hh.xml.internal.ws.api.pipe.Pipe;
import com.hh.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.pipe.TubeCloner;
import com.hh.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.developer.HttpConfigFeature;

import com.hh.webservice.ws.BindingProvider;
import com.hh.webservice.ws.WebServiceFeature;

/**
 * Proxy transport {@link Tube} and {@link Pipe} that lazily determines the
 * actual transport pipe by looking at {@link Packet#endpointAddress}.
 *
 * <p>
 * This pseudo transport is used when there's no statically known endpoint address,
 * and thus it's expected that the application will configure {@link BindingProvider}
 * at runtime before making invocation.
 *
 * <p>
 * Since a typical application makes multiple invocations with the same endpoint
 * address, this class implements a simple cache strategy to avoid re-creating
 * transport pipes excessively.
 *
 * @author Kohsuke Kawaguchi
 */
public final class DeferredTransportPipe extends AbstractTubeImpl {

    private Tube transport;
    private EndpointAddress address;

    // parameter to TransportPipeFactory
    private final ClassLoader classLoader;
    private final ClientTubeAssemblerContext context;

    public DeferredTransportPipe(ClassLoader classLoader, ClientTubeAssemblerContext context) {
        this.classLoader = classLoader;
        this.context = context;
        if (context.getBinding().getFeature(HttpConfigFeature.class) == null) {
            context.getBinding().getFeatures().mergeFeatures(
                    new WebServiceFeature[] { new HttpConfigFeature() }, false);
        }
        //See if we can create the transport pipe from the available information.
        try {
            this.transport = TransportTubeFactory.create(classLoader, context);
            this.address = context.getAddress();
        } catch(Exception e) {
            //No problem, transport will be initialized while processing the requests
        }
    }

    public DeferredTransportPipe(DeferredTransportPipe that, TubeCloner cloner) {
        super(that,cloner);
        this.classLoader = that.classLoader;
        this.context = that.context;
        if(that.transport!=null) {
            this.transport = cloner.copy(that.transport);
            this.address = that.address;
       }
    }
    public NextAction processException(@NotNull Throwable t) {
        return transport.processException(t);
    }

    public NextAction processRequest(@NotNull Packet request) {
        if(request.endpointAddress==address)
            // cache hit
            return transport.processRequest(request);

        // cache miss

        if(transport!=null) {
            // delete the current entry
            transport.preDestroy();
            transport = null;
            address = null;
        }

        // otherwise find out what transport will process this.

        ClientTubeAssemblerContext newContext = new ClientTubeAssemblerContext(
            request.endpointAddress,
            context.getWsdlModel(),
            context.getBindingProvider(),
            context.getBinding(),
            context.getContainer(),
            context.getCodec().copy(),
            context.getSEIModel()
        );

        address = request.endpointAddress;
        transport = TransportTubeFactory.create(classLoader, newContext);
        // successful return from the above method indicates a successful pipe creation
        assert transport!=null;

        return transport.processRequest(request);
    }

    public NextAction processResponse(@NotNull Packet response) {
        return transport.processResponse(response);
    }

    public void preDestroy() {
        if(transport!=null) {
            transport.preDestroy();
            transport = null;
            address = null;
        }
    }

    public DeferredTransportPipe copy(TubeCloner cloner) {
        return new DeferredTransportPipe(this,cloner);
    }
}
