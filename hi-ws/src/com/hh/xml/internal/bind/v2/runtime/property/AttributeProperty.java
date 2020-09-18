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

import com.hh.xml.internal.bind.api.AccessorException;
import com.hh.xml.internal.bind.v2.util.QNameMap;
import com.hh.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.hh.xml.internal.bind.v2.model.core.PropertyKind;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.hh.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.hh.xml.internal.bind.v2.runtime.Name;
import com.hh.xml.internal.bind.v2.runtime.XMLSerializer;
import com.hh.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.hh.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.hh.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.hh.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;

import org.xml.sax.SAXException;

/**
 * {@link Property} implementation for {@link AttributePropertyInfo}.
 *
 * <p>
 * This one works for both leaves and nodes, scalars and arrays.
 *
 * <p>
 * Implements {@link Comparable} so that it can be sorted lexicographically.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public final class AttributeProperty<BeanT> extends PropertyImpl<BeanT>
    implements Comparable<AttributeProperty> {

    /**
     * Attribute name.
     */
    public final Name attName;

    /**
     * Heart of the conversion logic.
     */
    public final TransducedAccessor<BeanT> xacc;

    private final Accessor acc;

    public AttributeProperty(JAXBContextImpl context, RuntimeAttributePropertyInfo prop) {
        super(context,prop);
        this.attName = context.nameBuilder.createAttributeName(prop.getXmlName());
        this.xacc = TransducedAccessor.get(context,prop);
        this.acc = prop.getAccessor();   // we only use this for binder, so don't waste memory by optimizing
    }

    /**
     * Marshals one attribute.
     *
     * @see JaxBeanInfo#serializeAttributes(Object, XMLSerializer)
     */
    public void serializeAttributes(BeanT o, XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
        CharSequence value = xacc.print(o);
        if(value!=null)
            w.attribute(attName,value.toString());
    }

    public void serializeURIs(BeanT o, XMLSerializer w) throws AccessorException, SAXException {
        xacc.declareNamespace(o,w);
    }

    public boolean hasSerializeURIAction() {
        return xacc.useNamespace();
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chainElem, QNameMap<ChildLoader> handlers) {
        throw new IllegalStateException();
    }


    public PropertyKind getKind() {
        return PropertyKind.ATTRIBUTE;
    }

    public void reset(BeanT o) throws AccessorException {
        acc.set(o,null);
    }

    public String getIdValue(BeanT bean) throws AccessorException, SAXException {
        return xacc.print(bean).toString();
    }

    public int compareTo(AttributeProperty that) {
        return this.attName.compareTo(that.attName);
    }
}
