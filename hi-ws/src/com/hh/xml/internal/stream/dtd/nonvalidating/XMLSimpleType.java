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
 *
 * THIS FILE WAS MODIFIED BY SUN MICROSYSTEMS, INC.
 */

/*
 * Copyright 2005 The Apache Software Foundation.
 */

package com.hh.xml.internal.stream.dtd.nonvalidating;
/**
 */
public class XMLSimpleType {

    //
    // Constants
    //

    /** TYPE_CDATA */
    public static final short TYPE_CDATA = 0;

    /** TYPE_ENTITY */
    public static final short TYPE_ENTITY = 1;

    /** TYPE_ENUMERATION */
    public static final short TYPE_ENUMERATION = 2;

    /** TYPE_ID */
    public static final short TYPE_ID = 3;

    /** TYPE_IDREF */
    public static final short TYPE_IDREF = 4;

    /** TYPE_NMTOKEN */
    public static final short TYPE_NMTOKEN = 5;

    /** TYPE_NOTATION */
    public static final short TYPE_NOTATION = 6;

    /** TYPE_NAMED */
    public static final short TYPE_NAMED = 7;

    /** DEFAULT_TYPE_DEFAULT */
    public static final short DEFAULT_TYPE_DEFAULT = 3;

    /** DEFAULT_TYPE_FIXED */
    public static final short DEFAULT_TYPE_FIXED = 1;

    /** DEFAULT_TYPE_IMPLIED */
    public static final short DEFAULT_TYPE_IMPLIED = 0;

    /** DEFAULT_TYPE_REQUIRED */
    public static final short DEFAULT_TYPE_REQUIRED = 2;

    //
    // Data
    //

    /** type */
    public short type;

    /** name */
    public String name;

    /** enumeration */
    public String[] enumeration;

    /** list */
    public boolean list;

    /** defaultType */
    public short defaultType;

    /** defaultValue */
    public String defaultValue;

    /** non-normalized defaultValue */
    public String nonNormalizedDefaultValue;


    //
    // Methods
    //

    /**
     * setValues
     *
     * @param type
     * @param name
     * @param enumeration
     * @param list
     * @param defaultType
     * @param defaultValue
     * @param nonNormalizedDefaultValue
     * @param datatypeValidator
     */
    public void setValues(short type, String name, String[] enumeration,
    boolean list, short defaultType,
    String defaultValue, String nonNormalizedDefaultValue){

        this.type              = type;
        this.name              = name;
        // REVISIT: Should this be a copy? -Ac
        if (enumeration != null && enumeration.length > 0) {
            this.enumeration = new String[enumeration.length];
            System.arraycopy(enumeration, 0, this.enumeration, 0, this.enumeration.length);
        }
        else {
            this.enumeration = null;
        }
        this.list              = list;
        this.defaultType       = defaultType;
        this.defaultValue      = defaultValue;
        this.nonNormalizedDefaultValue      = nonNormalizedDefaultValue;

    } // setValues(short,String,String[],boolean,short,String,String,DatatypeValidator)

    /** Set values. */
    public void setValues(XMLSimpleType simpleType) {

        type = simpleType.type;
        name = simpleType.name;
        // REVISIT: Should this be a copy? -Ac
        if (simpleType.enumeration != null && simpleType.enumeration.length > 0) {
            enumeration = new String[simpleType.enumeration.length];
            System.arraycopy(simpleType.enumeration, 0, enumeration, 0, enumeration.length);
        }
        else {
            enumeration = null;
        }
        list = simpleType.list;
        defaultType = simpleType.defaultType;
        defaultValue = simpleType.defaultValue;
        nonNormalizedDefaultValue = simpleType.nonNormalizedDefaultValue;

    } // setValues(XMLSimpleType)

    /**
     * clear
     */
    public void clear() {
        this.type              = -1;
        this.name              = null;
        this.enumeration       = null;
        this.list              = false;
        this.defaultType       = -1;
        this.defaultValue      = null;
        this.nonNormalizedDefaultValue = null;
    }

} // class XMLSimpleType
