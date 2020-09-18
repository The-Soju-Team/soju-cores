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

package com.hh.xml.internal.bind.api;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 *
 * @since JAXB2.1.10
 */

enum Messages {
    // TypeReference
    ARGUMENT_CANT_BE_NULL
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

    @Override
    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
