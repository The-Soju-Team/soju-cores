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

package com.hh.xml.internal.ws.wsdl.writer;

import com.hh.xml.internal.txw2.TypedXmlWriter;
import com.hh.xml.internal.txw2.annotation.XmlAttribute;
import com.hh.xml.internal.txw2.annotation.XmlElement;
import com.hh.xml.internal.ws.addressing.W3CAddressingConstants;
import com.hh.xml.internal.ws.wsdl.writer.document.StartWithExtensionsType;

/**
 * @author Arun Gupta
 */
@XmlElement(value = W3CAddressingConstants.WSA_NAMESPACE_WSDL_NAME,
            ns = W3CAddressingConstants.WSAW_USING_ADDRESSING_NAME)
public interface UsingAddressing extends TypedXmlWriter, StartWithExtensionsType {
    @XmlAttribute(value = "required", ns = "http://schemas.xmlsoap.org/wsdl/")
    public void required(boolean b);
}
