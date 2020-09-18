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

package com.hh.xml.internal.bind.v2.runtime.property;

import javax.xml.namespace.QName;

import com.hh.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.hh.xml.internal.bind.v2.runtime.Name;

/**
 * Pair of {@link QName} and {@link JaxBeanInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
class TagAndType {
    final Name tagName;
    final JaxBeanInfo beanInfo;
    TagAndType(Name tagName, JaxBeanInfo beanInfo) {
        this.tagName = tagName;
        this.beanInfo = beanInfo;
    }
}
