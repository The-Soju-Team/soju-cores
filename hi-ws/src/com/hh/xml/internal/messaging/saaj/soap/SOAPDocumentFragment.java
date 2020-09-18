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

/**
*
* @author SAAJ RI Development Team
*/
package com.hh.xml.internal.messaging.saaj.soap;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentFragmentImpl;

public class SOAPDocumentFragment extends DocumentFragmentImpl {

    public SOAPDocumentFragment(CoreDocumentImpl ownerDoc) {
        super(ownerDoc);
    }

    public SOAPDocumentFragment() {
        super();
    }

}
