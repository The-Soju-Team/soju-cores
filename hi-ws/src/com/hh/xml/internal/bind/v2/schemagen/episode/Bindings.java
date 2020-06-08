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

package com.hh.xml.internal.bind.v2.schemagen.episode;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlElement;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;

/**
 * @author Kohsuke Kawaguchi
 */
@XmlElement("bindings")
public interface Bindings extends TypedXmlWriter {
    /**
     * Nested bindings.
     */
    @XmlElement
    Bindings bindings();

    /**
     * Nested class customization.
     */
    @XmlElement("class")
    Klass klass();

    /**
     * Nested typesafeEnumClass customization
     */
    Klass typesafeEnumClass();

    @XmlElement
    SchemaBindings schemaBindings();

    @XmlAttribute
    void scd(String scd);

    @XmlAttribute
    void version(String v);
}
