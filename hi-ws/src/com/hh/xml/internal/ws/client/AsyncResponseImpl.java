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

import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.util.CompletedFuture;

import com.hh.webservice.ws.AsyncHandler;
import com.hh.webservice.ws.Response;
import com.hh.webservice.ws.WebServiceException;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * {@link Response} implementation. When Runnbale is executed, it just hands the
 * request to Fiber and returns. When the Fiber finishes the execution, it sets
 * response in the {@link FutureTask}
 *
 * @author Jitendra Kotamraju
 */
public final class AsyncResponseImpl<T> extends FutureTask<T> implements Response<T>, ResponseContextReceiver {

    /**
     * Optional {@link AsyncHandler} that gets invoked
     * at the completion of the task.
     */
    private final AsyncHandler<T> handler;
    private ResponseContext responseContext;
    private final Runnable callable;

    /**
     *
     * @param runnable
     *      This {@link Runnable} is executed asynchronously.
     * @param handler
     *      Optional {@link AsyncHandler} to invoke at the end
     *      of the processing. Can be null.
     */
    public AsyncResponseImpl(Runnable runnable, @Nullable AsyncHandler<T> handler) {
        super(runnable, null);
        this.callable = runnable;
        this.handler = handler;
    }

    @Override
    public void run() {
        // override so that AsyncInvoker calls set()
        // when Fiber calls the callback
        try {
            callable.run();
        } catch (WebServiceException e) {
            //it could be a WebServiceException or a ProtocolException or any RuntimeException
            // resulting due to some internal bug.
            set(null, e);
        } catch (Throwable e) {
            //its some other exception resulting from user error, wrap it in
            // WebServiceException
            set(null, new WebServiceException(e));
        }
    }


    public ResponseContext getContext() {
        return responseContext;
    }

    public void setResponseContext(ResponseContext rc) {
        responseContext = rc;
    }

    public void set(final T v, final Throwable t) {
        // call the handler before we mark the future as 'done'
        if (handler!=null) {
            try {
                /**
                 * {@link Response} object passed into the callback.
                 * We need a separate {@link java.util.concurrent.Future} because we don't want {@link ResponseImpl}
                 * to be marked as 'done' before the callback finishes execution.
                 * (That would provide implicit synchronization between the application code
                 * in the main thread and the callback code, and is compatible with the JAX-RI 2.0 FCS.
                 */
                class CallbackFuture<T> extends CompletedFuture<T> implements Response<T> {
                    public CallbackFuture(T v, Throwable t) {
                        super(v, t);
                    }

                    public Map<String, Object> getContext() {
                        return AsyncResponseImpl.this.getContext();
                    }
                }
                handler.handleResponse(new CallbackFuture<T>(v, t));
            } catch (Throwable e) {
                super.setException(e);
                return;
            }
        }
        if (t != null) {
            super.setException(t);
        } else {
            super.set(v);
        }
    }
}
