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

import com.hh.xml.internal.bind.DatatypeConverterImpl;
import com.hh.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.hh.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

/**
 * Template {@link TransducedAccessor} for a long field.
 *
 * <p>
 * All the TransducedAccessor_field are generated from <code>TransducedAccessor_field_B y t e</code>
 *
 * @author Kohsuke Kawaguchi
 *
 * @see TransducedAccessor#get
 */
public final class TransducedAccessor_field_Long extends DefaultTransducedAccessor {
    public String print(Object o) {
        return DatatypeConverterImpl._printLong( ((Bean)o).f_long );
    }

    public void parse(Object o, CharSequence lexical) {
        ((Bean)o).f_long=DatatypeConverterImpl._parseLong(lexical);
    }

    public boolean hasValue(Object o) {
        return true;
    }
}
