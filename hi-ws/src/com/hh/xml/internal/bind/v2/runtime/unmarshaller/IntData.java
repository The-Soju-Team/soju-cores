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

package com.hh.xml.internal.bind.v2.runtime.unmarshaller;

import java.io.IOException;

import com.hh.xml.internal.bind.v2.runtime.output.Pcdata;
import com.hh.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;

/**
 * {@link Pcdata} that represents a single integer.
 *
 * @author Kohsuke Kawaguchi
 */
public class IntData extends Pcdata {
    /**
     * The int value that this {@link Pcdata} represents.
     *
     * Modifiable.
     */
    private int data;

    /**
     * Length of the {@link #data} in ASCII string.
     * For example if data=-10, then length=3
     */
    private int length;

    public void reset(int i) {
        this.data = i;
        if(i==Integer.MIN_VALUE)
            length = 11;
        else
            length = (i < 0) ? stringSizeOfInt(-i) + 1 : stringSizeOfInt(i);
    }

    private final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
                                     99999999, 999999999, Integer.MAX_VALUE };

    // Requires positive x
    private static int stringSizeOfInt(int x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i])
                return i+1;
    }

    public String toString() {
        return String.valueOf(data);
    }


    public int length() {
        return length;
    }

    public char charAt(int index) {
        return toString().charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return toString().substring(start,end);
    }

    public void writeTo(UTF8XmlOutput output) throws IOException {
        output.text(data);
    }
}
