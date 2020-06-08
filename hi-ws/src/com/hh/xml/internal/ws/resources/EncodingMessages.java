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


package com.hh.xml.internal.ws.resources;

import com.hh.xml.internal.ws.util.localization.Localizable;
import com.hh.xml.internal.ws.util.localization.LocalizableMessageFactory;
import com.hh.xml.internal.ws.util.localization.Localizer;


/**
 * Defines string formatting method for each constant in the resource file
 *
 */
public final class EncodingMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.encoding");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableFAILED_TO_READ_RESPONSE(Object arg0) {
        return messageFactory.getMessage("failed.to.read.response", arg0);
    }

    /**
     * Failed to read a response: {0}
     *
     */
    public static String FAILED_TO_READ_RESPONSE(Object arg0) {
        return localizer.localize(localizableFAILED_TO_READ_RESPONSE(arg0));
    }

    public static Localizable localizableEXCEPTION_INCORRECT_TYPE(Object arg0) {
        return messageFactory.getMessage("exception.incorrectType", arg0);
    }

    /**
     * incorrect type. Expected java.lang.Exception, found {0}
     *
     */
    public static String EXCEPTION_INCORRECT_TYPE(Object arg0) {
        return localizer.localize(localizableEXCEPTION_INCORRECT_TYPE(arg0));
    }

    public static Localizable localizableEXCEPTION_NOTFOUND(Object arg0) {
        return messageFactory.getMessage("exception.notfound", arg0);
    }

    /**
     * exception class: {0} not found in the model!
     *
     */
    public static String EXCEPTION_NOTFOUND(Object arg0) {
        return localizer.localize(localizableEXCEPTION_NOTFOUND(arg0));
    }

    public static Localizable localizableXSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
        return messageFactory.getMessage("xsd.unexpectedElementName", arg0, arg1);
    }

    /**
     * unexpected element name: expected={0}, actual: {1}
     *
     */
    public static String XSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
        return localizer.localize(localizableXSD_UNEXPECTED_ELEMENT_NAME(arg0, arg1));
    }

    public static Localizable localizableNESTED_DESERIALIZATION_ERROR(Object arg0) {
        return messageFactory.getMessage("nestedDeserializationError", arg0);
    }

    /**
     * deserialization error: {0}
     *
     */
    public static String NESTED_DESERIALIZATION_ERROR(Object arg0) {
        return localizer.localize(localizableNESTED_DESERIALIZATION_ERROR(arg0));
    }

    public static Localizable localizableXSD_UNKNOWN_PREFIX(Object arg0) {
        return messageFactory.getMessage("xsd.unknownPrefix", arg0);
    }

    /**
     * unknown prefix "{0}"
     *
     */
    public static String XSD_UNKNOWN_PREFIX(Object arg0) {
        return localizer.localize(localizableXSD_UNKNOWN_PREFIX(arg0));
    }

    public static Localizable localizableNESTED_ENCODING_ERROR(Object arg0) {
        return messageFactory.getMessage("nestedEncodingError", arg0);
    }

    /**
     * encoding error: {0}
     *
     */
    public static String NESTED_ENCODING_ERROR(Object arg0) {
        return localizer.localize(localizableNESTED_ENCODING_ERROR(arg0));
    }

    public static Localizable localizableUNKNOWN_OBJECT() {
        return messageFactory.getMessage("unknown.object");
    }

    /**
     * don't know how to write object: {0}
     *
     */
    public static String UNKNOWN_OBJECT() {
        return localizer.localize(localizableUNKNOWN_OBJECT());
    }

    public static Localizable localizableINCORRECT_MESSAGEINFO() {
        return messageFactory.getMessage("incorrect.messageinfo");
    }

    /**
     * can't write object! unexpected type: {0}
     *
     */
    public static String INCORRECT_MESSAGEINFO() {
        return localizer.localize(localizableINCORRECT_MESSAGEINFO());
    }

    public static Localizable localizableNESTED_SERIALIZATION_ERROR(Object arg0) {
        return messageFactory.getMessage("nestedSerializationError", arg0);
    }

    /**
     * serialization error: {0}
     *
     */
    public static String NESTED_SERIALIZATION_ERROR(Object arg0) {
        return localizer.localize(localizableNESTED_SERIALIZATION_ERROR(arg0));
    }

    public static Localizable localizableNO_SUCH_CONTENT_ID(Object arg0) {
        return messageFactory.getMessage("noSuchContentId", arg0);
    }

    /**
     * There''s no attachment for the content ID "{0}"
     *
     */
    public static String NO_SUCH_CONTENT_ID(Object arg0) {
        return localizer.localize(localizableNO_SUCH_CONTENT_ID(arg0));
    }

}
