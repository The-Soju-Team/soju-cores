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

import com.hh.xml.internal.xsom.XSContentType;
import com.hh.xml.internal.xsom.XSParticle;
import com.hh.xml.internal.xsom.XSSimpleType;

/**
 * Visitor that works on {@link XSContentType}.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSContentTypeVisitor {
    void simpleType( XSSimpleType simpleType );
    void particle( XSParticle particle );
    void empty( XSContentType empty );
}
