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

import javax.xml.bind.JAXBElement;

import com.hh.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.hh.xml.internal.bind.api.AccessorException;

import org.xml.sax.SAXException;

/**
 * Reads a text value and set to the current target.
 *
 * @see LeafPropertyLoader
 * @author Kohsuke Kawaguchi
 */
public class ValuePropertyLoader extends Loader {

    private final TransducedAccessor xacc;

    public ValuePropertyLoader(TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        try {
            xacc.parse(state.target,text);
        } catch (AccessorException e) {
            handleGenericException(e,true);
        } catch (RuntimeException e) {
            if(state.prev != null) {
                if(state.prev.target instanceof JAXBElement) {
                    ; // do nothing - issue 601 - don't report exceptions like
                      // NumberFormatException when unmarshalling "nillable" element
                      // (I suppose JAXBElement indicates this
                } else {
                    handleParseConversionException(state,e);
                }
            } else {
                handleParseConversionException(state,e);
            }
        }
    }
}
