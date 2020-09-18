/*
 * Copyright (c) 2009, 2010, Oracle and/or its affiliates. All rights reserved.
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

import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.transport.http.HttpAdapterList;

public class ServerAdapterList extends HttpAdapterList<ServerAdapter> {
    @Override
    protected ServerAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
        return new ServerAdapter(name, urlPattern, endpoint, this);
    }
}
