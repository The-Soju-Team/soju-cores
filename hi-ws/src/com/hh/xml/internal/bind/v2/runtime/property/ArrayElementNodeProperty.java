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

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.hh.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.hh.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.hh.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.hh.xml.internal.bind.v2.runtime.XMLSerializer;

import org.xml.sax.SAXException;

/**
 * {@link ArrayProperty} that contains node values.
 *
 * @author Kohsuke Kawaguchi
 */
final class ArrayElementNodeProperty<BeanT,ListT,ItemT> extends ArrayElementProperty<BeanT,ListT,ItemT> {

    public ArrayElementNodeProperty(JAXBContextImpl p, RuntimeElementPropertyInfo prop) {
        super(p, prop);
    }

    public void serializeItem(JaxBeanInfo expected, ItemT item, XMLSerializer w) throws SAXException, IOException, XMLStreamException {
        if(item==null) {
            w.writeXsiNilTrue();
        } else {
            w.childAsXsiType(item,fieldName,expected, false);
        }
    }
}
