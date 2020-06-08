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

package com.hh.xml.internal.bind.v2.model.runtime;

import java.lang.reflect.Type;

import com.hh.xml.internal.bind.v2.model.core.ArrayInfo;
import com.hh.xml.internal.bind.v2.model.core.TypeInfo;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeArrayInfo extends ArrayInfo<Type,Class>, RuntimeNonElement {
    /**
     * Represents <tt>T[]</tt>.
     *
     * The same as {@link TypeInfo#getType()} but at the runtime, an array
     * is guaranteed to have a {@link Class} representation, not just any {@link Type}.
     */
    Class getType();

    /**
     * {@inheritDoc}
     */
    RuntimeNonElement getItemType();
}
