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

package com.hh.xml.internal.xsom.impl;

import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSModelGroup;
import com.hh.xml.internal.xsom.XSModelGroupDecl;
import com.hh.xml.internal.xsom.XSTerm;
import com.hh.xml.internal.xsom.XSWildcard;
import com.hh.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.hh.xml.internal.xsom.visitor.XSFunction;
import com.hh.xml.internal.xsom.visitor.XSTermFunction;
import com.hh.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.hh.xml.internal.xsom.visitor.XSTermVisitor;
import com.hh.xml.internal.xsom.visitor.XSVisitor;
import com.hh.xml.internal.xsom.visitor.XSWildcardFunction;
import com.hh.xml.internal.xsom.visitor.XSWildcardVisitor;
import org.xml.sax.Locator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class WildcardImpl extends ComponentImpl implements XSWildcard, Ref.Term
{
    protected WildcardImpl( SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, int _mode ) {
        super(owner,_annon,_loc,_fa);
        this.mode = _mode;
    }

    private final int mode;
    public int getMode() { return mode; }

    // compute the union
    public WildcardImpl union( SchemaDocumentImpl owner, WildcardImpl rhs ) {
        if(this instanceof Any || rhs instanceof Any)
            return new Any(owner,null,null,null,mode);

        if(this instanceof Finite && rhs instanceof Finite) {
            Set<String> values = new HashSet<String>();
            values.addAll( ((Finite)this).names );
            values.addAll( ((Finite)rhs ).names );
            return new Finite(owner,null,null,null,values,mode);
        }

        if(this instanceof Other && rhs instanceof Other) {
            if( ((Other)this).otherNamespace.equals(
                ((Other)rhs).otherNamespace) )
                return new Other(owner,null,null,null, ((Other)this).otherNamespace, mode );
            else
                // this somewhat strange rule is indeed in the spec
                return new Other(owner,null,null,null, "", mode );
        }

        Other o;
        Finite f;

        if( this instanceof Other ) {
            o=(Other)this; f=(Finite)rhs;
        } else {
            o=(Other)rhs; f=(Finite)this;
        }

        if(f.names.contains(o.otherNamespace))
            return new Any(owner,null,null,null,mode);
        else
            return new Other(owner,null,null,null,o.otherNamespace,mode);
    }



    public final static class Any extends WildcardImpl implements XSWildcard.Any {
        public Any( SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, int _mode ) {
            super(owner,_annon,_loc,_fa,_mode);
        }

        public boolean acceptsNamespace( String namespaceURI ) {
            return true;
        }
        public void visit( XSWildcardVisitor visitor ) {
            visitor.any(this);
        }
        public Object apply( XSWildcardFunction function ) {
            return function.any(this);
        }
    }

    public final static class Other extends WildcardImpl implements XSWildcard.Other {
        public Other( SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa,
                    String otherNamespace, int _mode ) {
            super(owner,_annon,_loc,_fa,_mode);
            this.otherNamespace = otherNamespace;
        }

        private final String otherNamespace;

        public String getOtherNamespace() { return otherNamespace; }

        public boolean acceptsNamespace( String namespaceURI ) {
            return !namespaceURI.equals(otherNamespace);
        }

        public void visit( XSWildcardVisitor visitor ) {
            visitor.other(this);
        }
        public Object apply( XSWildcardFunction function ) {
            return function.other(this);
        }
    }

    public final static class Finite extends WildcardImpl implements XSWildcard.Union {
        public Finite( SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa,
                    Set<String> ns, int _mode ) {
            super(owner,_annon,_loc,_fa,_mode);
            names = ns;
            namesView = Collections.unmodifiableSet(names);
        }

        private final Set<String> names;
        private final Set<String> namesView;

        public Iterator<String> iterateNamespaces() {
            return names.iterator();
        }

        public Collection<String> getNamespaces() {
            return namesView;
        }

        public boolean acceptsNamespace( String namespaceURI ) {
            return names.contains(namespaceURI);
        }

        public void visit( XSWildcardVisitor visitor ) {
            visitor.union(this);
        }
        public Object apply( XSWildcardFunction function ) {
            return function.union(this);
        }
    }

    public final void visit( XSVisitor visitor ) {
        visitor.wildcard(this);
    }
    public final void visit( XSTermVisitor visitor ) {
        visitor.wildcard(this);
    }
    public Object apply( XSTermFunction function ) {
        return function.wildcard(this);
    }

    public <T,P> T apply(XSTermFunctionWithParam<T, P> function, P param) {
        return function.wildcard(this,param);
    }

    public Object apply( XSFunction function ) {
        return function.wildcard(this);
    }


    public boolean isWildcard()                 { return true; }
    public boolean isModelGroupDecl()           { return false; }
    public boolean isModelGroup()               { return false; }
    public boolean isElementDecl()              { return false; }

    public XSWildcard asWildcard()              { return this; }
    public XSModelGroupDecl asModelGroupDecl()  { return null; }
    public XSModelGroup asModelGroup()          { return null; }
    public XSElementDecl asElementDecl()        { return null; }


    // Ref.Term implementation
    public XSTerm getTerm() { return this; }
}
