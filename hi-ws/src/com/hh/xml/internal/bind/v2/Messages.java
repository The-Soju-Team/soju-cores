/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.bind.v2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
enum Messages {
    ILLEGAL_ENTRY,          // 1 arg
    ERROR_LOADING_CLASS,    // 2 args
    INVALID_PROPERTY_VALUE, // 2 args
    UNSUPPORTED_PROPERTY,   // 1 arg
    BROKEN_CONTEXTPATH,     // 1 arg
    NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS, // 1 arg
    INVALID_TYPE_IN_MAP, // 0args
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
