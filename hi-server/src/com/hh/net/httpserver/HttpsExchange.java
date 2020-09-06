/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.net.httpserver;

import javax.net.ssl.SSLSession;

/**
 * This class encapsulates a HTTPS request received and a
 * response to be generated in one exchange and defines
 * the extensions to HttpExchange that are specific to the HTTPS protocol.
 *
 * @since 1.6
 */

public abstract class HttpsExchange extends HttpExchange {

    protected HttpsExchange() {
    }

    /**
     * Get the SSLSession for this exchange.
     *
     * @return the SSLSession
     */
    public abstract SSLSession getSSLSession();
}
