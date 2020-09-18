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

package com.hh.xml.internal.messaging.saaj.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link ArrayList} with a final marker to help JIT.
 * @author Kohsuke Kawaguchi
 */
public final class FinalArrayList extends ArrayList {
    public FinalArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public FinalArrayList() {
    }

    public FinalArrayList(Collection collection) {
        super(collection);
    }

}
