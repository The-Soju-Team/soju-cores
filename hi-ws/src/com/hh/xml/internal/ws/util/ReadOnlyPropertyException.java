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

package com.hh.xml.internal.ws.util;

import com.hh.xml.internal.ws.api.PropertySet;

/**
 * Used to indicate that {@link PropertySet#put(String, Object)} failed
 * because a property is read-only.
 *
 * @author Kohsuke Kawaguchi
 */
public class ReadOnlyPropertyException extends IllegalArgumentException {
    private final String propertyName;

    public ReadOnlyPropertyException(String propertyName) {
        super(propertyName+" is a read-only property.");
        this.propertyName = propertyName;
    }

    /**
     * Gets the name of the property that was read-only.
     */
    public String getPropertyName() {
        return propertyName;
    }
}
