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

package com.hh.xml.internal.ws.util;

import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.server.AsyncProvider;
import com.hh.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.hh.xml.internal.ws.handler.HandlerChainsModel;
import com.hh.xml.internal.ws.server.EndpointFactory;
import com.hh.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.istack.internal.NotNull;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPMessageHandlers;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.hh.webservice.ws.Provider;
import com.hh.webservice.ws.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

/**
 * <p>Used by client and server side to create handler information
 * from annotated class. The public methods all return a
 * HandlerChainInfo that contains the handlers and role information
 * needed at runtime.
 *
 * <p>All of the handler chain descriptors follow the same schema,
 * whether they are wsdl customizations, handler files specified
 * by an annotation, or are included in the sun-jaxws.xml file.
 * So this class is used for all handler xml information. The
 * two public entry points are
 * {@link HandlerAnnotationProcessor#buildHandlerInfo}, called
 * when you have an annotated class that points to a file.
 *
 * <p>The methods in the class are static so that it may called
 * from the runtime statically.
 *
 * @see com.hh.xml.internal.ws.util.HandlerAnnotationInfo
 *
 * @author JAX-WS Development Team
 */
public class HandlerAnnotationProcessor {

    private static final Logger logger = Logger.getLogger(com.hh.xml.internal.ws.util.Constants.LoggingDomain + ".util");

    /**
     * <p>This method is called by
     * {@link EndpointFactory} when
     * they have an annotated class.
     *
     * <p>If there is no handler chain annotation on the class,
     * this method will return null. Otherwise it will load the
     * class and call the parseHandlerFile method to read the
     * information.
     *
     * @return A HandlerAnnotationInfo object that stores the
     * handlers and roles. Will return null if the class passed
     * in has no handler chain annotation.
     */
    public static HandlerAnnotationInfo buildHandlerInfo(@NotNull
        Class<?> clazz, QName serviceName, QName portName, WSBinding binding) {

//        clazz = checkClass(clazz);
        HandlerChain handlerChain =
            clazz.getAnnotation(HandlerChain.class);
        if (handlerChain == null) {
            clazz = getSEI(clazz);
            if (clazz != null)
            handlerChain =
                clazz.getAnnotation(HandlerChain.class);
            if (handlerChain == null)
                return null;
        }

        if (clazz.getAnnotation(SOAPMessageHandlers.class) != null) {
            throw new UtilException(
                "util.handler.cannot.combine.soapmessagehandlers");
        }
        InputStream iStream = getFileAsStream(clazz, handlerChain);
        XMLStreamReader reader =
            XMLStreamReaderFactory.create(null,iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        HandlerAnnotationInfo handlerAnnInfo = HandlerChainsModel.parseHandlerFile(reader, clazz.getClassLoader(),
            serviceName, portName, binding);
        try {
            reader.close();
            iStream.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage());
        }
        return handlerAnnInfo;
    }

    public static HandlerChainsModel buildHandlerChainsModel(final Class<?> clazz) {
        if(clazz == null) {
            return null;
        }
        HandlerChain handlerChain =
            clazz.getAnnotation(HandlerChain.class);
        if(handlerChain == null)
            return null;
        InputStream iStream = getFileAsStream(clazz, handlerChain);
        XMLStreamReader reader =
            XMLStreamReaderFactory.create(null,iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        HandlerChainsModel handlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(clazz, reader);
        try {
            reader.close();
            iStream.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage());
        }
        return handlerChainsModel;
    }

    static Class getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(
                className);
        } catch (ClassNotFoundException e) {
            throw new UtilException("util.handler.class.not.found",
                className);
        }
    }

    static Class getSEI(Class<?> clazz) {
        if (Provider.class.isAssignableFrom(clazz) || AsyncProvider.class.isAssignableFrom(clazz)) {
            //No SEI for Provider Implementation
            return null;
        }
        if (Service.class.isAssignableFrom(clazz)) {
            //No SEI for Service class
            return null;
        }
        if (!clazz.isAnnotationPresent(WebService.class)) {
            throw new UtilException("util.handler.no.webservice.annotation",
                clazz.getCanonicalName());
        }

        WebService webService = clazz.getAnnotation(WebService.class);

        String ei = webService.endpointInterface();
        if (ei.length() > 0) {
            clazz = getClass(webService.endpointInterface());
            if (!clazz.isAnnotationPresent(WebService.class)) {
                throw new UtilException("util.handler.endpoint.interface.no.webservice",
                    webService.endpointInterface());
            }
            return clazz;
        }
        return null;
    }

   static InputStream getFileAsStream(Class clazz, HandlerChain chain) {
        URL url = clazz.getResource(chain.file());
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().
                getResource(chain.file());
        }
        if (url == null) {
            String tmp = clazz.getPackage().getName();
            tmp = tmp.replace('.', '/');
            tmp += "/" + chain.file();
            url =
                Thread.currentThread().getContextClassLoader().getResource(tmp);
        }
        if (url == null) {
            throw new UtilException("util.failed.to.find.handlerchain.file",
                clazz.getName(), chain.file());
        }
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new UtilException("util.failed.to.parse.handlerchain.file",
                clazz.getName(), chain.file());
        }
    }
}
