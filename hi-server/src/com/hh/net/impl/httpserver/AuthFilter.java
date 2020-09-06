/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.net.impl.httpserver;

import com.hh.net.httpserver.Authenticator;
import com.hh.net.httpserver.Filter;
import com.hh.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

public class AuthFilter extends Filter {

    private Authenticator authenticator;

    public AuthFilter(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public String description() {
        return "Authentication filter";
    }

    public void setAuthenticator(Authenticator a) {
        authenticator = a;
    }

    public void consumeInput(HttpExchange t) throws IOException {
        try (InputStream i = t.getRequestBody();) {
            byte[] b = new byte[4096];
            while (i.read(b) != -1) ;
            i.close();
        }
    }

    /**
     * The filter's implementation, which is invoked by the server
     */
    public void doFilter(HttpExchange t, Filter.Chain chain) throws IOException {
        if (authenticator != null) {
            Authenticator.Result r = authenticator.authenticate(t);
            if (r instanceof Authenticator.Success) {
                Authenticator.Success s = (Authenticator.Success) r;
                ExchangeImpl e = ExchangeImpl.get(t);
                e.setPrincipal(s.getPrincipal());
                chain.doFilter(t);
            } else if (r instanceof Authenticator.Retry) {
                Authenticator.Retry ry = (Authenticator.Retry) r;
                consumeInput(t);
                t.sendResponseHeaders(ry.getResponseCode(), -1);
            } else if (r instanceof Authenticator.Failure) {
                Authenticator.Failure f = (Authenticator.Failure) r;
                consumeInput(t);
                t.sendResponseHeaders(f.getResponseCode(), -1);
            }
        } else {
            chain.doFilter(t);
        }
    }
}
