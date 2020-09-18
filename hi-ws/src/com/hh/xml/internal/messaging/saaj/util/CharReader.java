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

package com.hh.xml.internal.messaging.saaj.util;

import java.io.CharArrayReader;

// This class just gives access to the underlying buffer without copying.

public class CharReader extends CharArrayReader {
    public CharReader(char buf[], int length) {
        super(buf, 0, length);
    }

    public CharReader(char buf[], int offset, int length) {
        super(buf, offset, length);
    }

    public char[] getChars() {
        return buf;
    }

    public int getCount() {
        return count;
    }
}
