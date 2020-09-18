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


import org.xml.sax.SAXException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


/**
 * Pool of SAXParser objects
 */
public class ParserPool {
    private final BlockingQueue queue;
    private SAXParserFactory factory;
    private int capacity;

    public ParserPool(int capacity) {
        this.capacity = capacity;
        queue = new ArrayBlockingQueue(capacity);
        //factory = SAXParserFactory.newInstance();
        factory = new com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl();
        factory.setNamespaceAware(true);
        for (int i=0; i < capacity; i++) {
           try {
                queue.put(factory.newSAXParser());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            } catch (ParserConfigurationException ex) {
                throw new RuntimeException(ex);
            } catch (SAXException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public SAXParser get() throws ParserConfigurationException,
                SAXException {

        try {
            return (SAXParser) queue.take();
        } catch (InterruptedException ex) {
            throw new SAXException(ex);
        }

    }

    public void put(SAXParser parser) {
        queue.offer(parser);
    }

    public void returnParser(SAXParser saxParser) {
        saxParser.reset();
        resetSaxParser(saxParser);
        put(saxParser);
    }


    /**
     * SAAJ Issue 46 :https://saaj.dev.java.net/issues/show_bug.cgi?id=46
     * Xerces does not provide a way to reset the SymbolTable
     * So we are trying to reset it using the proprietary code below.
     * Temporary Until the bug : https://jaxp.dev.java.net/issues/show_bug.cgi?id=59
     * is fixed.
     * @param parser the parser from the pool whose Symbol Table needs to be reset.
     */
     private void resetSaxParser(SAXParser parser) {
        try {
            //Object obj = parser.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            com.sun.org.apache.xerces.internal.util.SymbolTable table = new com.sun.org.apache.xerces.internal.util.SymbolTable();
            parser.setProperty("http://apache.org/xml/properties/internal/symbol-table", table);
            //obj = parser.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        } catch (SAXNotRecognizedException ex) {
            //nothing to do
        } catch (SAXNotSupportedException ex) {
            //nothing to do
        }
    }

}
