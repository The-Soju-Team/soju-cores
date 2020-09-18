///*
// * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
// * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// *
// */
//
//package com.sun.istack.internal.ws;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//import com.sun.mirror.apt.*;
//import com.sun.mirror.declaration.*;
//
//
////import com.sun.tools.internal.ws.processor.modeler.annotation.AnnotationProcessorContext;
////import com.sun.tools.internal.ws.processor.modeler.annotation.WebServiceAP;
//
///*
// * The JAX-WS {@com.sun.mirror.apt.AnnotationProcessorFactory AnnotationProcessorFactory}
// * class used by the <a href="http://java.sun.com/j2se/1.5.0/docs/tooldocs/share/apt.html">APT</a>
// * framework.
// */
//public class AnnotationProcessorFactoryImpl implements AnnotationProcessorFactory {
//
//    //private static WebServiceAP wsAP;
//    /*
//     * Processor doesn't examine any options.
//     */
//    static final Collection<String> supportedOptions = Collections.unmodifiableSet(new HashSet<String>());
//
//
//    /*
//     * Supports javax.jws.*, javax.jws.soap.* and javax.xml.ws.* annotations.
//     */
//    static Collection<String> supportedAnnotations;
//    static {
//        Collection<String> types = new HashSet<String>();
//        types.add("javax.jws.HandlerChain");
//        types.add("javax.jws.Oneway");
//        types.add("javax.jws.WebMethod");
//        types.add("javax.jws.WebParam");
//        types.add("javax.jws.WebResult");
//        types.add("javax.jws.WebService");
//        types.add("javax.jws.soap.InitParam");
//        types.add("javax.jws.soap.SOAPBinding");
//        types.add("javax.jws.soap.SOAPMessageHandler");
//        types.add("javax.jws.soap.SOAPMessageHandlers");
//        types.add("javax.xml.ws.BindingType");
//        types.add("javax.xml.ws.RequestWrapper");
//        types.add("javax.xml.ws.ResponseWrapper");
//        types.add("javax.xml.ws.ServiceMode");
//        types.add("javax.xml.ws.WebEndpoint");
//        types.add("javax.xml.ws.WebFault");
//        types.add("javax.xml.ws.WebServiceClient");
//        types.add("javax.xml.ws.WebServiceProvider");
//        types.add("javax.xml.ws.WebServiceRef");
//        supportedAnnotations = Collections.unmodifiableCollection(types);
//    }
//
//    public AnnotationProcessorFactoryImpl() {
//    }
//
//
//    public Collection<String> supportedOptions() {
//        return supportedOptions;
//    }
//
//    public Collection<String> supportedAnnotationTypes() {
//        return supportedAnnotations;
//    }
//
//    /*
//     * Return an instance of the {@link com.sun.istack.internal.ws.WSAP WSAP} AnnotationProcesor.
//     */
//    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds,
//                                        AnnotationProcessorEnvironment apEnv) {
//
//        if (wsAP == null) {
//            AnnotationProcessorContext context = new AnnotationProcessorContext();
//            wsAP = new WebServiceAP(null, context, null, null);
//
//        }
//        wsAP.init(apEnv);
//        return wsAP;
//    }
//}
