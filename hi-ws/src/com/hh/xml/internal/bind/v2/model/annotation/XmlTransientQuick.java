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

package com.hh.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick
    extends Quick
    implements XmlTransient
{

    private final XmlTransient core;

    public XmlTransientQuick(Locatable upstream, XmlTransient core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlTransientQuick(upstream, ((XmlTransient) core));
    }

    public Class<XmlTransient> annotationType() {
        return XmlTransient.class;
    }

}
