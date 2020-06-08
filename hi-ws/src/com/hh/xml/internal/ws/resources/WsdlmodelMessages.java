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
public final class WsdlmodelMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.wsdlmodel");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("wsdl.portaddress.epraddress.not.match", arg0, arg1, arg2);
    }

    /**
     * For Port: {0}, service location {1} does not match address {2} in the EndpointReference
     *
     */
    public static String WSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(arg0, arg1, arg2));
    }

    public static Localizable localizableWSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
        return messageFactory.getMessage("wsdl.import.should.be.wsdl", arg0);
    }

    /**
     * Import of {0} is violation of BP 1.1 R2001. Proceeding with a warning.
     * R2001 A DESCRIPTION MUST only use the WSDL "import" statement to import another WSDL description.
     *
     */
    public static String WSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
        return localizer.localize(localizableWSDL_IMPORT_SHOULD_BE_WSDL(arg0));
    }

    public static Localizable localizableMEX_METADATA_SYSTEMID_NULL() {
        return messageFactory.getMessage("Mex.metadata.systemid.null");
    }

    /**
     * Mex WSDL metadata can not be parsed, the systemId is of the MEX source is null.
     *
     */
    public static String MEX_METADATA_SYSTEMID_NULL() {
        return localizer.localize(localizableMEX_METADATA_SYSTEMID_NULL());
    }

}
