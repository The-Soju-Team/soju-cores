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

package com.hh.xml.internal.bind.v2.runtime.unmarshaller;

import java.net.URL;

import javax.xml.bind.ValidationEventLocator;

import org.xml.sax.Locator;
import org.w3c.dom.Node;

/**
 * Object that returns the current location that the {@link XmlVisitor} is parsing.
 *
 * @author Kohsuke Kawaguchi
 */
public interface LocatorEx extends Locator {
    /**
     * Gets the current location in a {@link ValidationEventLocator} object.
     */
    ValidationEventLocator getLocation();

    /**
     * Immutable snapshot of a {@link LocatorEx}
     */
    public static final class Snapshot implements LocatorEx, ValidationEventLocator {
        private final int columnNumber,lineNumber,offset;
        private final String systemId,publicId;
        private final URL url;
        private final Object object;
        private final Node node;

        public Snapshot(LocatorEx loc) {
            columnNumber = loc.getColumnNumber();
            lineNumber = loc.getLineNumber();
            systemId = loc.getSystemId();
            publicId = loc.getPublicId();

            ValidationEventLocator vel = loc.getLocation();
            offset = vel.getOffset();
            url = vel.getURL();
            object = vel.getObject();
            node = vel.getNode();
        }

        public Object getObject() {
            return object;
        }

        public Node getNode() {
            return node;
        }

        public int getOffset() {
            return offset;
        }

        public URL getURL() {
            return url;
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getPublicId() {
            return publicId;
        }

        public ValidationEventLocator getLocation() {
            return this;
        }
    }
}
