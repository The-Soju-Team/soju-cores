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

package com.hh.xml.internal.ws.wsdl.writer.document.xsd;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;
import com.hh.xml.internal.txw2.annotation.XmlElement;
import com.hh.xml.internal.ws.wsdl.writer.document.Documented;

/**
 *
 * @author WS Development Team
 */
@XmlElement("import")
public interface Import
    extends TypedXmlWriter, Documented
{


    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.xsd.Import schemaLocation(String value);

    @XmlAttribute
    public com.hh.xml.internal.ws.wsdl.writer.document.xsd.Import namespace(String value);

}
