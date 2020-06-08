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


final class InternalEntity extends EntityDecl {
    InternalEntity(String name, char value []) {
        this.name = name;
        this.buf = value;
    }

    char buf [];
}
