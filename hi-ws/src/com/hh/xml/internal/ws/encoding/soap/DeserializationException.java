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

package com.hh.xml.internal.ws.encoding.soap;

import com.hh.xml.internal.ws.util.exception.JAXWSExceptionBase;
import com.hh.xml.internal.ws.util.localization.Localizable;

/**
 * DeserializationException represents an exception that occurred while
 * deserializing a Java value from XML.
 *
 * @see JAXWSExceptionBase
 *
 * @author WS Development Team
 */
public class DeserializationException extends JAXWSExceptionBase {

    public DeserializationException(String key, Object... args) {
        super(key, args);
    }

    public DeserializationException(Throwable throwable) {
        super(throwable);
    }

    public DeserializationException(Localizable arg) {
        super("nestedDeserializationError", arg);
    }

    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.encoding";
    }
}
