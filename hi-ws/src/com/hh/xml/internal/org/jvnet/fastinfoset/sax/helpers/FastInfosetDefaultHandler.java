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

package com.hh.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import com.hh.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import com.hh.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Default base class for SAX event handlers of a {@link FastInfosetReader}.
 * <p>
 * This class is available as a convenience for applications: it provides
 * default implementations for all of the callbacks of the following:
 * <UL>
 *   <LI>{@link DefaultHandler}</LI>
 *   <LI>{@link LexicalHandler}</LI>
 *   <LI>{@link EncodingAlgorithmContentHandler}</LI>
 *   <LI>{@link PrimitiveTypeContentHandler}</LI>
 * </UL>
 * Application writers can extend this class when they need to implement only
 * part of an interface; parser writers can instantiate this class to provide
 * default handlers when the application has not supplied its own.
 */
public class FastInfosetDefaultHandler extends DefaultHandler implements
        LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler {

    // LexicalHandler

    public void comment(char[] ch, int start, int length) throws SAXException {
    }

    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }


    // EncodingAlgorithmContentHandler

    public void octets(String URI, int algorithm, byte[] b, int start, int length)  throws SAXException {
    }

    public void object(String URI, int algorithm, Object o)  throws SAXException {
    }


    // PrimitiveTypeContentHandler

    public void booleans(boolean[] b, int start, int length) throws SAXException {
    }

    public void bytes(byte[] b, int start, int length) throws SAXException {
    }

    public void shorts(short[] s, int start, int length) throws SAXException {
    }

    public void ints(int[] i, int start, int length) throws SAXException {
    }

    public void longs(long[] l, int start, int length) throws SAXException {
    }

    public void floats(float[] f, int start, int length) throws SAXException {
    }

    public void doubles(double[] d, int start, int length) throws SAXException {
    }

    public void uuids(long[] msblsb, int start, int length) throws SAXException {
    }
}
