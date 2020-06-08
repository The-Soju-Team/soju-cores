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

package com.hh.xml.internal.xsom.visitor;

import com.hh.xml.internal.xsom.XSAnnotation;
import com.hh.xml.internal.xsom.XSAttGroupDecl;
import com.hh.xml.internal.xsom.XSAttributeDecl;
import com.hh.xml.internal.xsom.XSAttributeUse;
import com.hh.xml.internal.xsom.XSComplexType;
import com.hh.xml.internal.xsom.XSFacet;
import com.hh.xml.internal.xsom.XSNotation;
import com.hh.xml.internal.xsom.XSSchema;
import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSXPath;
import com.hh.xml.internal.xsom.impl.IdentityConstraintImpl;
import com.hh.xml.internal.xsom.impl.XPathImpl;

/**
 * Visitor for {@link com.hh.xml.internal.xsom.XSComponent}
 */
public interface XSVisitor extends XSTermVisitor, XSContentTypeVisitor {
    void annotation( XSAnnotation ann );
    void attGroupDecl( XSAttGroupDecl decl );
    void attributeDecl( XSAttributeDecl decl );
    void attributeUse( XSAttributeUse use );
    void complexType( XSComplexType type );
    void schema( XSSchema schema );
//    void schemaSet( XSSchemaSet schema );
    void facet( XSFacet facet );
    void notation( XSNotation notation );
    void identityConstraint( XSIdentityConstraint decl);
    void xpath(XSXPath xp);
}
