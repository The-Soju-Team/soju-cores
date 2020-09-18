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
 * Template {@link Accessor} for char fields.
 *
 * <p>
 * All the FieldAccessors are generated from <code>FieldAccessor_B y t e</code>
 *
 * @author Kohsuke Kawaguchi
 */
public class FieldAccessor_Character extends Accessor {
    public FieldAccessor_Character() {
        super(Character.class);
    }

    public Object get(Object bean) {
        return ((Bean)bean).f_char;
    }

    public void set(Object bean, Object value) {
        ((Bean)bean).f_char = value==null ? Const.default_value_char : (Character)value;
    }
}
