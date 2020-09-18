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

package com.hh.xml.internal.ws.streaming;

import com.hh.xml.internal.ws.util.exception.JAXWSExceptionBase;
import com.hh.xml.internal.ws.util.localization.Localizable;

/**
 * <p> XMLStream ReaderException represents an exception that occurred while reading an
 * XML document. </p>
 *
 * @see JAXWSExceptionBase
 *
 * @author WS Development Team
 */
public class XMLStreamReaderException extends JAXWSExceptionBase {

    public XMLStreamReaderException(String key, Object... args) {
        super(key, args);
    }

    public XMLStreamReaderException(Throwable throwable) {
        super(throwable);
    }

    public XMLStreamReaderException(Localizable arg) {
        super("xmlreader.nestedError", arg);
    }

    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.streaming";
    }
}
