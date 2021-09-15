/*
 * Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.dtdparser;

/**
 * Base class for entity declarations as used by the parser.
 *
 * @author David Brownell
 * @author Janet Koenig
 * @version 1.3 00/02/24
 */
class EntityDecl {
    String name;        // <!ENTITY name ... >

    boolean isFromInternalSubset;
    boolean isPE;
}
