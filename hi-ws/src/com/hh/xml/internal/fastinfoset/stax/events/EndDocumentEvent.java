/*
 * Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
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
 *
 * THIS FILE WAS MODIFIED BY SUN MICROSYSTEMS, INC.
 */

package com.hh.xml.internal.fastinfoset.stax.events ;

import javax.xml.stream.events.EndDocument;


public class EndDocumentEvent extends EventBase implements EndDocument {

    public EndDocumentEvent() {
        super(END_DOCUMENT);
    }

    public String toString() {
        return "<? EndDocument ?>";
    }

}
