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

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.client.HandlerConfiguration;
import com.hh.xml.internal.ws.encoding.soap.streaming.SOAP12NamespaceConstants;
import com.hh.xml.internal.ws.resources.ClientMessages;

import javax.xml.namespace.QName;
import com.hh.webservice.soap.MessageFactory;
import com.hh.webservice.soap.SOAPFactory;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.handler.Handler;

import com.hh.webservice.ws.soap.MTOMFeature;
import com.hh.webservice.ws.soap.SOAPBinding;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




/**
 * @author WS Development Team
 */
public final class SOAPBindingImpl extends BindingImpl implements SOAPBinding {

    public static final String X_SOAP12HTTP_BINDING =
        "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/";

    private static final String ROLE_NONE = SOAP12NamespaceConstants.ROLE_NONE;
    //protected boolean enableMtom;
    protected final SOAPVersion soapVersion;

    private Set<QName> portKnownHeaders = Collections.emptySet();
    private Set<QName> bindingUnderstoodHeaders = new HashSet<QName>();

    /**
     * Use {@link BindingImpl#create(BindingID)} to create this.
     */
    SOAPBindingImpl(BindingID bindingId) {
        this(bindingId,EMPTY_FEATURES);
    }

    /**
     * Use {@link BindingImpl#create(BindingID)} to create this.
     *
     * @param features
     *      These features have a precedence over
     *      {@link BindingID#createBuiltinFeatureList() the implicit features}
     *      associated with the {@link BindingID}.
     */
    SOAPBindingImpl(BindingID bindingId, WebServiceFeature... features) {
        super(bindingId);
        this.soapVersion = bindingId.getSOAPVersion();
        //populates with required roles and updates handlerConfig
        setRoles(new HashSet<String>());
        //Is this still required? comment out for now
        //setupSystemHandlerDelegate(serviceName);

        setFeatures(features);
        this.features.addAll(bindingId.createBuiltinFeatureList());
        populateBindingUnderstoodHeaders();
    }

    /**
     *  This method should be called if the binding has SOAPSEIModel
     *  The Headers understood by the Port are set, so that they can be used for MU
     *  processing.
     *
     * @param headers
     */
    public void setPortKnownHeaders(@NotNull Set<QName> headers) {
        this.portKnownHeaders = headers;
    }

    public boolean understandsHeader(QName header) {
        if(serviceMode == javax.xml.ws.Service.Mode.MESSAGE)
            return true;
        if(portKnownHeaders.contains(header))
            return true;
        if(bindingUnderstoodHeaders.contains(header))
            return true;

        return false;
    }

    /**
     * Understand WS-Addressing headers if WS-Addressing is enabled
     *
     */
    private void populateBindingUnderstoodHeaders() {
        AddressingVersion addressingVersion = getAddressingVersion();
        if (addressingVersion != null) {
            bindingUnderstoodHeaders.add(addressingVersion.actionTag);
            bindingUnderstoodHeaders.add(addressingVersion.faultToTag);
            bindingUnderstoodHeaders.add(addressingVersion.fromTag);
            bindingUnderstoodHeaders.add(addressingVersion.messageIDTag);
            bindingUnderstoodHeaders.add(addressingVersion.relatesToTag);
            bindingUnderstoodHeaders.add(addressingVersion.replyToTag);
            bindingUnderstoodHeaders.add(addressingVersion.toTag);
        }
    }

    /**
     * Sets the handlers on the binding and then sorts the handlers in to logical and protocol handlers.
     * Creates a new HandlerConfiguration object and sets it on the BindingImpl. Also parses Headers understood by
     * Protocol Handlers and sets the HandlerConfiguration.
     */
    public void setHandlerChain(List<Handler> chain) {
        handlerConfig = new HandlerConfiguration(handlerConfig.getRoles(), chain);
    }

    protected void addRequiredRoles(Set<String> roles) {
        roles.addAll(soapVersion.requiredRoles);
    }

    public Set<String> getRoles() {
        return handlerConfig.getRoles();
    }

    /**
     * Adds the next and other roles in case this has
     * been called by a user without them.
     * Creates a new HandlerConfiguration object and sets it on the BindingImpl.
     */
    public void setRoles(Set<String> roles) {
        if (roles == null) {
            roles = new HashSet<String>();
        }
        if (roles.contains(ROLE_NONE)) {
            throw new WebServiceException(ClientMessages.INVALID_SOAP_ROLE_NONE());
        }
        addRequiredRoles(roles);
        handlerConfig = new HandlerConfiguration(roles, getHandlerConfig());
    }


    /**
     * Used typically by the runtime to enable/disable Mtom optimization
     */
    public boolean isMTOMEnabled() {
        return isFeatureEnabled(MTOMFeature.class);
    }

    /**
     * Client application can override if the MTOM optimization should be enabled
     */
    public void setMTOMEnabled(boolean b) {
        setFeatures(new MTOMFeature(b));
    }

    public SOAPFactory getSOAPFactory() {
        return soapVersion.saajSoapFactory;
    }

    public MessageFactory getMessageFactory() {
        return soapVersion.saajMessageFactory;
    }

    private static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
}
