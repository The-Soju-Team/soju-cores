/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.xml.internal.stream.events ;

import java.util.List;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.events.XMLEvent;
import com.hh.xml.internal.stream.util.ReadOnlyIterator;

/** Implementation of EndElement event.
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 */

public class EndElementEvent extends DummyEvent
implements EndElement {

    List fNamespaces = null;
    QName fQName ;

    public EndElementEvent() {
        init();
    }

    protected void init() {
        setEventType(XMLEvent.END_ELEMENT);
        fNamespaces = new ArrayList();
    }


    public EndElementEvent(String prefix,  String uri, String localpart) {
        this(new QName(uri,localpart,prefix));
    }

    public EndElementEvent(QName qname) {
        this.fQName = qname;
        init();
    }

    public QName getName() {
        return fQName;
    }

    public void setName(QName qname) {
        this.fQName = qname;
    }

    protected void writeAsEncodedUnicodeEx(java.io.Writer writer)
    throws java.io.IOException
    {
        writer.write("</");
        String prefix = fQName.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(':');
     }
        writer.write(fQName.getLocalPart());
        writer.write('>');
    }

    /** Returns an Iterator of namespaces that have gone out
     * of scope.  Returns an empty iterator if no namespaces have gone
     * out of scope.
     * @return an Iterator over Namespace interfaces, or an
     * empty iterator
     */
    public Iterator getNamespaces() {
        if(fNamespaces != null)
            fNamespaces.iterator();
        return new ReadOnlyIterator();
    }

    public void addNamespace(Namespace attr){
        if(attr != null){
            fNamespaces.add(attr);
        }
    }

    public String toString() {
        String s = "</" + nameAsString();
        s = s + ">";
        return s;
    }

    public String nameAsString() {
        if("".equals(fQName.getNamespaceURI()))
            return fQName.getLocalPart();
        if(fQName.getPrefix() != null)
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getPrefix() + ":" + fQName.getLocalPart();
        else
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getLocalPart();
    }

}
