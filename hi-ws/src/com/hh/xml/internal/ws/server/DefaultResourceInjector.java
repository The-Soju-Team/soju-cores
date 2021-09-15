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

package com.hh.xml.internal.ws.server;

import com.hh.xml.internal.ws.api.server.ResourceInjector;
import com.hh.xml.internal.ws.api.server.WSWebServiceContext;

import com.hh.webservice.ws.WebServiceContext;

/**
 * Default {@link ResourceInjector}.
 *
 * @see ResourceInjector#STANDALONE
 * @author Kohsuke Kawaguchi
 */
public final class DefaultResourceInjector extends ResourceInjector {
    
    public void inject(WSWebServiceContext context, Object instance) {
        AbstractInstanceResolver.buildInjectionPlan(
            instance.getClass(),WebServiceContext.class,false).inject(instance, context);
    }
}
