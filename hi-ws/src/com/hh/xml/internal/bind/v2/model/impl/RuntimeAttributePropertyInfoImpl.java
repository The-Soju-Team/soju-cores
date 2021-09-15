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
import java.util.List;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;

/**
 * @author Kohsuke Kawaguchi
 */
class RuntimeAttributePropertyInfoImpl extends AttributePropertyInfoImpl<Type,Class,Field,Method>
    implements RuntimeAttributePropertyInfo {

    RuntimeAttributePropertyInfoImpl(RuntimeClassInfoImpl classInfo, PropertySeed<Type,Class,Field,Method> seed) {
        super(classInfo, seed);
    }

    public boolean elementOnlyContent() {
        return true;
    }

    public RuntimeNonElement getTarget() {
        return (RuntimeNonElement) super.getTarget();
    }

    public List<? extends RuntimeNonElement> ref() {
        return (List<? extends RuntimeNonElement>)super.ref();
    }

    public RuntimePropertyInfo getSource() {
        return this;
    }

    public void link() {
        getTransducer();
        super.link();
    }
}
