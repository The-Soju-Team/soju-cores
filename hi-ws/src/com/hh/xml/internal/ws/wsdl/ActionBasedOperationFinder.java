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

package com.hh.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.model.SEIModel;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.hh.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.hh.xml.internal.ws.api.addressing.AddressingVersion;
import com.hh.xml.internal.ws.api.message.HeaderList;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Messages;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.model.AbstractSEIModelImpl;
import com.hh.xml.internal.ws.model.JavaMethodImpl;
import static com.hh.xml.internal.ws.wsdl.PayloadQNameBasedOperationFinder.*;
import com.hh.xml.internal.ws.resources.AddressingMessages;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * An {@link WSDLOperationFinder} implementation that uses
 * WS-Addressing Action Message Addressing Property, <code>wsa:Action</code> and SOAP Payload QName,
 * as the key for dispatching.
 * <p/>
 * This should be used only when AddressingFeature is enabled.
 * A map of all {@link ActionBasedOperationSignature}s in the port and the corresponding and the WSDL Operation QNames
 * is maintained.
 * <p/>
 *
 * @author Rama Pulavarthi
 */
final class ActionBasedOperationFinder extends WSDLOperationFinder {

    private static final Logger LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
    private final Map<ActionBasedOperationSignature, QName> uniqueOpSignatureMap;
    private final Map<String, QName> actionMap;

    private final @NotNull AddressingVersion av;

    public ActionBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);

        assert binding.getAddressingVersion() != null;    // this dispatcher can be only used when addressing is on.
        av = binding.getAddressingVersion();
        uniqueOpSignatureMap = new HashMap<ActionBasedOperationSignature, QName>();
        actionMap = new HashMap<String,QName>();

        if (seiModel != null) {
            for (JavaMethodImpl m : ((AbstractSEIModelImpl) seiModel).getJavaMethods()) {
                if(m.getMEP().isAsync)
                    continue;

                String action = m.getInputAction();
                QName payloadName = m.getRequestPayloadName();
                if (payloadName == null)
                    payloadName = EMPTY_PAYLOAD;
                //first look at annotations and then in wsdlmodel
                if (action == null || action.equals("")) {
                    action = m.getOperation().getOperation().getInput().getAction();

                }
                if (action != null) {
                    ActionBasedOperationSignature opSignature = new ActionBasedOperationSignature(action, payloadName);
                    if(uniqueOpSignatureMap.get(opSignature) != null) {
                        LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(
                                uniqueOpSignatureMap.get(opSignature),m.getOperation().getName(),action,payloadName));
                    }
                    uniqueOpSignatureMap.put(opSignature, m.getOperation().getName());
                    actionMap.put(action,m.getOperation().getName());
                }
            }
        } else {
            for (WSDLBoundOperation wsdlOp : wsdlModel.getBinding().getBindingOperations()) {
                QName payloadName = wsdlOp.getReqPayloadName();
                if (payloadName == null)
                    payloadName = EMPTY_PAYLOAD;
                String action = wsdlOp.getOperation().getInput().getAction();
                ActionBasedOperationSignature opSignature = new ActionBasedOperationSignature(
                        action, payloadName);
                if(uniqueOpSignatureMap.get(opSignature) != null) {
                    LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(
                                    uniqueOpSignatureMap.get(opSignature),wsdlOp.getName(),action,payloadName));

                }
                uniqueOpSignatureMap.put(opSignature, wsdlOp.getName());
                actionMap.put(action,wsdlOp.getName());
            }
        }
    }

    /**
     *
     * @param request  Request Packet that is used to find the associated WSDLOperation
     * @return WSDL operation Qname.
     *         return null if WS-Addressing is not engaged.
     * @throws DispatchException with WSA defined fault message when it cannot find an associated WSDL operation.
     *
     */
    @Override
    public QName getWSDLOperationQName(Packet request) throws DispatchException {
        HeaderList hl = request.getMessage().getHeaders();
        String action = hl.getAction(av, binding.getSOAPVersion());

        if (action == null)
            // Addressing is not enagaged, return null to use other ways to dispatch.
            return null;

        Message message = request.getMessage();
        QName payloadName;
        String localPart = message.getPayloadLocalPart();
        if (localPart == null) {
            payloadName = EMPTY_PAYLOAD;
        } else {
            String nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null)
                nsUri = EMPTY_PAYLOAD_NSURI;
            payloadName = new QName(nsUri, localPart);
        }

        QName opName = uniqueOpSignatureMap.get(new ActionBasedOperationSignature(action, payloadName));
        if (opName != null)
            return opName;

        //Seems like in Wstrust STS wsdls, the payload does not match what is specified in the wsdl leading to incorrect
        //  wsdl operation resolution. Use just wsa:Action to dispatch as a last resort.
        //try just with wsa:Action
        opName = actionMap.get(action);
        if (opName != null)
            return opName;

        // invalid action header
        Message result = Messages.create(action, av, binding.getSOAPVersion());

        throw new DispatchException(result);

    }


}
