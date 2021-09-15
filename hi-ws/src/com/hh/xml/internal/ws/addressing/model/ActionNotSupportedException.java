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

package com.hh.xml.internal.ws.addressing.model;

import com.hh.xml.internal.ws.resources.AddressingMessages;

import com.hh.webservice.ws.WebServiceException;

/**
 * @author Arun Gupta
 */
public class ActionNotSupportedException extends WebServiceException {
    private String action;

    public ActionNotSupportedException(String action) {
        super(AddressingMessages.ACTION_NOT_SUPPORTED_EXCEPTION(action));
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
