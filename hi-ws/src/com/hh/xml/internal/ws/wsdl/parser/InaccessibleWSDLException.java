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

package com.hh.xml.internal.ws.wsdl.parser;

import com.hh.webservice.ws.WebServiceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of {@link InaccessibleWSDLException} wrapped in one exception.
 *
 * <p>
 * This exception is used to report all the errors during WSDL parsing from {@link RuntimeWSDLParser#parse(java.net.URL, org.xml.sax.EntityResolver, boolean, com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension[])}
 *
 * @author Vivek Pandey
 */
public class InaccessibleWSDLException extends WebServiceException {

    private final List<Throwable> errors;

    private static final long serialVersionUID = 1L;

    public InaccessibleWSDLException(List<Throwable> errors) {
        super(errors.size()+" counts of InaccessibleWSDLException.\n");
        assert !errors.isEmpty() : "there must be at least one error";
        this.errors = Collections.unmodifiableList(new ArrayList<Throwable>(errors));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append('\n');

        for( Throwable error : errors )
            sb.append(error.toString()).append('\n');

        return sb.toString();
    }

    /**
     * Returns a read-only list of {@link InaccessibleWSDLException}s
     * wrapped in this exception.
     *
     * @return
     *      a non-null list.
     */
    public List<Throwable> getErrors() {
        return errors;
    }

    public static class Builder implements ErrorHandler {
        private final List<Throwable> list = new ArrayList<Throwable>();
        public void error(Throwable e) {
            list.add(e);
        }
        /**
         * If an error was reported, throw the exception.
         * Otherwise exit normally.
         */
        public void check() throws InaccessibleWSDLException {
            if(list.isEmpty())
                return;
            throw new InaccessibleWSDLException(list);
        }
    }

}
