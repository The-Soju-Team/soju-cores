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

package com.hh.xml.internal.bind.v2.util;

import com.hh.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;

/**
 *
 * @author snajper
 */
public class ClassLoaderRetriever {

    public static ClassLoader getClassLoader() {
        ClassLoader cl = UnmarshallerImpl.class.getClassLoader();
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        return cl;
    }

}
