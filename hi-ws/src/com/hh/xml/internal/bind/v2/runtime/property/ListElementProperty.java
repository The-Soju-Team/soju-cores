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
import com.hh.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.hh.xml.internal.bind.v2.model.core.PropertyKind;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.hh.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.hh.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.hh.xml.internal.bind.v2.runtime.Name;
import com.hh.xml.internal.bind.v2.runtime.Transducer;
import com.hh.xml.internal.bind.v2.runtime.XMLSerializer;
import com.hh.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import com.hh.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.hh.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.hh.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.hh.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;

import org.xml.sax.SAXException;

/**
 * {@link Property} implementation for {@link ElementPropertyInfo} whose
 * {@link ElementPropertyInfo#isValueList()} is true.
 *
 * @author Kohsuke Kawaguchi
 */
final class ListElementProperty<BeanT,ListT,ItemT> extends ArrayProperty<BeanT,ListT,ItemT> {

    private final Name tagName;
    /**
     * Converts all the values to a list and back.
     */
    private final TransducedAccessor<BeanT> xacc;

    public ListElementProperty(JAXBContextImpl grammar, RuntimeElementPropertyInfo prop) {
        super(grammar, prop);

        assert prop.isValueList();
        assert prop.getTypes().size()==1;   // required by the contract of isValueList
        RuntimeTypeRef ref = prop.getTypes().get(0);

        tagName = grammar.nameBuilder.createElementName(ref.getTagName());

        // transducer for each item
        Transducer xducer = ref.getTransducer();
        // transduced accessor for the whole thing
        xacc = new ListTransducedAccessorImpl(xducer,acc,lister);
    }

    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        handlers.put(tagName, new ChildLoader(new LeafPropertyLoader(xacc),null));
    }

    @Override
    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        ListT list = acc.get(o);

        if(list!=null) {
            if(xacc.useNamespace()) {
                w.startElement(tagName,null);
                xacc.declareNamespace(o,w);
                w.endNamespaceDecls(list);
                w.endAttributes();
                xacc.writeText(w,o,fieldName);
                w.endElement();
            } else {
                xacc.writeLeafElement(w, tagName, o, fieldName);
            }
        }
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        if(tagName!=null) {
            if(tagName.equals(nsUri,localName))
                return acc;
        }
        return null;
    }
}
