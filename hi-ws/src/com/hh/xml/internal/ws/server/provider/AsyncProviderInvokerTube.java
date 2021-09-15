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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Fiber;
import com.hh.xml.internal.ws.api.pipe.NextAction;
import com.hh.xml.internal.ws.api.pipe.Tube;
import com.hh.xml.internal.ws.api.server.AsyncProvider;
import com.hh.xml.internal.ws.api.server.AsyncProviderCallback;
import com.hh.xml.internal.ws.api.server.Invoker;
import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.server.AbstractWebServiceContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This {@link Tube} is used to invoke the {@link AsyncProvider} endpoints.
 *
 * @author Jitendra Kotamraju
 */
class AsyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {

    private static final Logger LOGGER = Logger.getLogger(com.hh.xml.internal.ws.util.Constants.LoggingDomain + ".server.AsyncProviderInvokerTube");

    public AsyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }

   /*
    * This binds the parameter for Provider endpoints and invokes the
    * invoke() method of {@linke Provider} endpoint. The return value from
    * invoke() is used to create a new {@link Message} that traverses
    * through the Pipeline to transport.
    */
    public @NotNull NextAction processRequest(@NotNull Packet request) {
        T param = argsBuilder.getParameter(request);
        AsyncProviderCallback callback = new AsyncProviderInvokerTube.AsyncProviderCallbackImpl(request);
        AsyncWebServiceContext ctxt = new AsyncWebServiceContext(getEndpoint(),request);

        AsyncProviderInvokerTube.LOGGER.fine("Invoking AsyncProvider Endpoint");
        try {
            getInvoker(request).invokeAsyncProvider(request, param, callback, ctxt);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return doThrow(e);
        }
        // Suspend the Fiber. AsyncProviderCallback will resume the Fiber after
        // it receives response.
        return doSuspend();
    }

    private class AsyncProviderCallbackImpl implements AsyncProviderCallback<T> {
        private final Packet request;
        private final Fiber fiber;

        public AsyncProviderCallbackImpl(Packet request) {
            this.request = request;
            this.fiber = Fiber.current();
        }

        public void send(@Nullable T param) {
            if (param == null) {
                if (request.transportBackChannel != null) {
                    request.transportBackChannel.close();
                }
            }
            Packet packet = argsBuilder.getResponse(request, param, getEndpoint().getPort(), getEndpoint().getBinding());
            fiber.resume(packet);
        }

        public void sendError(@NotNull Throwable t) {
            Exception e;
            if (t instanceof RuntimeException) {
                e = (RuntimeException)t;
            } else {
                e = new RuntimeException(t);
            }
            Packet packet = argsBuilder.getResponse(request, e, getEndpoint().getPort(), getEndpoint().getBinding());
            fiber.resume(packet);
        }
    }

    /**
     * The single {@link javax.xml.ws.WebServiceContext} instance injected into application.
     */
    private static final class AsyncWebServiceContext extends AbstractWebServiceContext {
        final Packet packet;

        AsyncWebServiceContext(WSEndpoint endpoint, Packet packet) {
            super(endpoint);
            this.packet = packet;
        }

        public @NotNull Packet getRequestPacket() {
            return packet;
        }
    }

    public @NotNull NextAction processResponse(@NotNull Packet response) {
        return doReturnWith(response);
    }

    public @NotNull NextAction processException(@NotNull Throwable t) {
        throw new IllegalStateException("AsyncProviderInvokerTube's processException shouldn't be called.");
    }

}
