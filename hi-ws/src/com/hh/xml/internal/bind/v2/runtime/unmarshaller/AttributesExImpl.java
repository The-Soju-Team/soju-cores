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

package com.hh.xml.internal.bind.v2.runtime.unmarshaller;

import com.hh.xml.internal.bind.util.AttributesImpl;

/**
 * {@link AttributesEx} implementation.
 *
 * TODO: proper implementation that holds CharSequence
 *
 * @author Kohsuke Kawaguchi
 */
public final class AttributesExImpl extends AttributesImpl implements AttributesEx {
    public CharSequence getData(int idx) {
        return getValue(idx);
    }

    public CharSequence getData(String nsUri, String localName) {
        return getValue(nsUri,localName);
    }
}
