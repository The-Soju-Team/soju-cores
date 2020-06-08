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

package com.hh.xml.internal.ws.wsdl.writer.document;

import javax.xml.namespace.QName;
import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;
import com.hh.xml.internal.txw2.annotation.XmlElement;

/**
 *
 * @author WS Development Team
 */
@XmlElement("port")
public interface Port
    extends TypedXmlWriter, Documented
{


    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.Port name(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.Port arrayType(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.Port binding(QName value);

}
