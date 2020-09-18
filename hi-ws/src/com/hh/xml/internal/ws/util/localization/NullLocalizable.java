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
 * {@link Localizable} that wraps a non-localizable string.
 *
 * @author WS Development Team
 */
public final class NullLocalizable implements Localizable {
    private final String msg;

    public NullLocalizable(String msg) {
        if(msg==null)
            throw new IllegalArgumentException();
        this.msg = msg;
    }

    public String getKey() {
        return Localizable.NOT_LOCALIZABLE;
    }
    public Object[] getArguments() {
        return new Object[]{msg};
    }
    public String getResourceBundleName() {
        return "";
    }
}
