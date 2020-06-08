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

package com.hh.xml.internal.bind.v2.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.bind.api.Bridge;
import com.hh.xml.internal.bind.api.TypeReference;
import com.hh.xml.internal.bind.marshaller.SAX2DOMEx;
import com.hh.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.hh.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import com.hh.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * {@link Bridge} implementaiton.
 *
 * @author Kohsuke Kawaguchi
 */
final class BridgeImpl<T> extends InternalBridge<T> {

    /**
     * Tag name associated with this {@link Bridge}.
     * Used for marshalling.
     */
    private final Name tagName;
    private final JaxBeanInfo<T> bi;
    private final TypeReference typeRef;

    public BridgeImpl(JAXBContextImpl context, Name tagName, JaxBeanInfo<T> bi,TypeReference typeRef) {
        super(context);
        this.tagName = tagName;
        this.bi = bi;
        this.typeRef = typeRef;
    }

    public void marshal(Marshaller _m, T t, XMLStreamWriter output) throws JAXBException {
        MarshallerImpl m = (MarshallerImpl)_m;
        m.write(tagName,bi,t,XMLStreamWriterOutput.create(output,context),new StAXPostInitAction(output,m.serializer));
    }

    public void marshal(Marshaller _m, T t, OutputStream output, NamespaceContext nsContext) throws JAXBException {
        MarshallerImpl m = (MarshallerImpl)_m;

        Runnable pia = null;
        if(nsContext!=null)
            pia = new StAXPostInitAction(nsContext,m.serializer);

        m.write(tagName,bi,t,m.createWriter(output),pia);
    }

    public void marshal(Marshaller _m, T t, Node output) throws JAXBException {
        MarshallerImpl m = (MarshallerImpl)_m;
        m.write(tagName,bi,t,new SAXOutput(new SAX2DOMEx(output)),new DomPostInitAction(output,m.serializer));
    }

    public void marshal(Marshaller _m, T t, ContentHandler contentHandler) throws JAXBException {
        MarshallerImpl m = (MarshallerImpl)_m;
        m.write(tagName,bi,t,new SAXOutput(contentHandler),null);
    }

    public void marshal(Marshaller _m, T t, Result result) throws JAXBException {
        MarshallerImpl m = (MarshallerImpl)_m;
        m.write(tagName,bi,t, m.createXmlOutput(result),m.createPostInitAction(result));
    }

    public @NotNull T unmarshal(Unmarshaller _u, XMLStreamReader in) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement<T>)u.unmarshal0(in,bi)).getValue();
    }

    public @NotNull T unmarshal(Unmarshaller _u, Source in) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement<T>)u.unmarshal0(in,bi)).getValue();
    }

    public @NotNull T unmarshal(Unmarshaller _u, InputStream in) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement<T>)u.unmarshal0(in,bi)).getValue();
    }

    public @NotNull T unmarshal(Unmarshaller _u, Node n) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl)_u;
        return ((JAXBElement<T>)u.unmarshal0(n,bi)).getValue();
    }

    public TypeReference getTypeReference() {
        return typeRef;
    }

    public void marshal(T value, XMLSerializer out) throws IOException, SAXException, XMLStreamException {
        out.startElement(tagName,null);
        if(value==null) {
            out.writeXsiNilTrue();
        } else {
            out.childAsXsiType(value,null,bi,false);
        }
        out.endElement();
    }

}
