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

package com.hh.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

import com.hh.webservice.ws.WebServiceException;

/**
 * Extension point to plug in additional {@link BindingID} parsing logic.
 *
 * <p>
 * When the JAX-WS RI is asked to parse a binding ID string into a {@link BindingID}
 * object, it uses service idiom to look for the implementations of this class
 * in the <tt>META-INF/services/...</tt>.
 *
 * @since JAX-WS 2.0.next
 * @author Kohsuke Kawaguchi
 * @see BindingID#parse(String)
 */
public abstract class BindingIDFactory {
    /**
     * Parses a binding ID string into {@link BindingID} if possible.
     *
     * @return
     *      a non-null return value would cause the JAX-WS RI to consider
     *      the parsing to be successful. No furhter {@link BindingIDFactory}
     *      will be consulted.
     *
     *      <p>
     *      Retruning a null value indicates that this factory doesn't understand
     *      this string, in which case the JAX-WS RI will keep asking next
     *      {@link BindingIDFactory}.
     *
     * @throws WebServiceException
     *      if the implementation understood the lexical value but it is not correct,
     *      this exception can be thrown to abort the parsing with error.
     *      No further {@link BindingIDFactory} will be consulted, and
     *      {@link BindingID#parse(String)} will throw the exception.
     */
    public abstract @Nullable BindingID parse(@NotNull String lexical) throws WebServiceException;
}
