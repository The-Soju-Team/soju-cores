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

/**
*
* @author SAAJ RI Development Team
*/
package com.hh.xml.internal.messaging.saaj.soap;

import com.hh.xml.internal.messaging.saaj.soap.impl.CDATAImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.CommentImpl;
import com.hh.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.hh.xml.internal.messaging.saaj.soap.impl.TextImpl;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.w3c.dom.*;

import com.hh.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;

public class SOAPDocumentImpl extends DocumentImpl implements SOAPDocument {

    private static final String XMLNS = "xmlns".intern();
    protected static final Logger log =
        Logger.getLogger(LogDomainConstants.SOAP_DOMAIN,
                         "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");

    SOAPPartImpl enclosingSOAPPart;

    public SOAPDocumentImpl(SOAPPartImpl enclosingDocument) {
        this.enclosingSOAPPart = enclosingDocument;
    }

    //    public SOAPDocumentImpl(boolean grammarAccess) {
    //        super(grammarAccess);
    //    }
    //
    //    public SOAPDocumentImpl(DocumentType doctype) {
    //        super(doctype);
    //    }
    //
    //    public SOAPDocumentImpl(DocumentType doctype, boolean grammarAccess) {
    //        super(doctype, grammarAccess);
    //    }

    public SOAPPartImpl getSOAPPart() {
        if (enclosingSOAPPart == null) {
            log.severe("SAAJ0541.soap.fragment.not.bound.to.part");
            throw new RuntimeException("Could not complete operation. Fragment not bound to SOAP part.");
        }
        return enclosingSOAPPart;
    }

    public SOAPDocumentImpl getDocument() {
        return this;
    }

    public DocumentType getDoctype() {
        // SOAP means no DTD, No DTD means no doctype (SOAP 1.2 only?)
        return null;
    }

    public DOMImplementation getImplementation() {
        return super.getImplementation();
    }

    public Element getDocumentElement() {
        // This had better be an Envelope!
        getSOAPPart().doGetDocumentElement();
        return doGetDocumentElement();
    }

    protected Element doGetDocumentElement() {
        return super.getDocumentElement();
    }

    public Element createElement(String tagName) throws DOMException {
        return ElementFactory.createElement(
            this,
            NameImpl.getLocalNameFromTagName(tagName),
            NameImpl.getPrefixFromTagName(tagName),
            null);
    }

    public DocumentFragment createDocumentFragment() {
        return new SOAPDocumentFragment(this);
    }

    public org.w3c.dom.Text createTextNode(String data) {
        return new TextImpl(this, data);
    }

    public Comment createComment(String data) {
        return new CommentImpl(this, data);
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        return new CDATAImpl(this, data);
    }

    public ProcessingInstruction createProcessingInstruction(
        String target,
        String data)
        throws DOMException {
        log.severe("SAAJ0542.soap.proc.instructions.not.allowed.in.docs");
        throw new UnsupportedOperationException("Processing Instructions are not allowed in SOAP documents");
    }

    public Attr createAttribute(String name) throws DOMException {
        boolean isQualifiedName = (name.indexOf(":") > 0);
        if (isQualifiedName) {
            String nsUri = null;
            String prefix = name.substring(0, name.indexOf(":"));
            //cannot do anything to resolve the URI if prefix is not
            //XMLNS.
            if (XMLNS.equals(prefix)) {
                nsUri = ElementImpl.XMLNS_URI;
                return createAttributeNS(nsUri, name);
            }
        }

        return super.createAttribute(name);
    }

    public EntityReference createEntityReference(String name)
        throws DOMException {
            log.severe("SAAJ0543.soap.entity.refs.not.allowed.in.docs");
            throw new UnsupportedOperationException("Entity References are not allowed in SOAP documents");
    }

    public NodeList getElementsByTagName(String tagname) {
        return super.getElementsByTagName(tagname);
    }

    public org.w3c.dom.Node importNode(Node importedNode, boolean deep)
        throws DOMException {
        return super.importNode(importedNode, deep);
    }

    public Element createElementNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        return ElementFactory.createElement(
            this,
            NameImpl.getLocalNameFromTagName(qualifiedName),
            NameImpl.getPrefixFromTagName(qualifiedName),
            namespaceURI);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        return super.createAttributeNS(namespaceURI, qualifiedName);
    }

    public NodeList getElementsByTagNameNS(
        String namespaceURI,
        String localName) {
        return super.getElementsByTagNameNS(namespaceURI, localName);
    }

    public Element getElementById(String elementId) {
        return super.getElementById(elementId);
    }

    public Node cloneNode(boolean deep) {
        SOAPPartImpl newSoapPart = getSOAPPart().doCloneNode();
        super.cloneNode(newSoapPart.getDocument(), deep);
        return newSoapPart;
    }

    public void cloneNode(SOAPDocumentImpl newdoc, boolean deep) {
        super.cloneNode(newdoc, deep);
    }
}
