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

package com.hh.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.hh.xml.internal.ws.api.server.Container;
import com.hh.xml.internal.ws.util.ServiceFinder;
import com.hh.xml.internal.ws.util.pipe.StandaloneTubeAssembler;

import java.util.logging.Logger;

/**
 * Creates {@link TubelineAssembler}.
 * <p/>
 * <p/>
 * To create a tubeline,
 * the JAX-WS runtime locates {@link TubelineAssemblerFactory}s through
 * the <tt>META-INF/services/com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory</tt> files.
 * Factories found are checked to see if it supports the given binding ID one by one,
 * and the first valid {@link TubelineAssembler} returned will be used to create
 * a tubeline.
 *
 * @author Jitendra Kotamraju
 */
public abstract class TubelineAssemblerFactory {
    /**
     * Creates a {@link TubelineAssembler} applicable for the given binding ID.
     *
     * @param bindingId The binding ID for which a tubeline will be created,
     *                  such as {@link javax.xml.ws.soap.SOAPBinding#SOAP11HTTP_BINDING}.
     *                  Must not be null.
     * @return null if this factory doesn't recognize the given binding ID.
     */
    public abstract TubelineAssembler doCreate(BindingID bindingId);

    /**
     * @deprecated
     *      Use {@link #create(ClassLoader, BindingID, Container)}
     */
    public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId) {
        return create(classLoader,bindingId,null);
    }

    /**
     * Locates {@link TubelineAssemblerFactory}s and create
     * a suitable {@link TubelineAssembler}.
     *
     * @param bindingId The binding ID string for which the new {@link TubelineAssembler}
     *                  is created. Must not be null.
     * @param container
     *      if specified, the container is given a chance to specify a {@link TubelineAssembler}
     *      instance. This parameter should be always given on the server, but can be null.
     * @return Always non-null, since we fall back to our default {@link TubelineAssembler}.
     */
    public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId, @Nullable Container container) {

        if(container!=null) {
            // first allow the container to control pipeline for individual endpoint.
            TubelineAssemblerFactory taf = container.getSPI(TubelineAssemblerFactory.class);
            if(taf!=null) {
                TubelineAssembler a = taf.doCreate(bindingId);
                if(a!=null)
                    return a;
            }
        }

        for (TubelineAssemblerFactory factory : ServiceFinder.find(TubelineAssemblerFactory.class, classLoader)) {
            TubelineAssembler assembler = factory.doCreate(bindingId);
            if (assembler != null) {
                TubelineAssemblerFactory.logger.fine(factory.getClass() + " successfully created " + assembler);
                return assembler;
            }
        }

        // See if there is a PipelineAssembler out there and use it for compatibility.
        for (PipelineAssemblerFactory factory : ServiceFinder.find(PipelineAssemblerFactory.class,classLoader)) {
            PipelineAssembler assembler = factory.doCreate(bindingId);
            if(assembler!=null) {
                logger.fine(factory.getClass()+" successfully created "+assembler);
                return new TubelineAssemblerAdapter(assembler);
            }
        }

        // default binding IDs that are known
        return new StandaloneTubeAssembler();
    }

    private static class TubelineAssemblerAdapter implements TubelineAssembler {
        private PipelineAssembler assembler;

        TubelineAssemblerAdapter(PipelineAssembler assembler) {
            this.assembler = assembler;
        }

        public @NotNull Tube createClient(@NotNull ClientTubeAssemblerContext context) {
            ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(
                    context.getAddress(), context.getWsdlModel(), context.getService(),
                    context.getBinding(), context.getContainer());
            return PipeAdapter.adapt(assembler.createClient(ctxt));
        }

        public @NotNull Tube createServer(@NotNull ServerTubeAssemblerContext context) {
            return PipeAdapter.adapt(assembler.createServer((ServerPipeAssemblerContext) context));
        }
    }

    private static final Logger logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());
}
