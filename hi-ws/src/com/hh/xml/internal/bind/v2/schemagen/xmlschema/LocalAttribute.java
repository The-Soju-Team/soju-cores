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

@XmlElement("attribute")
public interface LocalAttribute
    extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{


    @XmlAttribute
    public LocalAttribute form(String value);

    @XmlAttribute
    public LocalAttribute name(String value);

    @XmlAttribute
    public LocalAttribute ref(QName value);

    @XmlAttribute
    public LocalAttribute use(String value);

}
