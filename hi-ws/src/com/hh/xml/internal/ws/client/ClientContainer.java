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

import com.hh.xml.internal.ws.api.ResourceLoader;
import com.hh.xml.internal.ws.api.server.Container;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Jitendra Kotamraju
 */
final class ClientContainer extends Container {

    private final ResourceLoader loader = new ResourceLoader() {
        public URL getResource(String resource) throws MalformedURLException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            return cl.getResource("META-INF/"+resource);
        }
    };

    public <T> T getSPI(Class<T> spiType) {
        if (spiType == ResourceLoader.class) {
            return spiType.cast(loader);
        }
        return null;
    }

}
