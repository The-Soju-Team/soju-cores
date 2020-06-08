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

package com.hh.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.WSFeatureList;
import com.hh.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

import com.hh.webservice.ws.Dispatch;
import com.hh.webservice.ws.WebServiceFeature;

/**
 * {@link WSDLObject} that can have features associated with it.
 *
 * <p>
 * {@link WSDLParserExtension}s can add features to this object,
 * which will then be incorporated when {@link Dispatch}s and
 * proxies are created for the port.
 *
 * @author Kohsuke Kawaguchi
 */
public interface WSDLFeaturedObject extends WSDLObject {

    @Nullable
    <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType);

    /**
     * Gets the feature list associated with this object.
     */
    @NotNull WSFeatureList getFeatures();

    /**
     * Enables a {@link WebServiceFeature} based upon policy assertions on this port.
     * This method would be called during WSDL parsing by WS-Policy code.
     */
    void addFeature(@NotNull WebServiceFeature feature);
}
