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

import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSModelGroup;
import com.hh.xml.internal.xsom.XSModelGroupDecl;
import com.hh.xml.internal.xsom.XSWildcard;

/**
 * Visitor that works on {@link com.hh.xml.internal.xsom.XSTerm}.
 *
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSTermVisitor {
    void wildcard( XSWildcard wc );
    void modelGroupDecl( XSModelGroupDecl decl );
    void modelGroup( XSModelGroup group );
    void elementDecl( XSElementDecl decl );
}
