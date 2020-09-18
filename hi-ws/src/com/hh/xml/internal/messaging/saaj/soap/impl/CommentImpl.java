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

package com.hh.xml.internal.messaging.saaj.soap.impl;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.hh.webservice.soap.SOAPElement;
import com.hh.webservice.soap.SOAPException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

import com.hh.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.hh.xml.internal.messaging.saaj.util.LogDomainConstants;

public class CommentImpl
    extends com.sun.org.apache.xerces.internal.dom.CommentImpl
    implements com.hh.webservice.soap.Text, org.w3c.dom.Comment {

    protected static final Logger log =
        Logger.getLogger(LogDomainConstants.SOAP_IMPL_DOMAIN,
                         "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
    protected static ResourceBundle rb =
        log.getResourceBundle();

    public CommentImpl(SOAPDocumentImpl ownerDoc, String text) {
        super(ownerDoc, text);
    }

    public String getValue() {
        String nodeValue = getNodeValue();
        return (nodeValue.equals("") ? null : nodeValue);
    }

    public void setValue(String text) {
        setNodeValue(text);
    }


    public void setParentElement(SOAPElement element) throws SOAPException {
        if (element == null) {
            log.severe("SAAJ0112.impl.no.null.to.parent.elem");
            throw new SOAPException("Cannot pass NULL to setParentElement");
        }
        ((ElementImpl) element).addNode(this);
    }

    public SOAPElement getParentElement() {
        return (SOAPElement) getParentNode();
    }

    public void detachNode() {
        org.w3c.dom.Node parent = getParentNode();
        if (parent != null) {
            parent.removeChild(this);
        }
    }

    public void recycleNode() {
        detachNode();
        // TBD
        //  - add this to the factory so subsequent
        //    creations can reuse this object.
    }

    public boolean isComment() {
        return true;
    }

    public Text splitText(int offset) throws DOMException {
        log.severe("SAAJ0113.impl.cannot.split.text.from.comment");
        throw new UnsupportedOperationException("Cannot split text from a Comment Node.");
    }

    public Text replaceWholeText(String content) throws DOMException {
        log.severe("SAAJ0114.impl.cannot.replace.wholetext.from.comment");
        throw new UnsupportedOperationException("Cannot replace Whole Text from a Comment Node.");
    }

    public String getWholeText() {
        //TODO: maybe we have to implement this in future.
        throw new UnsupportedOperationException("Not Supported");
    }

    public boolean isElementContentWhitespace() {
        //TODO: maybe we have to implement this in future.
        throw new UnsupportedOperationException("Not Supported");
    }

}
