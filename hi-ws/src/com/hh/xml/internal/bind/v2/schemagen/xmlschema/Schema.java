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

package com.hh.xml.internal.bind.v2.schemagen.xmlschema;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;
import com.hh.xml.internal.txw2.annotation.XmlElement;

@XmlElement("schema")
public interface Schema
    extends SchemaTop, TypedXmlWriter
{


    @XmlElement
    public Annotation annotation();

    @XmlElement("import")
    public Import _import();

    @XmlAttribute
    public Schema targetNamespace(String value);

    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    public Schema lang(String value);

    @XmlAttribute
    public Schema id(String value);

    @XmlAttribute
    public Schema elementFormDefault(String value);

    @XmlAttribute
    public Schema attributeFormDefault(String value);

    @XmlAttribute
    public Schema blockDefault(String value);

    @XmlAttribute
    public Schema blockDefault(String[] value);

    @XmlAttribute
    public Schema finalDefault(String value);

    @XmlAttribute
    public Schema finalDefault(String[] value);

    @XmlAttribute
    public Schema version(String value);

}
