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

package com.hh.xml.internal.ws.api.policy;

import com.hh.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.hh.xml.internal.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;
import com.hh.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

/**
 *
 * @author Fabian Ritzmann
 */
public class ModelUnmarshaller extends XmlPolicyModelUnmarshaller {

    private static final ModelUnmarshaller INSTANCE = new ModelUnmarshaller();

    /**
     * This private constructor avoids direct instantiation from outside the class.
     */
    private ModelUnmarshaller() {
        super();
    }

    /**
     * Factory method that returns a {@link ModelUnmarshaller} instance.
     *
     * @return {@link PolicyModelGenerator} instance
     */
    public static ModelUnmarshaller getUnmarshaller() {
        return INSTANCE;
    }

    @Override
    protected PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String id, String name) {
        return SourceModel.createSourceModel(nsVersion, id, name);
    }

}
