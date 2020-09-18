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

package com.hh.xml.internal.ws.binding;

import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.client.HandlerConfiguration;
import com.hh.xml.internal.ws.resources.ClientMessages;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.handler.Handler;
import com.hh.webservice.ws.handler.LogicalHandler;
import com.hh.webservice.ws.http.HTTPBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author WS Development Team
 */
public class HTTPBindingImpl extends BindingImpl implements HTTPBinding {

    /**
     * Use {@link BindingImpl#create(BindingID)} to create this.
     */
    HTTPBindingImpl() {
        // TODO: implement a real Codec for these
        super(BindingID.XML_HTTP);
    }

    /**
     * This method separates the logical and protocol handlers and
     * sets the HandlerConfiguration.
     * Only logical handlers are allowed with HTTPBinding.
     * Setting SOAPHandlers throws WebServiceException
     */
    public void setHandlerChain(List<Handler> chain) {
        List<LogicalHandler> logicalHandlers = new ArrayList<LogicalHandler>();
        for (Handler handler : chain) {
            if (!(handler instanceof LogicalHandler)) {
                throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(handler.getClass()));
            } else {
                logicalHandlers.add((LogicalHandler) handler);
            }
        }
        handlerConfig = new HandlerConfiguration(Collections.<String>emptySet(), chain);
    }
}
