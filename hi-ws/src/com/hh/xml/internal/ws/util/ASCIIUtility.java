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

package com.hh.xml.internal.ws.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Copied from mail.jar.
 */
public class ASCIIUtility {
    // Private constructor so that this class is not instantiated
    private ASCIIUtility() { }


    /**
     * Convert the bytes within the specified range of the given byte
     * array into a signed integer in the given radix . The range extends
     * from <code>start</code> till, but not including <code>end</code>. <p>
     *
     * Based on java.lang.Integer.parseInt()
     */
    public static int parseInt(byte[] b, int start, int end, int radix)
        throws NumberFormatException {
        if (b == null)
            throw new NumberFormatException("null");

        int result = 0;
        boolean negative = false;
        int i = start;
        int limit;
        int multmin;
        int digit;

        if (end > start) {
            if (b[i] == '-') {
                negative = true;
                limit = Integer.MIN_VALUE;
                i++;
            } else {
                limit = -Integer.MAX_VALUE;
            }
            multmin = limit / radix;
            if (i < end) {
                digit = Character.digit((char)b[i++], radix);
                if (digit < 0) {
                    throw new NumberFormatException(
                    "illegal number: " + toString(b, start, end)
                    );
                } else {
                    result = -digit;
                }
            }
            while (i < end) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit((char)b[i++], radix);
                if (digit < 0) {
                    throw new NumberFormatException("illegal number");
                }
                if (result < multmin) {
                    throw new NumberFormatException("illegal number");
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException("illegal number");
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("illegal number");
        }
        if (negative) {
            if (i > start + 1) {
                return result;
            } else {    /* Only got "-" */
                throw new NumberFormatException("illegal number");
            }
        } else {
            return -result;
        }
    }

    /**
     * Convert the bytes within the specified range of the given byte
     * array into a String. The range extends from <code>start</code>
     * till, but not including <code>end</code>. <p>
     */
    public static String toString(byte[] b, int start, int end) {
        int size = end - start;
        char[] theChars = new char[size];

        for (int i = 0, j = start; i < size; )
            theChars[i++] = (char)(b[j++]&0xff);

        return new String(theChars);
    }

    public static byte[] getBytes(String s) {
        char [] chars= s.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size;)
            bytes[i] = (byte) chars[i++];
        return bytes;
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayBuffer bab = new ByteArrayBuffer();
        bab.write(is);
        return bab.toByteArray();
    }

    public static void copyStream(InputStream is, OutputStream out) throws IOException {
        int size = 1024;
        byte[] buf = new byte[size];
        int len;

        while ((len = is.read(buf, 0, size)) != -1)
            out.write(buf, 0, len);
    }
}
