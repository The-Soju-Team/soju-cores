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

package com.hh.xml.internal.xsom.util;

import com.hh.xml.internal.xsom.XSAnnotation;
import com.hh.xml.internal.xsom.XSAttGroupDecl;
import com.hh.xml.internal.xsom.XSAttributeDecl;
import com.hh.xml.internal.xsom.XSAttributeUse;
import com.hh.xml.internal.xsom.XSComplexType;
import com.hh.xml.internal.xsom.XSComponent;
import com.hh.xml.internal.xsom.XSContentType;
import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSFacet;
import com.hh.xml.internal.xsom.XSModelGroup;
import com.hh.xml.internal.xsom.XSModelGroupDecl;
import com.hh.xml.internal.xsom.XSNotation;
import com.hh.xml.internal.xsom.XSParticle;
import com.hh.xml.internal.xsom.XSSchema;
import com.hh.xml.internal.xsom.XSSimpleType;
import com.hh.xml.internal.xsom.XSWildcard;
import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSXPath;
import com.hh.xml.internal.xsom.visitor.XSFunction;

/**
 * Utility implementation of {@link XSFunction} that returns
 * {@link Boolean} to find something from schema objects.
 *
 * <p>
 * This implementation returns <code>Boolean.FALSE</code> from
 * all of the methods. The derived class is expected to override
 * some of the methods to actually look for something.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XSFinder implements XSFunction<Boolean> {

    /**
     * Invokes this object as a visitor with the specified component.
     */
    public final boolean find( XSComponent c ) {
        return c.apply(this);
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#annotation(com.sun.xml.internal.xsom.XSAnnotation)
     */
    public Boolean annotation(XSAnnotation ann) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#attGroupDecl(com.sun.xml.internal.xsom.XSAttGroupDecl)
     */
    public Boolean attGroupDecl(XSAttGroupDecl decl) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#attributeDecl(com.sun.xml.internal.xsom.XSAttributeDecl)
     */
    public Boolean attributeDecl(XSAttributeDecl decl) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#attributeUse(com.sun.xml.internal.xsom.XSAttributeUse)
     */
    public Boolean attributeUse(XSAttributeUse use) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#complexType(com.sun.xml.internal.xsom.XSComplexType)
     */
    public Boolean complexType(XSComplexType type) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#schema(com.sun.xml.internal.xsom.XSSchema)
     */
    public Boolean schema(XSSchema schema) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#facet(com.sun.xml.internal.xsom.XSFacet)
     */
    public Boolean facet(XSFacet facet) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSFunction#notation(com.sun.xml.internal.xsom.XSNotation)
     */
    public Boolean notation(XSNotation notation) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSContentTypeFunction#simpleType(com.sun.xml.internal.xsom.XSSimpleType)
     */
    public Boolean simpleType(XSSimpleType simpleType) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSContentTypeFunction#particle(com.sun.xml.internal.xsom.XSParticle)
     */
    public Boolean particle(XSParticle particle) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSContentTypeFunction#empty(com.sun.xml.internal.xsom.XSContentType)
     */
    public Boolean empty(XSContentType empty) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSTermFunction#wildcard(com.sun.xml.internal.xsom.XSWildcard)
     */
    public Boolean wildcard(XSWildcard wc) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSTermFunction#modelGroupDecl(com.sun.xml.internal.xsom.XSModelGroupDecl)
     */
    public Boolean modelGroupDecl(XSModelGroupDecl decl) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSTermFunction#modelGroup(com.sun.xml.internal.xsom.XSModelGroup)
     */
    public Boolean modelGroup(XSModelGroup group) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.internal.xsom.visitor.XSTermFunction#elementDecl(com.sun.xml.internal.xsom.XSElementDecl)
     */
    public Boolean elementDecl(XSElementDecl decl) {
        return Boolean.FALSE;
    }

    public Boolean identityConstraint(XSIdentityConstraint decl) {
        return Boolean.FALSE;
    }

    public Boolean xpath(XSXPath xpath) {
        return Boolean.FALSE;
    }
}
