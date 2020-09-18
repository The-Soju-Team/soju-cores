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

package com.hh.xml.internal.xsom.impl;

import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSXPath;
import com.hh.xml.internal.xsom.XmlString;
import com.hh.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.hh.xml.internal.xsom.visitor.XSFunction;
import com.hh.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

/**
 * @author Kohsuke Kawaguchi
 */
public class XPathImpl extends ComponentImpl implements XSXPath {
    private XSIdentityConstraint parent;
    private final XmlString xpath;

    public XPathImpl(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa, XmlString xpath) {
        super(_owner, _annon, _loc, fa);
        this.xpath = xpath;
    }

    public void setParent(XSIdentityConstraint parent) {
        this.parent = parent;
    }

    public XSIdentityConstraint getParent() {
        return parent;
    }

    public XmlString getXPath() {
        return xpath;
    }

    public void visit(XSVisitor visitor) {
        visitor.xpath(this);
    }

    public <T> T apply(XSFunction<T> function) {
        return function.xpath(this);
    }
}
