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

package com.hh.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.api.WSFeatureList;
import com.hh.xml.internal.ws.api.FeatureConstructor;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.hh.xml.internal.ws.model.RuntimeModelerException;
import com.hh.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.hh.xml.internal.ws.resources.ModelerMessages;
import com.hh.xml.internal.bind.util.Which;

import com.hh.webservice.ws.RespectBinding;
import com.hh.webservice.ws.RespectBindingFeature;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.soap.Addressing;
import com.hh.webservice.ws.soap.AddressingFeature;
import com.hh.webservice.ws.soap.MTOM;
import com.hh.webservice.ws.soap.MTOMFeature;
import com.hh.webservice.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a list of {@link WebServiceFeature}s that has bunch of utility methods
 * pertaining to web service features.
 *
 * @author Rama Pulavarthi
 */
public final class WebServiceFeatureList implements WSFeatureList {
    private Map<Class<? extends WebServiceFeature>, WebServiceFeature> wsfeatures =
            new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();

    public WebServiceFeatureList() {
    }

    /**
     * Delegate to this parent if non-null.
     */
    private @Nullable WSDLFeaturedObject parent;

    public WebServiceFeatureList(@NotNull WebServiceFeature... features) {
        if (features != null)
            for (WebServiceFeature f : features) {
                wsfeatures.put(f.getClass(), f);
            }
    }

    /**
     * Creates a list by reading featuers from the annotation on a class.
     */
    public WebServiceFeatureList(@NotNull Class<?> endpointClass) {
        parseAnnotations(endpointClass);
    }

    /**
     * Adds the corresponding features to the list for feature annotations(i.e
     * which have {@link WebServiceFeatureAnnotation} meta annotation)
     *
     * @param annIt collection of annotations(that can have non-feature annotations)
     */
    public void parseAnnotations(Iterable<Annotation> annIt) {
        for(Annotation ann : annIt) {
            WebServiceFeature feature = getFeature(ann);
            if (feature != null) {
                add(feature);
            }
        }
    }

    /**
     * Returns a corresponding feature for a feature annotation(i.e which has
     * {@link WebServiceFeatureAnnotation} meta annotation)
     *
     * @return corresponding feature for the annotation
     *         null, if the annotation is nota feature annotation
     */
    public static WebServiceFeature getFeature(Annotation a) {
        WebServiceFeature ftr;
        if (!(a.annotationType().isAnnotationPresent(WebServiceFeatureAnnotation.class))) {
            ftr = null;
        } else if (a instanceof Addressing) {
            Addressing addAnn = (Addressing) a;
            try {
                ftr = new AddressingFeature(addAnn.enabled(), addAnn.required(),addAnn.responses());
            } catch(NoSuchMethodError e) {
                //throw error. We can't default to Responses.ALL as we dont know if the user has not used 2.2 annotation with responses.
                throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(Addressing.class))));
            }
        } else if (a instanceof MTOM) {
            MTOM mtomAnn = (MTOM) a;
            ftr = new MTOMFeature(mtomAnn.enabled(), mtomAnn.threshold());
        } else if (a instanceof RespectBinding) {
            RespectBinding rbAnn = (RespectBinding) a;
            ftr = new RespectBindingFeature(rbAnn.enabled());
        } else {
            ftr = getWebServiceFeatureBean(a);
        }
        return ftr;
    }

    /**
     * Reads {@link WebServiceFeatureAnnotation feature annotations} on a class
     * and adds them to the list.
     *
     * @param endpointClass web service impl class
     */
    public void parseAnnotations(Class<?> endpointClass) {
        for (Annotation a : endpointClass.getAnnotations()) {
            WebServiceFeature ftr = getFeature(a);
            if (ftr != null) {
                if (ftr instanceof MTOMFeature) {
                    // check conflict with @BindingType
                    BindingID bindingID = BindingID.parse(endpointClass);
                    MTOMFeature bindingMtomSetting = bindingID.createBuiltinFeatureList().get(MTOMFeature.class);
                    if (bindingMtomSetting != null && bindingMtomSetting.isEnabled() ^ ftr.isEnabled()) {
                        throw new RuntimeModelerException(
                            ModelerMessages.RUNTIME_MODELER_MTOM_CONFLICT(bindingID, ftr.isEnabled()));
                    }
                }
                add(ftr);
            }
        }
    }

    /**
     * Given the URL String inside jar, returns the URL to the jar itself.
     */
    private static String toJar(String url) {
        if(!url.startsWith("jar:"))
            return url;
        url = url.substring(4); // cut off jar:
        return url.substring(0,url.lastIndexOf('!'));    // cut off everything after '!'
    }

    private static WebServiceFeature getWebServiceFeatureBean(Annotation a) {
        WebServiceFeatureAnnotation wsfa = a.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
        Class<? extends WebServiceFeature> beanClass = wsfa.bean();
        WebServiceFeature bean;

        Constructor ftrCtr = null;
        String[] paramNames = null;
        for (Constructor con : beanClass.getConstructors()) {
            FeatureConstructor ftrCtrAnn = (FeatureConstructor) con.getAnnotation(FeatureConstructor.class);
            if (ftrCtrAnn != null) {
                if (ftrCtr == null) {
                    ftrCtr = con;
                    paramNames = ftrCtrAnn.value();
                } else {
                    throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(a, beanClass));
                }
            }
        }
        if (ftrCtr == null) {
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(a, beanClass));
        }
        if (ftrCtr.getParameterTypes().length != paramNames.length) {
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(a, beanClass));
        }

        try {
            Object[] params = new Object[paramNames.length];
            for (int i = 0; i < paramNames.length; i++) {
                Method m = a.annotationType().getDeclaredMethod(paramNames[i]);
                params[i] = m.invoke(a);
            }
            bean = (WebServiceFeature) ftrCtr.newInstance(params);
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
        return bean;
    }

    public Iterator<WebServiceFeature> iterator() {
        if (parent != null)
            return new MergedFeatures(parent.getFeatures());
        return wsfeatures.values().iterator();
    }

    public
    @NotNull
    WebServiceFeature[] toArray() {
        if (parent != null)
            return new MergedFeatures(parent.getFeatures()).toArray();
        return wsfeatures.values().toArray(new WebServiceFeature[]{});
    }

    public boolean isEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
        WebServiceFeature ftr = get(feature);
        return ftr != null && ftr.isEnabled();
    }


    public
    @Nullable
            <F extends WebServiceFeature> F get(@NotNull Class<F> featureType) {
        WebServiceFeature f = featureType.cast(wsfeatures.get(featureType));
        if (f == null && parent != null) {
            return parent.getFeatures().get(featureType);
        }
        return (F) f;
    }

    /**
     * Adds a feature to the list if it's not already added.
     */
    public void add(@NotNull WebServiceFeature f) {
        if (!wsfeatures.containsKey(f.getClass())) {
            wsfeatures.put(f.getClass(), f);
        }
    }

    /**
     * Adds features to the list if it's not already added.
     */
    public void addAll(@NotNull WSFeatureList list) {
        for (WebServiceFeature f : list)
            add(f);
    }

    /**
     * Merges the extra features that are not already set on binding.
     * i.e, if a feature is set already on binding through someother API
     * the coresponding wsdlFeature is not set.
     *
     * @param features          Web Service features that need to be merged with already configured features.
     * @param reportConflicts   If true, checks if the feature setting in WSDL (wsdl extension or
     *                          policy configuration) colflicts with feature setting in Deployed Service and
     *                          logs warning if there are any conflicts.
     */
    public void mergeFeatures(@NotNull Iterable<WebServiceFeature> features, boolean reportConflicts) {
        for (WebServiceFeature wsdlFtr : features) {
            if (get(wsdlFtr.getClass()) == null) {
                add(wsdlFtr);
            } else if (reportConflicts) {
                if (isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
                    LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(
                            get(wsdlFtr.getClass()), wsdlFtr));
                }
            }
        }
    }

        public void mergeFeatures(WebServiceFeature[] features, boolean reportConflicts) {
        for (WebServiceFeature wsdlFtr : features) {
            if (get(wsdlFtr.getClass()) == null) {
                add(wsdlFtr);
            } else if (reportConflicts) {
                if (isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
                    LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(
                            get(wsdlFtr.getClass()), wsdlFtr));
                }
            }
        }
        }

    /**
     * Set the parent features. Basically the parent feature list will be overriden
     * by this feature list.
     */
    public void setParentFeaturedObject(@NotNull WSDLFeaturedObject parent) {
        this.parent = parent;
    }

    public static @Nullable <F extends WebServiceFeature> F getFeature(@NotNull WebServiceFeature[] features, @NotNull Class<F> featureType) {
        for(WebServiceFeature f : features) {
            if (f.getClass() == featureType)
                return (F)f;
        }
        return null;
    }

    /**
     * A Union of this WebServiceFeatureList and the parent.
     */
    private final class MergedFeatures implements Iterator<WebServiceFeature> {
        private final Stack<WebServiceFeature> features = new Stack<WebServiceFeature>();

        public MergedFeatures(@NotNull WSFeatureList parent) {

            for (WebServiceFeature f : wsfeatures.values()) {
                features.push(f);
            }

            for (WebServiceFeature f : parent) {
                if (!wsfeatures.containsKey(f.getClass())) {
                    features.push(f);
                }
            }
        }

        public boolean hasNext() {
            return !features.empty();
        }

        public WebServiceFeature next() {
            if (!features.empty()) {
                return features.pop();
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (!features.empty()) {
                features.pop();
            }
        }

        public WebServiceFeature[] toArray() {
            return features.toArray(new WebServiceFeature[]{});
        }
    }

    private static final Logger LOGGER = Logger.getLogger(WebServiceFeatureList.class.getName());
}
