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

package com.hh.xml.internal.bind.v2.runtime.reflect.opt;

import com.hh.xml.internal.bind.v2.runtime.reflect.Accessor;

/**
 * Template {@link Accessor} for short fields.
 *
 * <p>
 * All the FieldAccessors are generated from <code>FieldAccessor_B y t e</code>
 *
 * @author Kohsuke Kawaguchi
 */
public class FieldAccessor_Short extends Accessor {
    public FieldAccessor_Short() {
        super(Short.class);
    }

    public Object get(Object bean) {
        return ((Bean)bean).f_short;
    }

    public void set(Object bean, Object value) {
        ((Bean)bean).f_short = value==null ? Const.default_value_short : (Short)value;
    }
}
