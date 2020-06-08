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

import java.lang.reflect.Type;

import com.hh.xml.internal.bind.v2.model.nav.Navigator;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.hh.xml.internal.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type,Class> implements RuntimeNonElement {
    private RuntimeAnyTypeImpl() {
        super(Navigator.REFLECTION);
    }

    public <V> Transducer<V> getTransducer() {
        return null;
    }

    static final RuntimeNonElement theInstance = new RuntimeAnyTypeImpl();
}
