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

package com.hh.xml.internal.xsom.impl;

import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSXPath;
import com.hh.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.hh.xml.internal.xsom.visitor.XSFunction;
import com.hh.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

import java.util.Collections;
import java.util.List;

/**
 * {@link XSIdentityConstraint} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public class IdentityConstraintImpl extends ComponentImpl implements XSIdentityConstraint, Ref.IdentityConstraint {

    private XSElementDecl parent;
    private final short category;
    private final String name;
    private final XSXPath selector;
    private final List<XSXPath> fields;
    private final Ref.IdentityConstraint refer;

    public IdentityConstraintImpl(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc,
        ForeignAttributesImpl fa, short category, String name, XPathImpl selector,
        List<XPathImpl> fields, Ref.IdentityConstraint refer) {

        super(_owner, _annon, _loc, fa);
        this.category = category;
        this.name = name;
        this.selector = selector;
        selector.setParent(this);
        this.fields = Collections.unmodifiableList((List)fields);
        for( XPathImpl xp : fields )
            xp.setParent(this);
        this.refer = refer;
    }


    public void visit(XSVisitor visitor) {
        visitor.identityConstraint(this);
    }

    public <T> T apply(XSFunction<T> function) {
        return function.identityConstraint(this);
    }

    public void setParent(ElementDecl parent) {
        this.parent = parent;
        parent.getOwnerSchema().addIdentityConstraint(this);
    }

    public XSElementDecl getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getTargetNamespace() {
        return getParent().getTargetNamespace();
    }

    public short getCategory() {
        return category;
    }

    public XSXPath getSelector() {
        return selector;
    }

    public List<XSXPath> getFields() {
        return fields;
    }

    public XSIdentityConstraint getReferencedKey() {
        if(category==KEYREF)
            return refer.get();
        else
            throw new IllegalStateException("not a keyref");
    }

    public XSIdentityConstraint get() {
        return this;
    }
}
