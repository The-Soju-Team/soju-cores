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

import com.hh.xml.internal.bind.api.AccessorException;
import com.hh.xml.internal.bind.v2.runtime.Transducer;

import org.xml.sax.SAXException;

/**
 * Unmarshals a text into an object.
 *
 * <p>
 * If the caller can use {@link LeafPropertyLoader}, that's usually faster.
 *
 * @see LeafPropertyLoader
 * @see ValuePropertyLoader
 * @author Kohsuke Kawaguchi
 */
public class TextLoader extends Loader {

    private final Transducer xducer;

    public TextLoader(Transducer xducer) {
        super(true);
        this.xducer = xducer;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        try {
            state.target = xducer.parse(text);
        } catch (AccessorException e) {
            handleGenericException(e,true);
        } catch (RuntimeException e) {
            handleParseConversionException(state,e);
        }
    }
}
