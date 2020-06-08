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

package com.hh.xml.internal.bind.v2.model.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import javax.xml.namespace.QName;

import com.hh.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.hh.xml.internal.bind.v2.model.core.NonElement;
import com.hh.xml.internal.bind.v2.model.nav.ReflectionNavigator;

/**
 * {@link TypeInfoSet} refined for runtime.
 *
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeTypeInfoSet extends TypeInfoSet<Type,Class,Field,Method>{
    Map<Class,? extends RuntimeArrayInfo> arrays();
    Map<Class,? extends RuntimeClassInfo> beans();
    Map<Type,? extends RuntimeBuiltinLeafInfo> builtins();
    Map<Class,? extends RuntimeEnumLeafInfo> enums();
    RuntimeNonElement getTypeInfo( Type type );
    RuntimeNonElement getAnyTypeInfo();
    RuntimeNonElement getClassInfo( Class type );
    RuntimeElementInfo getElementInfo( Class scope, QName name );
    Map<QName,? extends RuntimeElementInfo> getElementMappings( Class scope );
    Iterable<? extends RuntimeElementInfo> getAllElements();
    ReflectionNavigator getNavigator();
}
