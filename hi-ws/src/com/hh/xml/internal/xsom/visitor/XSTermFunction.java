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
import com.hh.xml.internal.xsom.XSTerm;

/**
 * Function object that works on {@link XSTerm}.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSTermFunction<T> {
    T wildcard( XSWildcard wc );
    T modelGroupDecl( XSModelGroupDecl decl );
    T modelGroup( XSModelGroup group );
    T elementDecl( XSElementDecl decl );
}
