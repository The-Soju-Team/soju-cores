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

package com.hh.xml.internal.ws.encoding.policy;

import com.hh.xml.internal.ws.policy.AssertionSet;
import com.hh.xml.internal.ws.policy.Policy;
import com.hh.xml.internal.ws.policy.PolicyAssertion;
import com.hh.xml.internal.ws.policy.PolicyException;
import com.hh.xml.internal.ws.policy.PolicyMap;
import com.hh.xml.internal.ws.policy.PolicyMapKey;
import com.hh.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.soap.MTOMFeature;

import static com.hh.xml.internal.ws.encoding.policy.EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION;

/**
 *
 * @author japod
 * @author Fabian Ritzmann
 */
public class MtomFeatureConfigurator implements PolicyFeatureConfigurator {
    /**
     * Creates a new instance of MtomFeatureConfigurator
     */
    public MtomFeatureConfigurator() {
    }

    /**
     * process Mtom policy assertions and if found and is not optional then mtom is enabled on the
     * {@link WSDLBoundPortType}
     *
     * @param key Key that identifies the endpoint scope
     * @param policyMap Must be non-null
     * @throws PolicyException If retrieving the policy triggered an exception
     */
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if ((key != null) && (policyMap != null)) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (null!=policy && policy.contains(OPTIMIZED_MIME_SERIALIZATION_ASSERTION)) {
                Iterator <AssertionSet> assertions = policy.iterator();
                while(assertions.hasNext()){
                    AssertionSet assertionSet = assertions.next();
                    Iterator<PolicyAssertion> policyAssertion = assertionSet.iterator();
                    while(policyAssertion.hasNext()){
                        PolicyAssertion assertion = policyAssertion.next();
                        if(OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(assertion.getName())){
                            features.add(new MTOMFeature(true));
                        } // end-if non optional mtom assertion found
                    } // next assertion
                } // next alternative
            } // end-if policy contains mtom assertion
        }
        return features;
    }
}
