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

package com.sun.istack.internal.localization;

/**
 * @author WS Development Team
 */
public final class LocalizableMessage implements Localizable {

    private final String _bundlename;
    private final String _key;
    private final Object[] _args;

    public LocalizableMessage(String bundlename, String key, Object... args) {
        _bundlename = bundlename;
        _key = key;
        if(args==null)
            args = new Object[0];
        _args = args;
    }

    public String getKey() {
        return _key;
    }

    public Object[] getArguments() {
        return _args;
    }

    public String getResourceBundleName() {
        return _bundlename;
    }
}
