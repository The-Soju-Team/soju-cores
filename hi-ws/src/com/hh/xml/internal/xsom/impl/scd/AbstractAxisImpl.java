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

package com.hh.xml.internal.xsom.impl.scd;

import com.hh.xml.internal.xsom.XSAnnotation;
import com.hh.xml.internal.xsom.XSAttGroupDecl;
import com.hh.xml.internal.xsom.XSAttributeDecl;
import com.hh.xml.internal.xsom.XSAttributeUse;
import com.hh.xml.internal.xsom.XSComplexType;
import com.hh.xml.internal.xsom.XSComponent;
import com.hh.xml.internal.xsom.XSContentType;
import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSFacet;
import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSModelGroup;
import com.hh.xml.internal.xsom.XSModelGroupDecl;
import com.hh.xml.internal.xsom.XSNotation;
import com.hh.xml.internal.xsom.XSParticle;
import com.hh.xml.internal.xsom.XSSchema;
import com.hh.xml.internal.xsom.XSSimpleType;
import com.hh.xml.internal.xsom.XSWildcard;
import com.hh.xml.internal.xsom.XSXPath;
import com.hh.xml.internal.xsom.visitor.XSFunction;

import java.util.Iterator;

/**
 * Partial default implementation of {@link Axis}.
 *
 * <p>
 * {@link XSParticle}s are skipped in SCD, so this class compensates that.
 * For example, when we are considering a path from {@link XSComplexType},
 * we need to also consider a path from its content type particle (if any.)
 *
 * @author Kohsuke Kawaguchi
 */
abstract class AbstractAxisImpl<T extends XSComponent> implements Axis<T>, XSFunction<Iterator<T>> {
    /**
     * Creates a singleton list.
     */
    protected final Iterator<T> singleton(T t) {
        return Iterators.singleton(t);
    }

    protected final Iterator<T> union(T... items) {
        return new Iterators.Array<T>(items);
    }

    protected final Iterator<T> union(Iterator<? extends T> first, Iterator<? extends T> second) {
        return new Iterators.Union<T>(first,second);
    }

    public Iterator<T> iterator(XSComponent contextNode) {
        return contextNode.apply(this);
    }

    /**
     * Gets the prefix of the axis, like "foo::".
     */
    public String getName() {
        return toString();
    }

    /**
     * Default implementation that simply delegate sto {@link #iterator(XSComponent)}
     */
    public Iterator<T> iterator(Iterator<? extends XSComponent> contextNodes) {
        return new Iterators.Map<T,XSComponent>(contextNodes) {
            protected Iterator<? extends T> apply(XSComponent u) {
                return iterator(u);
            }
        };
    }

    public boolean isModelGroup() {
        return false;
    }

    public Iterator<T> annotation(XSAnnotation ann) {
        return empty();
    }

    public Iterator<T> attGroupDecl(XSAttGroupDecl decl) {
        return empty();
    }

    public Iterator<T> attributeDecl(XSAttributeDecl decl) {
        return empty();
    }

    public Iterator<T> attributeUse(XSAttributeUse use) {
        return empty();
    }

    public Iterator<T> complexType(XSComplexType type) {
        // compensate particle
        XSParticle p = type.getContentType().asParticle();
        if(p!=null)
            return particle(p);
        else
            return empty();
    }

    public Iterator<T> schema(XSSchema schema) {
        return empty();
    }

    public Iterator<T> facet(XSFacet facet) {
        return empty();
    }

    public Iterator<T> notation(XSNotation notation) {
        return empty();
    }

    public Iterator<T> identityConstraint(XSIdentityConstraint decl) {
        return empty();
    }

    public Iterator<T> xpath(XSXPath xpath) {
        return empty();
    }

    public Iterator<T> simpleType(XSSimpleType simpleType) {
        return empty();
    }

    public Iterator<T> particle(XSParticle particle) {
        return empty();
    }

    public Iterator<T> empty(XSContentType empty) {
        return empty();
    }

    public Iterator<T> wildcard(XSWildcard wc) {
        return empty();
    }

    public Iterator<T> modelGroupDecl(XSModelGroupDecl decl) {
        return empty();
    }

    public Iterator<T> modelGroup(XSModelGroup group) {
        // compensate for particles that are ignored in SCD
        return new Iterators.Map<T,XSParticle>(group.iterator()) {
            protected Iterator<? extends T> apply(XSParticle p) {
                return particle(p);
            }
        };
    }

    public Iterator<T> elementDecl(XSElementDecl decl) {
        return empty();
    }

    /**
     * Returns an empty list.
     */
    protected final Iterator<T> empty() {
        return Iterators.empty();
    }

}
