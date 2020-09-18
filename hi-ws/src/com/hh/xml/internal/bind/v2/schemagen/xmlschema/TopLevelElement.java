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

import javax.xml.namespace.QName;
import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;
import com.hh.xml.internal.txw2.annotation.XmlElement;

@XmlElement("element")
public interface TopLevelElement
    extends Element, TypedXmlWriter
{


    @XmlAttribute("final")
    public TopLevelElement _final(String value);

    @XmlAttribute("final")
    public TopLevelElement _final(String[] value);

    @XmlAttribute("abstract")
    public TopLevelElement _abstract(boolean value);

    @XmlAttribute
    public TopLevelElement substitutionGroup(QName value);

    @XmlAttribute
    public TopLevelElement name(String value);

}
