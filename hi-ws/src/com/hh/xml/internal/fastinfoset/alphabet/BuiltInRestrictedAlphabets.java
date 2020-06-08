/*
 * Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
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
 *
 * THIS FILE WAS MODIFIED BY SUN MICROSYSTEMS, INC.
 */

package com.hh.xml.internal.fastinfoset.alphabet;

import com.hh.xml.internal.fastinfoset.EncodingConstants;
import com.hh.xml.internal.org.jvnet.fastinfoset.RestrictedAlphabet;

public final class BuiltInRestrictedAlphabets {
    public final static char[][] table =
            new char[EncodingConstants.RESTRICTED_ALPHABET_BUILTIN_END + 1][];

    static {
        table[RestrictedAlphabet.NUMERIC_CHARACTERS_INDEX] = RestrictedAlphabet.NUMERIC_CHARACTERS.toCharArray();
        table[RestrictedAlphabet.DATE_TIME_CHARACTERS_INDEX] = RestrictedAlphabet.DATE_TIME_CHARACTERS.toCharArray();
    }
}
