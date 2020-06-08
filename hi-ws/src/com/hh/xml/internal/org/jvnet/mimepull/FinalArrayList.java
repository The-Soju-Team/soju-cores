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

package com.hh.xml.internal.org.jvnet.mimepull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link java.util.ArrayList} with the final keyword.
 *
 * <p>
 * This gives HotSpot a better hint that all methods can be inlined.
 *
 * @author Kohsuke Kawaguchi
 */
final class FinalArrayList<T> extends ArrayList<T> {
    public FinalArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public FinalArrayList() {
    }

    public FinalArrayList(Collection<? extends T> ts) {
        super(ts);
    }
}
