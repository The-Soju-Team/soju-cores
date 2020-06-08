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

package com.hh.xml.internal.xsom.impl;

import com.hh.xml.internal.xsom.XSElementDecl;
import com.hh.xml.internal.xsom.XSIdentityConstraint;
import com.hh.xml.internal.xsom.XSModelGroup;
import com.hh.xml.internal.xsom.XSModelGroupDecl;
import com.hh.xml.internal.xsom.XSTerm;
import com.hh.xml.internal.xsom.XSType;
import com.hh.xml.internal.xsom.XSWildcard;
import com.hh.xml.internal.xsom.XmlString;
import com.hh.xml.internal.xsom.impl.parser.PatcherManager;
import com.hh.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.hh.xml.internal.xsom.visitor.XSFunction;
import com.hh.xml.internal.xsom.visitor.XSTermFunction;
import com.hh.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.hh.xml.internal.xsom.visitor.XSTermVisitor;
import com.hh.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElementDecl extends DeclarationImpl implements XSElementDecl, Ref.Term
{
    public ElementDecl( PatcherManager reader, SchemaDocumentImpl owner,
        AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa,
        String _tns, String _name, boolean _anonymous,

        XmlString _defv, XmlString _fixedv,
        boolean _nillable, boolean _abstract, Boolean _form,
        Ref.Type _type, Ref.Element _substHead,
        int _substDisallowed, int _substExcluded,
        List<IdentityConstraintImpl> idConstraints) {

        super(owner,_annon,_loc,fa,_tns,_name,_anonymous);

        this.defaultValue = _defv;
        this.fixedValue = _fixedv;
        this.nillable = _nillable;
        this._abstract = _abstract;
        this.form = _form;
        this.type = _type;
        this.substHead = _substHead;
        this.substDisallowed = _substDisallowed;
        this.substExcluded = _substExcluded;
        this.idConstraints = Collections.unmodifiableList((List)idConstraints);

        for (IdentityConstraintImpl idc : idConstraints)
            idc.setParent(this);

        if(type==null)
            throw new IllegalArgumentException();
    }

    private XmlString defaultValue;
    public XmlString getDefaultValue() { return defaultValue; }

    private XmlString fixedValue;
    public XmlString getFixedValue() { return fixedValue; }

    private boolean nillable;
    public boolean isNillable() { return nillable; }

    private boolean _abstract;
    public boolean isAbstract() { return _abstract; }

    private Ref.Type type;
    public XSType getType() { return type.getType(); }

    private Ref.Element substHead;
    public XSElementDecl getSubstAffiliation() {
        if(substHead==null)     return null;
        return substHead.get();
    }

    private int substDisallowed;
    public boolean isSubstitutionDisallowed( int method ) {
        return (substDisallowed&method)!=0;
    }

    private int substExcluded;
    public boolean isSubstitutionExcluded( int method ) {
        return (substExcluded&method)!=0;
    }

    private final List<XSIdentityConstraint> idConstraints;
    public List<XSIdentityConstraint> getIdentityConstraints() {
        return idConstraints;
    }

    private Boolean form;
    public Boolean getForm() {
        return form;
    }


    /**
     * @deprecated
     */
    public XSElementDecl[] listSubstitutables() {
        Set<? extends XSElementDecl> s = getSubstitutables();
        return s.toArray(new XSElementDecl[s.size()]);
    }

    /** Set that represents element decls that can substitute this element. */
    private Set<XSElementDecl> substitutables = null;

    /** Unmodifieable view of {@link #substitutables}. */
    private Set<XSElementDecl> substitutablesView = null;

    public Set<? extends XSElementDecl> getSubstitutables() {
        if( substitutables==null ) {
            // if the field is null by the time this method
            // is called, it means this element is substitutable by itself only.
            substitutables = substitutablesView = Collections.singleton((XSElementDecl)this);
        }
        return substitutablesView;
    }

    protected void addSubstitutable( ElementDecl decl ) {
        if( substitutables==null ) {
            substitutables = new HashSet<XSElementDecl>();
            substitutables.add(this);
            substitutablesView = Collections.unmodifiableSet(substitutables);
        }
        substitutables.add(decl);
    }


    public void updateSubstitutabilityMap() {
        ElementDecl parent = this;
        XSType type = this.getType();

        boolean rused = false;
        boolean eused = false;

        while( (parent=(ElementDecl)parent.getSubstAffiliation())!=null ) {

            if(parent.isSubstitutionDisallowed(XSType.SUBSTITUTION))
                continue;

            boolean rd = parent.isSubstitutionDisallowed(XSType.RESTRICTION);
            boolean ed = parent.isSubstitutionDisallowed(XSType.EXTENSION);

            if( (rd && rused) || ( ed && eused ) )   continue;

            XSType parentType = parent.getType();
            while (type!=parentType) {
                if(type.getDerivationMethod()==XSType.RESTRICTION)  rused = true;
                else                                                eused = true;

                type = type.getBaseType();
                if(type==null)  // parentType and type doesn't share the common base type. a bug in the schema.
                    break;

                if( type.isComplexType() ) {
                    rd |= type.asComplexType().isSubstitutionProhibited(XSType.RESTRICTION);
                    ed |= type.asComplexType().isSubstitutionProhibited(XSType.EXTENSION);
                }
                if (getRoot().getAnyType().equals(type)) break;
            }

            if( (rd && rused) || ( ed && eused ) )   continue;

            // this element can substitute "parent"
            parent.addSubstitutable(this);
        }
    }

    public boolean canBeSubstitutedBy(XSElementDecl e) {
        return getSubstitutables().contains(e);
    }

    public boolean isWildcard()                 { return false; }
    public boolean isModelGroupDecl()           { return false; }
    public boolean isModelGroup()               { return false; }
    public boolean isElementDecl()              { return true; }

    public XSWildcard asWildcard()              { return null; }
    public XSModelGroupDecl asModelGroupDecl()  { return null; }
    public XSModelGroup asModelGroup()          { return null; }
    public XSElementDecl asElementDecl()        { return this; }




    public void visit( XSVisitor visitor ) {
        visitor.elementDecl(this);
    }
    public void visit( XSTermVisitor visitor ) {
        visitor.elementDecl(this);
    }
    public Object apply( XSTermFunction function ) {
        return function.elementDecl(this);
    }

    public <T,P> T apply(XSTermFunctionWithParam<T, P> function, P param) {
        return function.elementDecl(this,param);
    }

    public Object apply( XSFunction function ) {
        return function.elementDecl(this);
    }


    // Ref.Term implementation
    public XSTerm getTerm() { return this; }
}
