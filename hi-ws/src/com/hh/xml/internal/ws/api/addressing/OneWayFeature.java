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

package com.hh.xml.internal.ws.api.addressing;

import com.hh.xml.internal.ws.api.FeatureConstructor;

import com.hh.webservice.ws.WebServiceFeature;

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Unsupported RI extension to work around an issue in WSIT.
 *
 * <p>
 * <b>This feature is not meant to be used by a common Web service developer</b> as there
 * is no need to send the above mentioned header for a one-way operation. But these
 * properties may need to be sent in certain middleware Web services.
 *
 * <p>
 * This feature allows ReplyTo, From and RelatesTo Message Addressing Properties
 * to be added for all messages that are sent from the port configured with
 * this annotation. All operations are assumed to be one-way, and
 * this feature should be used for one-way
 * operations only.
 *
 * If a non-null ReplyTo is specified, then MessageID property is also added.
 *
 * @author Arun Gupta
 */
@ManagedData
public class OneWayFeature extends WebServiceFeature {
    /**
     * Constant value identifying the {@link OneWayFeature}
     */
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";

    private WSEndpointReference replyTo;
    private WSEndpointReference from;
    private String relatesToID;

    /**
     * Create an {@link OneWayFeature}. The instance created will be enabled.
     */
    public OneWayFeature() {
        this.enabled = true;
    }

    /**
     * Create an {@link OneWayFeature}
     *
     * @param enabled specifies whether this feature should
     *                be enabled or not.
     */
    public OneWayFeature(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Create an {@link OneWayFeature}
     *
     * @param enabled specifies whether this feature should be enabled or not.
     * @param replyTo specifies the {@link WSEndpointReference} of wsa:ReplyTo header.
     */
    public OneWayFeature(boolean enabled, WSEndpointReference replyTo) {
        this.enabled = enabled;
        this.replyTo = replyTo;
    }

    /**
     * Create an {@link OneWayFeature}
     *
     * @param enabled specifies whether this feature should be enabled or not.
     * @param replyTo specifies the {@link WSEndpointReference} of wsa:ReplyTo header.
     * @param from specifies the {@link WSEndpointReference} of wsa:From header.
     * @param relatesTo specifies the MessageID to be used for wsa:RelatesTo header.
     */
    @FeatureConstructor({"enabled","replyTo","from","relatesTo"})
    public OneWayFeature(boolean enabled, WSEndpointReference replyTo, WSEndpointReference from, String relatesTo) {
        this.enabled = enabled;
        this.replyTo = replyTo;
        this.from = from;
        this.relatesToID = relatesTo;
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    /**
     * Getter for wsa:ReplyTo header {@link WSEndpointReference} .
     *
     * @return address of the wsa:ReplyTo header
     */
    @ManagedAttribute
    public WSEndpointReference getReplyTo() {
        return replyTo;
    }

    /**
     * Setter for wsa:ReplyTo header {@link WSEndpointReference}.
     *
     * @param address
     */
    public void setReplyTo(WSEndpointReference address) {
        this.replyTo = address;
    }

    /**
     * Getter for wsa:From header {@link WSEndpointReference}.
     *
     * @return address of the wsa:From header
     */
    @ManagedAttribute
    public WSEndpointReference getFrom() {
        return from;
    }

    /**
     * Setter for wsa:From header {@link WSEndpointReference}.
     *
     * @param address of the wsa:From header
     */
    public void setFrom(WSEndpointReference address) {
        this.from = address;
    }

    /**
     * Getter for MessageID for wsa:RelatesTo header.
     *
     * @return address of the wsa:FaultTo header
     */
    @ManagedAttribute
    public String getRelatesToID() {
        return relatesToID;
    }

    /**
     * Setter for MessageID for wsa:RelatesTo header.
     *
     * @param id
     */
    public void setRelatesToID(String id) {
        this.relatesToID = id;
    }
}
