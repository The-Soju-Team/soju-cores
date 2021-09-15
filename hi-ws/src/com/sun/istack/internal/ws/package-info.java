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

/**
 *
 * <P>This document describes the {@link com.sun.mirror.apt.AnnotationProcessor AnnotationProcessor}
 * included with JAX-WS 2.0.1.
 *
 * <p>The {@link com.sun.istack.internal.ws.AnnotationProcessorFactoryImpl AnnoatationnProcessorFactoryImpl} class
 * tells the <a href="http://java.sun.com/j2se/1.5.0/docs/tooldocs/share/apt.html">APT</a>
 * framework that there exists an {@com.sun.mirror.apt.AnnotationProcessor AnnotationProcessor}
 * ({@link com.sun.istak.ws.WSAP WSAP}) for for processing javax.jws.*, javax.jws.soap.*,
 *  and javax.xml.ws.* annotations.
 *
 * @ArchitectureDocument
*/
package com.sun.istack.internal.ws;
