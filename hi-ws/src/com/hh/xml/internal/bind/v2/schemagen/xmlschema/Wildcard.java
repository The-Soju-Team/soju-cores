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

public interface Wildcard
    extends Annotated, TypedXmlWriter
{


    @XmlAttribute
    public Wildcard processContents(String value);

    @XmlAttribute
    public Wildcard namespace(String[] value);

    @XmlAttribute
    public Wildcard namespace(String value);

}
