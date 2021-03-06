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

package com.hh.xml.internal.ws.wsdl.writer.document.soap;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;

/**
 *
 * @author WS Development Team
 */
public interface BodyType
    extends TypedXmlWriter
{


    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.soap.BodyType encodingStyle(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.soap.BodyType namespace(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.soap.BodyType use(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.soap.BodyType parts(String value);

}
