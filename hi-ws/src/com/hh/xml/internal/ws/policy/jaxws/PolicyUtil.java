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

package com.hh.xml.internal.ws.policy.jaxws;

import com.hh.xml.internal.ws.addressing.policy.AddressingFeatureConfigurator;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLService;
import com.hh.xml.internal.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.hh.xml.internal.ws.encoding.policy.MtomFeatureConfigurator;
import com.hh.xml.internal.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.hh.xml.internal.ws.policy.PolicyException;
import com.hh.xml.internal.ws.policy.PolicyMap;
import com.hh.xml.internal.ws.policy.PolicyMapKey;
import com.hh.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.hh.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.hh.xml.internal.ws.util.ServiceFinder;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.WebServiceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Rama Pulavarthi
 * @author Fabian Ritzmann
 */
public class PolicyUtil {

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
    private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS =
            new LinkedList<PolicyFeatureConfigurator>();

    static {
        // Add feature configurators that are already built into JAX-WS
        CONFIGURATORS.add(new AddressingFeatureConfigurator());
        CONFIGURATORS.add(new MtomFeatureConfigurator());
        CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
        CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());

        // Dynamically discover remaining feature configurators
        addServiceProviders(CONFIGURATORS, PolicyFeatureConfigurator.class);
    }

    /**
     * Adds the dynamically discovered implementations for the given service class
     * to the given collection.
     *
     * @param <T> The type of the service class.
     * @param providers The discovered implementations are added to this collection.
     * @param service The service interface.
     */
    public static <T> void addServiceProviders(Collection<T> providers, Class<T> service) {
        final Iterator<T> foundProviders = ServiceFinder.find(service).iterator();
        while (foundProviders.hasNext()) {
            providers.add(foundProviders.next());
        }
    }

    /**
     * Iterates through the ports in the WSDL model, for each policy in the policy
     * map that is attached at endpoint scope computes a list of corresponding
     * WebServiceFeatures and sets them on the port.
     *
     * @param model The WSDL model
     * @param policyMap The policy map
     * @throws PolicyException If the list of WebServiceFeatures could not be computed
     */
    public static void configureModel(final WSDLModel model, PolicyMap policyMap) throws PolicyException {
        LOGGER.entering(model, policyMap);
        for (WSDLService service : model.getServices().values()) {
            for (WSDLPort port : service.getPorts()) {
                final Collection<WebServiceFeature> features = getPortScopedFeatures(policyMap, service.getName(), port.getName());
                for (WebServiceFeature feature : features) {
                    port.addFeature(feature);
                    port.getBinding().addFeature(feature);
                }
            }
        }
        LOGGER.exiting();
    }

    /**
     * Returns the list of features that correspond to the policies in the policy
     * map for a give port
     *
     * @param policyMap The service policies
     * @param serviceName The service name
     * @param portName The service port name
     * @return List of features for the given port corresponding to the policies in the map
     */
    public static Collection<WebServiceFeature> getPortScopedFeatures(PolicyMap policyMap, QName serviceName, QName portName) {
        LOGGER.entering(policyMap, serviceName, portName);
        Collection<WebServiceFeature> features = new ArrayList<WebServiceFeature>();
        try {
            final PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
            for (PolicyFeatureConfigurator configurator : CONFIGURATORS) {
                Collection<WebServiceFeature> additionalFeatures = configurator.getFeatures(key, policyMap);
                if (additionalFeatures != null) {
                    features.addAll(additionalFeatures);
                }
            }
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }
        LOGGER.exiting(features);
        return features;
    }

}
