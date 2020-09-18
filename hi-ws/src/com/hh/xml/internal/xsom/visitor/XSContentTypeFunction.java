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
 * Function object that works on {@link XSContentType}.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSContentTypeFunction<T> {
    T simpleType( XSSimpleType simpleType );
    T particle( XSParticle particle );
    T empty( XSContentType empty );
}
