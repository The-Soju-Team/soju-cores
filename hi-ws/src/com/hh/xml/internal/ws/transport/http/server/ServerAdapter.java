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
import com.hh.xml.internal.ws.api.server.Module;
import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.api.server.WebModule;
import com.hh.xml.internal.ws.transport.http.HttpAdapter;

import com.hh.webservice.ws.WebServiceException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * {@link HttpAdapter} for Endpoint API.
 *
 * <p>
 * This is a thin wrapper around {@link HttpAdapter}
 * with some description specified in the deployment (in particular those
 * information are related to how a request is routed to a {@link ServerAdapter}.
 *
 * <p>
 * This class implements {@link BoundEndpoint} and represent the
 * server-{@link WSEndpoint} association for Endpoint API's transport
 *
 * @author Jitendra Kotamraju
 */
public final class ServerAdapter extends HttpAdapter implements BoundEndpoint {
    final String name;

    protected ServerAdapter(String name, String urlPattern, WSEndpoint endpoint, ServerAdapterList owner) {
        super(endpoint, owner, urlPattern);
        this.name = name;
        // registers itself with the container
        Module module = endpoint.getContainer().getSPI(Module.class);
        if(module==null)
            LOGGER.warning("Container "+endpoint.getContainer()+" doesn't support "+Module.class);
        else {
            module.getBoundEndpoints().add(this);
        }
    }

    /**
     * Gets the name of the endpoint as given in the <tt>sun-jaxws.xml</tt>
     * deployment descriptor.
     */
    public String getName() {
        return name;
    }


    @NotNull
    public URI getAddress() {
        WebModule webModule = endpoint.getContainer().getSPI(WebModule.class);
        if(webModule==null)
            // this is really a bug in the container implementation
            throw new WebServiceException("Container "+endpoint.getContainer()+" doesn't support "+WebModule.class);

        return getAddress(webModule.getContextPath());
    }

    public @NotNull URI getAddress(String baseAddress) {
        String adrs = baseAddress+getValidPath();
        try {
            return new URI(adrs);
        } catch (URISyntaxException e) {
            // this is really a bug in the container implementation
            throw new WebServiceException("Unable to compute address for "+endpoint,e);
        }
    }

    public void dispose() {
        endpoint.dispose();
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public String toString() {
        return super.toString()+"[name="+name+']';
    }

    private static final Logger LOGGER = Logger.getLogger(ServerAdapter.class.getName());
}
