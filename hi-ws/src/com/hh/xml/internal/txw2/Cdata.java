/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.txw2;

/**
 * CDATA section.
 *
 * @author Kohsuke Kawaguchi
 */
final class Cdata extends Text {
    Cdata(Document document, NamespaceResolver nsResolver, Object obj) {
        super(document, nsResolver, obj);
    }

    void accept(ContentVisitor visitor) {
        visitor.onCdata(buffer);
    }
}
