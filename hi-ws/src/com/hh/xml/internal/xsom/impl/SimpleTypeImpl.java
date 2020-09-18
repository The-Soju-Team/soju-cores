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

import com.hh.xml.internal.xsom.XSComplexType;
import com.hh.xml.internal.xsom.XSContentType;
import com.hh.xml.internal.xsom.XSListSimpleType;
import com.hh.xml.internal.xsom.XSParticle;
import com.hh.xml.internal.xsom.XSRestrictionSimpleType;
import com.hh.xml.internal.xsom.XSSimpleType;
import com.hh.xml.internal.xsom.XSType;
import com.hh.xml.internal.xsom.XSUnionSimpleType;
import com.hh.xml.internal.xsom.XSVariety;
import com.hh.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.hh.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.hh.xml.internal.xsom.visitor.XSContentTypeVisitor;
import com.hh.xml.internal.xsom.visitor.XSFunction;
import com.hh.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

import java.util.Set;

public abstract class SimpleTypeImpl extends DeclarationImpl
    implements XSSimpleType, ContentTypeImpl, Ref.SimpleType
{
    SimpleTypeImpl(
        SchemaDocumentImpl _parent,
        AnnotationImpl _annon,
        Locator _loc,
        ForeignAttributesImpl _fa,
        String _name,
        boolean _anonymous,
        Set<XSVariety> finalSet,
        Ref.SimpleType _baseType) {

        super(_parent, _annon, _loc, _fa, _parent.getTargetNamespace(), _name, _anonymous);

        this.baseType = _baseType;
        this.finalSet = finalSet;
    }

    private Ref.SimpleType baseType;

    public XSType[] listSubstitutables() {
        return Util.listSubstitutables(this);
    }

    public void redefine( SimpleTypeImpl st ) {
        baseType = st;
        st.redefinedBy = this;
        redefiningCount = (short)(st.redefiningCount+1);
    }

    /**
     * Number of times this component redefines other components.
     */
    private short redefiningCount = 0;

    private SimpleTypeImpl redefinedBy = null;

    public XSSimpleType getRedefinedBy() {
        return redefinedBy;
    }

    public int getRedefinedCount() {
        int i=0;
        for( SimpleTypeImpl st =this.redefinedBy; st !=null; st =st.redefinedBy)
            i++;
        return i;
    }

    public XSType getBaseType() { return baseType.getType(); }
    public XSSimpleType getSimpleBaseType() { return baseType.getType(); }
    public boolean isPrimitive() { return false; }

    public XSListSimpleType getBaseListType() {
        return getSimpleBaseType().getBaseListType();
    }

    public XSUnionSimpleType getBaseUnionType() {
        return getSimpleBaseType().getBaseUnionType();
    }

    private final Set<XSVariety> finalSet;

    public boolean isFinal(XSVariety v) {
        return finalSet.contains(v);
    }


    public final int getDerivationMethod() { return XSType.RESTRICTION; }


    public final XSSimpleType asSimpleType()  { return this; }
    public final XSComplexType asComplexType(){ return null; }

    public boolean isDerivedFrom(XSType t) {
        XSType x = this;
        while(true) {
            if(t==x)
                return true;
            XSType s = x.getBaseType();
            if(s==x)
                return false;
            x = s;
        }
    }

    public final boolean isSimpleType()       { return true; }
    public final boolean isComplexType()      { return false; }
    public final XSParticle asParticle()      { return null; }
    public final XSContentType asEmpty()      { return null; }


    public boolean isRestriction() { return false; }
    public boolean isList() { return false; }
    public boolean isUnion() { return false; }
    public XSRestrictionSimpleType asRestriction() { return null; }
    public XSListSimpleType asList() { return null; }
    public XSUnionSimpleType asUnion() { return null; }




    public final void visit( XSVisitor visitor ) {
        visitor.simpleType(this);
    }
    public final void visit( XSContentTypeVisitor visitor ) {
        visitor.simpleType(this);
    }
    public final Object apply( XSFunction function ) {
        return function.simpleType(this);
    }
    public final Object apply( XSContentTypeFunction function ) {
        return function.simpleType(this);
    }

    // Ref.ContentType implementation
    public XSContentType getContentType() { return this; }
    // Ref.SimpleType implementation
    public XSSimpleType getType() { return this; }
}
