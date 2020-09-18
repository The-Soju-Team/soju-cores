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

package com.hh.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;


interface MIMEConstants {
    // namespace URIs
    public static String NS_WSDL_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";

    // QNames
    public static QName QNAME_CONTENT = new QName(NS_WSDL_MIME, "content");
    public static QName QNAME_MULTIPART_RELATED =
        new QName(NS_WSDL_MIME, "multipartRelated");
    public static QName QNAME_PART = new QName(NS_WSDL_MIME, "part");
    public static QName QNAME_MIME_XML = new QName(NS_WSDL_MIME, "mimeXml");
}
