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

package com.hh.xml.internal.ws.handler;

import com.hh.xml.internal.ws.util.exception.JAXWSExceptionBase;
import com.hh.xml.internal.ws.util.localization.Localizable;

/**
 * Exception thrown by handler-related code. Extends
 * {@link com.hh.xml.internal.ws.util.exception.JAXWSExceptionBase}
 * using the appropriate resource bundle.
 *
 * @see com.hh.xml.internal.ws.util.exception.JAXWSExceptionBase
 *
 * @author WS Development Team
 */
public class HandlerException extends JAXWSExceptionBase {
    public HandlerException(String key, Object... args) {
        super(key, args);
    }

    public HandlerException(Throwable throwable) {
        super(throwable);
    }

    public HandlerException(Localizable arg) {
        super("handler.nestedError", arg);
    }

    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.handler";
    }
}
