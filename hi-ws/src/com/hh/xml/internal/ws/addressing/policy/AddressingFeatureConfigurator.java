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

package com.hh.xml.internal.ws.addressing.policy;

import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.policy.AssertionSet;
import com.hh.xml.internal.ws.policy.NestedPolicy;
import com.hh.xml.internal.ws.policy.Policy;
import com.hh.xml.internal.ws.policy.PolicyAssertion;
import com.hh.xml.internal.ws.policy.PolicyException;
import com.hh.xml.internal.ws.policy.PolicyMap;
import com.hh.xml.internal.ws.policy.PolicyMapKey;
import com.hh.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.hh.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.hh.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.hh.xml.internal.ws.resources.ModelerMessages;
import com.hh.xml.internal.bind.util.Which;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.soap.AddressingFeature;

/**
 * This Policy extension configures the WSDLModel with AddressingFeature when Addressing assertions are present in the
 * PolicyMap.
 *
 * @author japod
 * @author Rama Pulavarthi
 */
public class AddressingFeatureConfigurator implements PolicyFeatureConfigurator {

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);

    private static final QName[] ADDRESSING_ASSERTIONS = {
        new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing")};

    /**
     * Creates a new instance of AddressingFeatureConfigurator
     */
    public AddressingFeatureConfigurator() {
    }

    public Collection<WebServiceFeature> getFeatures(final PolicyMapKey key, final PolicyMap policyMap) throws PolicyException {
        LOGGER.entering(key, policyMap);
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if ((key != null) && (policyMap != null)) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            for (QName addressingAssertionQName : ADDRESSING_ASSERTIONS) {
                if ((policy != null) && policy.contains(addressingAssertionQName)) {
                    final Iterator <AssertionSet> assertions = policy.iterator();
                    while(assertions.hasNext()){
                        final AssertionSet assertionSet = assertions.next();
                        final Iterator<PolicyAssertion> policyAssertion = assertionSet.iterator();
                        while(policyAssertion.hasNext()){
                            final PolicyAssertion assertion = policyAssertion.next();
                            if(assertion.getName().equals(addressingAssertionQName)){
                                final WebServiceFeature feature = AddressingVersion.getFeature(addressingAssertionQName.getNamespaceURI(), true, !assertion.isOptional());
                                if (LOGGER.isLoggable(Level.FINE)) {
                                    LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                                }
                                features.add(feature);
                            } // end-if non optional wsa assertion found
                        } // next assertion
                    } // next alternative
                } // end-if policy contains wsa assertion
            } //end foreach addr assertion

            // Deal with WS-Addressing 1.0 Metadata assertions
            if (policy != null && policy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                for (AssertionSet assertions : policy) {
                    for (PolicyAssertion assertion : assertions) {
                        if (assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                            NestedPolicy nestedPolicy = assertion.getNestedPolicy();
                            boolean requiresAnonymousResponses = false;
                            boolean requiresNonAnonymousResponses = false;
                            if (nestedPolicy != null) {
                                requiresAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                                requiresNonAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                            }
                            if(requiresAnonymousResponses && requiresNonAnonymousResponses) {
                                throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                            }

                            final WebServiceFeature feature;
                            try {
                                if (requiresAnonymousResponses) {
                                    feature = new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.ANONYMOUS);
                                } else if (requiresNonAnonymousResponses) {
                                    feature = new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.NON_ANONYMOUS);
                                } else {
                                    feature = new AddressingFeature(true, !assertion.isOptional());
                                }
                            } catch (NoSuchMethodError e) {
                                throw LOGGER.logSevereException(new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(AddressingFeature.class))), e));
                            }
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                            }
                            features.add(feature);
                        }
                    }
                }
            }
        }
        LOGGER.exiting(features);
        return features;
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
}
