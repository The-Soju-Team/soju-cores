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
 * Visits {@link Content}.
 *
 * @author Kohsuke Kawaguchi
 */
interface ContentVisitor {
    void onStartDocument();

    void onEndDocument();

    void onEndTag();

    void onPcdata(StringBuilder buffer);

    void onCdata(StringBuilder buffer);

    void onStartTag(String nsUri, String localName, Attribute attributes, NamespaceDecl namespaces);

    void onComment(StringBuilder buffer);
}
