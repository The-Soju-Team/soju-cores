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

package com.hh.xml.internal.fastinfoset.tools;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import com.hh.xml.internal.fastinfoset.CommonResourceBundle;

public class StAX2SAXReader {

    /**
     * Content handler where events are pushed.
     */
    ContentHandler _handler;

    /**
     * Lexical handler to report lexical events.
     */
    LexicalHandler _lexicalHandler;

    /**
     * XML stream reader where events are pulled.
     */
    XMLStreamReader _reader;

    public StAX2SAXReader(XMLStreamReader reader, ContentHandler handler) {
        _handler = handler;
        _reader = reader;
    }

    public StAX2SAXReader(XMLStreamReader reader) {
        _reader = reader;
    }

    public void setContentHandler(ContentHandler handler) {
        _handler = handler;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        _lexicalHandler = lexicalHandler;
    }

    public void adapt() throws XMLStreamException, SAXException {
        QName qname;
        String prefix, localPart;
        AttributesImpl attrs = new AttributesImpl();
        char[] buffer;
        int nsc;
        int nat;

        _handler.startDocument();

        try {

            while (_reader.hasNext()) {
                int event = _reader.next();


                switch(event) {
                case  XMLStreamConstants.START_ELEMENT: {
                    // Report namespace events first
                    nsc = _reader.getNamespaceCount();
                    for (int i = 0; i < nsc; i++) {
                        _handler.startPrefixMapping(_reader.getNamespacePrefix(i),
                            _reader.getNamespaceURI(i));
                    }

                    // Collect list of attributes
                    attrs.clear();
                    nat = _reader.getAttributeCount();
                    for (int i = 0; i < nat; i++) {
                        QName q = _reader.getAttributeName(i);
                        String qName = _reader.getAttributePrefix(i);
                        if (qName == null || qName == "") {
                            qName = q.getLocalPart();
                        } else {
                            qName = qName + ":" +  q.getLocalPart();
                        }
                        attrs.addAttribute(_reader.getAttributeNamespace(i),
                                           q.getLocalPart(),
                                           qName,
                                           _reader.getAttributeType(i),
                                           _reader.getAttributeValue(i));
                    }

                    // Report start element
                    qname = _reader.getName();
                    prefix = qname.getPrefix();
                    localPart = qname.getLocalPart();

                    _handler.startElement(_reader.getNamespaceURI(),
                                          localPart,
                                          (prefix.length() > 0) ?
                                              (prefix + ":" + localPart) : localPart,
                                          attrs);
                    break;
                }
                case  XMLStreamConstants.END_ELEMENT: {
                    // Report end element
                    qname = _reader.getName();
                    prefix = qname.getPrefix();
                    localPart = qname.getLocalPart();

                    _handler.endElement(_reader.getNamespaceURI(),
                                        localPart,
                                        (prefix.length() > 0) ?
                                            (prefix + ":" + localPart) : localPart);

                    // Report end namespace events
                    nsc = _reader.getNamespaceCount();
                    for (int i = 0; i < nsc; i++) {
                        _handler.endPrefixMapping(_reader.getNamespacePrefix(i));
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                    _handler.characters(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
                    break;
                case XMLStreamConstants.COMMENT:
                    _lexicalHandler.comment(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    _handler.processingInstruction(_reader.getPITarget(), _reader.getPIData());
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    break;
                default:
                    throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[]{Integer.valueOf(event)}));
                } // switch
            }
        }
        catch (XMLStreamException e) {
            _handler.endDocument();     // flush whatever we have
            throw e;
        }

        _handler.endDocument();
    }

}
