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

package com.hh.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.bind.api.JAXBRIContext;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.server.AsyncProvider;
import com.hh.xml.internal.ws.resources.ServerMessages;

import javax.activation.DataSource;
import com.hh.webservice.soap.SOAPMessage;
import javax.xml.transform.Source;
import com.hh.webservice.ws.Provider;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.ServiceMode;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.soap.SOAPBinding;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Keeps the runtime information like Service.Mode and erasure of Provider class
 * about Provider endpoint. It proccess annotations to find about Service.Mode
 * It also finds about parameterized type(e.g. Source, SOAPMessage, DataSource)
 * of endpoint class.
 *
 * @author Jitendra Kotamraju
 * @author Kohsuke Kawaguchi
 */
final class ProviderEndpointModel<T> {
    /**
     * True if this is {@link AsyncProvider}.
     */
    final boolean isAsync;

    /**
     * In which mode does this provider operate?
     */
    @NotNull final Service.Mode mode;
    /**
     * T of {@link Provider}&lt;T>.
     */
    @NotNull final Class datatype;
    /**
     * User class that extends {@link Provider}.
     */
    @NotNull final Class implClass;

    ProviderEndpointModel(Class<T> implementorClass, WSBinding binding) {
        assert implementorClass != null;
        assert binding != null;

        implClass = implementorClass;
        mode = getServiceMode(implementorClass);
        Class otherClass = (binding instanceof SOAPBinding)
            ? SOAPMessage.class : DataSource.class;
        isAsync = AsyncProvider.class.isAssignableFrom(implementorClass);


        Class<? extends Object> baseType = isAsync ? AsyncProvider.class : Provider.class;
        Type baseParam = JAXBRIContext.getBaseType(implementorClass, baseType);
        if (baseParam==null)
            throw new WebServiceException(ServerMessages.NOT_IMPLEMENT_PROVIDER(implementorClass.getName()));
        if (!(baseParam instanceof ParameterizedType))
            throw new WebServiceException(ServerMessages.PROVIDER_NOT_PARAMETERIZED(implementorClass.getName()));

        ParameterizedType pt = (ParameterizedType)baseParam;
        Type[] types = pt.getActualTypeArguments();
        if(!(types[0] instanceof Class))
            throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(implementorClass.getName(),types[0]));
        datatype = (Class)types[0];

        if (mode == Service.Mode.PAYLOAD && datatype!=Source.class) {
            // Illegal to have PAYLOAD && SOAPMessage
            // Illegal to have PAYLOAD && DataSource
            throw new IllegalArgumentException(
                "Illeagal combination - Mode.PAYLOAD and Provider<"+otherClass.getName()+">");
        }
    }

    /**
     * Is it PAYLOAD or MESSAGE ??
     *
     * @param c endpoint class
     * @return Service.Mode.PAYLOAD or Service.Mode.MESSAGE
     */
    private static Service.Mode getServiceMode(Class<?> c) {
        ServiceMode mode = c.getAnnotation(ServiceMode.class);
        return (mode == null) ? Service.Mode.PAYLOAD : mode.value();
    }
}
