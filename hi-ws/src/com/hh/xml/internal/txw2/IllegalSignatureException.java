/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.txw2;

/**
 * Signals incorrect method signatures on {@link TypedXmlWriter}-derived interfaces.
 *
 * @author Kohsuke Kawaguchi
 */
public class IllegalSignatureException extends TxwException {
    public IllegalSignatureException(String message) {
        super(message);
    }

    public IllegalSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSignatureException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}
