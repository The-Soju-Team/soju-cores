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

package com.hh.xml.internal.bind.v2.model.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.hh.xml.internal.bind.v2.model.annotation.Locatable;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.hh.xml.internal.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeArrayInfoImpl extends ArrayInfoImpl<Type,Class,Field,Method> implements RuntimeArrayInfo {
    RuntimeArrayInfoImpl(RuntimeModelBuilder builder, Locatable upstream, Class arrayType) {
        super(builder, upstream, arrayType);
    }

    public Class getType() {
        return (Class)super.getType();
    }

    public RuntimeNonElement getItemType() {
        return (RuntimeNonElement)super.getItemType();
    }

    public <V> Transducer<V> getTransducer() {
        return null;
    }
}
