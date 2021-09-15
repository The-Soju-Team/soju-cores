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

import com.hh.xml.internal.xsom.XSWildcard;

/**
 * Visits three kinds of {@link XSWildcard}.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSWildcardVisitor {
    void any( XSWildcard.Any wc );
    void other( XSWildcard.Other wc );
    void union( XSWildcard.Union wc );
}
