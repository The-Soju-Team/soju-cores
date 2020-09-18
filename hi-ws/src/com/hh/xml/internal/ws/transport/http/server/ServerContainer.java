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

package com.hh.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.server.BoundEndpoint;
import com.hh.xml.internal.ws.api.server.Container;
import com.hh.xml.internal.ws.api.server.Module;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jitendra Kotamraju
 */
class ServerContainer extends Container {

    private final Module module = new Module() {
        private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();

        public @NotNull List<BoundEndpoint> getBoundEndpoints() {
            return endpoints;
        }
    };

    public <T> T getSPI(Class<T> spiType) {
        if (spiType == Module.class) {
            return spiType.cast(module);
        }
        return null;
    }

}
