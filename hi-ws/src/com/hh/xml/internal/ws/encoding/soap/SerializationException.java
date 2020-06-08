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
 * SerializationException represents an exception that occurred while
 * serializing a Java value as XML.
 *
 * @see JAXWSExceptionBase
 *
 * @author WS Development Team
 */
public class SerializationException extends JAXWSExceptionBase {

    public SerializationException(String key, Object... args) {
        super(key, args);
    }

    public SerializationException(Localizable arg) {
        super("nestedSerializationError", arg);
    }

    public SerializationException(Throwable throwable) {
        super(throwable);
    }

    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.encoding";
    }

}
