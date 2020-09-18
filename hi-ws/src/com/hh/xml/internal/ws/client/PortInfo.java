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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.BindingID;
import com.hh.xml.internal.ws.api.EndpointAddress;
import com.hh.xml.internal.ws.api.WSService;
import com.hh.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.hh.xml.internal.ws.api.policy.PolicyResolver;
import com.hh.xml.internal.ws.api.client.WSPortInfo;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.binding.BindingImpl;
import com.hh.xml.internal.ws.binding.WebServiceFeatureList;
import com.hh.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.hh.xml.internal.ws.model.wsdl.WSDLModelImpl;
import com.hh.xml.internal.ws.policy.PolicyMap;
import com.hh.xml.internal.ws.policy.jaxws.PolicyUtil;

import javax.xml.namespace.QName;
import com.hh.webservice.ws.WebServiceFeature;

/**
 * Information about a port.
 * <p/>
 * This object is owned by {@link WSServiceDelegate} to keep track of a port,
 * since a port maybe added dynamically.
 *
 * @author JAXWS Development Team
 */
public class PortInfo implements WSPortInfo {
    private final @NotNull WSServiceDelegate owner;

    public final @NotNull QName portName;
    public final @NotNull EndpointAddress targetEndpoint;
    public final @NotNull BindingID bindingId;

    public final @NotNull PolicyMap policyMap;
    /**
     * If a port is known statically to a WSDL, {@link PortInfo} may
     * have the corresponding WSDL model. This would occur when the
     * service was created with the WSDL location and the port is defined
     * in the WSDL.
     * <p/>
     * If this is a {@link SEIPortInfo}, then this is always non-null.
     */
    public final @Nullable WSDLPort portModel;

    public PortInfo(WSServiceDelegate owner, EndpointAddress targetEndpoint, QName name, BindingID bindingId) {
        this.owner = owner;
        this.targetEndpoint = targetEndpoint;
        this.portName = name;
        this.bindingId = bindingId;
        this.portModel = getPortModel(owner, name);
        this.policyMap = createPolicyMap();

    }

    public PortInfo(@NotNull WSServiceDelegate owner, @NotNull WSDLPort port) {
        this.owner = owner;
        this.targetEndpoint = port.getAddress();
        this.portName = port.getName();
        this.bindingId = port.getBinding().getBindingId();
        this.portModel = port;
        this.policyMap = createPolicyMap();
    }

    public PolicyMap getPolicyMap() {
        return policyMap;
    }

    public PolicyMap createPolicyMap() {
       PolicyMap map;
       if(portModel != null) {
            map = ((WSDLModelImpl) portModel.getOwner().getParent()).getPolicyMap();
       } else {
           map = PolicyResolverFactory.create().resolve(new PolicyResolver.ClientContext(null,owner.getContainer()));
       }
       //still map is null, create a empty map
       if(map == null)
           map = PolicyMap.createPolicyMap(null);
       return map;
    }
    /**
     * Creates {@link BindingImpl} for this {@link PortInfo}.
     *
     * @param webServiceFeatures
     *      User-specified features.
     * @param portInterface
     *      Null if this is for dispatch. Otherwise the interface the proxy is going to implement
     * @return
     *      The initialized BindingImpl
     */
    public BindingImpl createBinding(WebServiceFeature[] webServiceFeatures, Class<?> portInterface) {
        WebServiceFeatureList r = new WebServiceFeatureList(webServiceFeatures);

        Iterable<WebServiceFeature> configFeatures;

        //TODO incase of Dispatch, provide a way to User for complete control of the message processing by giving
        // ability to turn off the WSDL/Policy based features and its associated tubes.

        //Even in case of Dispatch, merge all features configured via WSDL/Policy or deployment configuration
        if (portModel != null) {
            // could have merged features from this.policyMap, but some features are set in WSDLModel which are not there in PolicyMap
            // for ex: <wsaw:UsingAddressing> wsdl extn., and since the policyMap features are merged into WSDLModel anyway during postFinished(),
            // So, using here WsdlModel for merging is right.

            // merge features from WSDL
            configFeatures = portModel.getFeatures();
        } else {
            configFeatures = PolicyUtil.getPortScopedFeatures(policyMap, owner.getServiceName(),portName);
        }
        r.mergeFeatures(configFeatures, false);

        // merge features from interceptor
        r.mergeFeatures(owner.serviceInterceptor.preCreateBinding(this,portInterface,r), false);

        BindingImpl bindingImpl = BindingImpl.create(bindingId, r.toArray());
        owner.getHandlerConfigurator().configureHandlers(this,bindingImpl);

        return bindingImpl;
    }

    //This method is used for Dispatch client only
    private WSDLPort getPortModel(WSServiceDelegate owner, QName portName) {

        if (owner.getWsdlService() != null){
            Iterable<WSDLPortImpl> ports = owner.getWsdlService().getPorts();
            for (WSDLPortImpl port : ports){
                if (port.getName().equals(portName))
                    return port;
            }
        }
        return null;
    }

//
// implementation of API PortInfo interface
//

    @Nullable
    public WSDLPort getPort() {
        return portModel;
    }

    @NotNull
    public WSService getOwner() {
        return owner;
    }

    @NotNull
    public BindingID getBindingId() {
        return bindingId;
    }

    @NotNull
    public EndpointAddress getEndpointAddress() {
        return targetEndpoint;
    }

    /**
     * @deprecated
     *      Only meant to be used via {@link javax.xml.ws.handler.PortInfo}.
     *      Use {@link WSServiceDelegate#getServiceName()}.
     */
    public QName getServiceName() {
        return owner.getServiceName();
    }

    /**
     * @deprecated
     *      Only meant to be used via {@link javax.xml.ws.handler.PortInfo}.
     *      Use {@link #portName}.
     */
    public QName getPortName() {
        return portName;
    }

    /**
     * @deprecated
     *      Only meant to be used via {@link javax.xml.ws.handler.PortInfo}.
     *      Use {@link #bindingId}.
     */
    public String getBindingID() {
        return bindingId.toString();
    }
}
