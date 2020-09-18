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
public final class SenderMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.sender");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
        return messageFactory.getMessage("sender.request.illegalValueForContentNegotiation", arg0);
    }

    /**
     * illegal value for content negotiation property "{0}"
     *
     */
    public static String SENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
        return localizer.localize(localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(arg0));
    }

    public static Localizable localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return messageFactory.getMessage("sender.response.cannotDecodeFaultDetail");
    }

    /**
     * fault detail cannot be decoded
     *
     */
    public static String SENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return localizer.localize(localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL());
    }

    public static Localizable localizableSENDER_NESTED_ERROR(Object arg0) {
        return messageFactory.getMessage("sender.nestedError", arg0);
    }

    /**
     * sender error: {0}
     *
     */
    public static String SENDER_NESTED_ERROR(Object arg0) {
        return localizer.localize(localizableSENDER_NESTED_ERROR(arg0));
    }

    public static Localizable localizableSENDER_REQUEST_MESSAGE_NOT_READY() {
        return messageFactory.getMessage("sender.request.messageNotReady");
    }

    /**
     * message not ready to be sent
     *
     */
    public static String SENDER_REQUEST_MESSAGE_NOT_READY() {
        return localizer.localize(localizableSENDER_REQUEST_MESSAGE_NOT_READY());
    }

}
