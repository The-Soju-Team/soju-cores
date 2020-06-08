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

package com.hh.xml.internal.ws.client;

import com.hh.xml.internal.ws.util.JAXWSUtils;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.Service;
import com.hh.webservice.ws.WebEndpoint;
import com.hh.webservice.ws.WebServiceClient;
import com.hh.webservice.ws.WebServiceException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * Represents parsed {@link WebServiceClient} and {@link WebEndpoint}
 * annotations on a {@link Service}-derived class.
 *
 * @author Kohsuke Kawaguchi
 */
final class SCAnnotations {
    SCAnnotations(final Class<?> sc) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                WebServiceClient wsc =sc.getAnnotation(WebServiceClient.class);
                if(wsc==null)
                    throw new WebServiceException("Service Interface Annotations required, exiting...");

                String name = wsc.name();
                String tns = wsc.targetNamespace();
                serviceQName = new QName(tns, name);
                try {
                    wsdlLocation = JAXWSUtils.getFileOrURL(wsc.wsdlLocation());
                } catch (IOException e) {
                    // TODO: report a reasonable error message
                    throw new WebServiceException(e);
                }

                for (Method method : sc.getDeclaredMethods()) {
                    WebEndpoint webEndpoint = method.getAnnotation(WebEndpoint.class);
                    if (webEndpoint != null) {
                        String endpointName = webEndpoint.name();
                        QName portQName = new QName(tns, endpointName);
                        portQNames.add(portQName);
                    }
                    Class<?> seiClazz = method.getReturnType();
                    if (seiClazz!=void.class) {
                        classes.add(seiClazz);
                    }
                }

                return null;
            }
        });
    }

    QName serviceQName;
    final ArrayList<QName> portQNames = new ArrayList<QName>();
    final ArrayList<Class> classes = new ArrayList<Class>();
    URL wsdlLocation;
}
