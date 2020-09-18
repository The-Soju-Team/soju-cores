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

package com.hh.xml.internal.ws.util.localization;

/**
 * Straight-forward {@link Localizable} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public final class LocalizableImpl implements Localizable {
    private final String key;
    private final Object[] arguments;
    private final String resourceBundleName;

    public LocalizableImpl(String key, Object[] arguments, String resourceBundleName) {
        this.key = key;
        this.arguments = arguments;
        this.resourceBundleName = resourceBundleName;
    }

    public String getKey() {
        return key;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getResourceBundleName() {
        return resourceBundleName;
    }
}
