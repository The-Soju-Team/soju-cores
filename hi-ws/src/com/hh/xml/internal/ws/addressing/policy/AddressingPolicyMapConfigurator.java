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

import com.hh.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.policy.AssertionSet;
import com.hh.xml.internal.ws.policy.Policy;
import com.hh.xml.internal.ws.policy.PolicyAssertion;
import com.hh.xml.internal.ws.policy.PolicyException;
import com.hh.xml.internal.ws.policy.PolicyMap;
import com.hh.xml.internal.ws.policy.PolicySubject;
import com.hh.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.hh.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.hh.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.hh.xml.internal.ws.policy.subject.WsdlBindingSubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import com.hh.webservice.ws.soap.AddressingFeature;

/**
 * Generate an addressing policy and updates the PolicyMap if AddressingFeature is enabled.
 *
 * @author Fabian Ritzmann
 * @author Rama Pulavarthi
 */
public class AddressingPolicyMapConfigurator implements PolicyMapConfigurator {

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);

    private static final class AddressingAssertion extends PolicyAssertion {
        /**
         * Creates an assertion with nested alternatives.
         *
         * @param assertionData
         * @param nestedAlternative
         */
        AddressingAssertion(AssertionData assertionData, final AssertionSet nestedAlternative) {
            super(assertionData, null, nestedAlternative);
        }

        /**
         * Creates an assertion with no nested alternatives.
         *
         * @param assertionData
         */
        AddressingAssertion(AssertionData assertionData) {
            super(assertionData, null, null);
        }
    }


    /**
     * Puts an addressing policy into the PolicyMap if the addressing feature was set.
     */
    public Collection<PolicySubject> update(final PolicyMap policyMap, final SEIModel model, final WSBinding wsBinding)
            throws PolicyException {
        LOGGER.entering(policyMap, model, wsBinding);

        Collection<PolicySubject> subjects = new ArrayList<PolicySubject>();
        if (policyMap != null) {
            final AddressingFeature addressingFeature = wsBinding.getFeature(AddressingFeature.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("addressingFeature = " + addressingFeature);
            }
            if ((addressingFeature != null) && addressingFeature.isEnabled()) {
                //add wsam:Addrressing assertion if not exists.
                addWsamAddressing(subjects, policyMap, model, addressingFeature);
            }
        } // endif policy map not null
        LOGGER.exiting(subjects);
        return subjects;
    }

    private void addWsamAddressing(Collection<PolicySubject> subjects, PolicyMap policyMap, SEIModel model, AddressingFeature addressingFeature)
            throws PolicyException {
        final QName bindingName = model.getBoundPortTypeName();
        final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
        final Policy addressingPolicy = createWsamAddressingPolicy(bindingName, addressingFeature);
        final PolicySubject addressingPolicySubject = new PolicySubject(wsdlSubject, addressingPolicy);
        subjects.add(addressingPolicySubject);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Added addressing policy with ID \"" + addressingPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
        }
    }

    /**
     * Create a policy with an WSAM Addressing assertion.
     */
    private Policy createWsamAddressingPolicy(final QName bindingName, AddressingFeature af) {
        final ArrayList<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        final ArrayList<PolicyAssertion> assertions = new ArrayList<PolicyAssertion>(1);
        final AssertionData addressingData =
                AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
        if (!af.isRequired()) {
            addressingData.setOptionalAttribute(true);
        }
        try {
            AddressingFeature.Responses responses = af.getResponses();
            if (responses == AddressingFeature.Responses.ANONYMOUS) {
                AssertionData nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                PolicyAssertion nestedAsser = new AddressingAssertion(nestedAsserData, null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            } else if (responses == AddressingFeature.Responses.NON_ANONYMOUS) {
                final AssertionData nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                PolicyAssertion nestedAsser = new AddressingAssertion(nestedAsserData, null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            } else {
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
            }
        } catch (NoSuchMethodError e) {
            //If JAX-WS 2.2 API is really required, it would been reported in @Addressing or wsam:Addressing processing
            //Don't add any nested assertion to mimic the 2.1 behavior
            assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
        }
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_WSAM_Addressing_Policy", assertionSets);
    }
}
