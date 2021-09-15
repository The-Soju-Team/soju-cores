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

package com.hh.xml.internal.ws.developer;

import com.hh.xml.internal.ws.api.FeatureConstructor;
import com.sun.istack.internal.Nullable;

import com.hh.webservice.ws.WebServiceFeature;

import com.hh.xml.internal.org.jvnet.mimepull.MIMEConfig;

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Proxy needs to be created with this feature to configure StreamingAttachment
 * attachments behaviour.
 *
 * <pre>
 * for e.g.: To configure all StreamingAttachment attachments to be kept in memory
 * <p>
 *
 * StreamingAttachmentFeature feature = new StreamingAttachmentFeature();
 * feature.setAllMemory(true);
 *
 * proxy = HelloService().getHelloPort(feature);
 *
 * </pre>
 *
 * @author Jitendra Kotamraju
 */
@ManagedData
public final class StreamingAttachmentFeature extends WebServiceFeature {
    /**
     * Constant value identifying the {@link @StreamingAttachment} feature.
     */
    public static final String ID = "http://jax-ws.dev.java.net/features/mime";

    private MIMEConfig config;

    private String dir;
    private boolean parseEagerly;
    private long memoryThreshold;

    public StreamingAttachmentFeature() {
    }

    @FeatureConstructor({"dir","parseEagerly","memoryThreshold"})
    public StreamingAttachmentFeature(@Nullable String dir, boolean parseEagerly, long memoryThreshold) {
        this.enabled = true;
        this.dir = dir;
        this.parseEagerly = parseEagerly;
        this.memoryThreshold = memoryThreshold;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    /**
     * Returns the configuration object. Once this is called, you cannot
     * change the configuration.
     *
     * @return
     */
    @ManagedAttribute
    public MIMEConfig getConfig() {
        if (config == null) {
            config = new MIMEConfig();
            config.setDir(dir);
            config.setParseEagerly(parseEagerly);
            config.setMemoryThreshold(memoryThreshold);
            config.validate();
        }
        return config;
    }

    /**
     * Directory in which large attachments are stored
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * StreamingAttachment message is parsed eagerly
     */
    public void setParseEagerly(boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }

    /**
     * After this threshold(no of bytes), large attachments are
     * written to file system
     */
    public void setMemoryThreshold(long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

}
