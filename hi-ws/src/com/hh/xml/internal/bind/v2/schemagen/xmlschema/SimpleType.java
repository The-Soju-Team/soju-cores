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

@XmlElement("simpleType")
public interface SimpleType
    extends Annotated, SimpleDerivation, TypedXmlWriter
{


    @XmlAttribute("final")
    public SimpleType _final(String value);

    @XmlAttribute("final")
    public SimpleType _final(String[] value);

    @XmlAttribute
    public SimpleType name(String value);

}
