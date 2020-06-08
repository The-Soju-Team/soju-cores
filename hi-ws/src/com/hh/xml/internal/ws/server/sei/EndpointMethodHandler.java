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

package com.hh.xml.internal.ws.server.sei;

import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.fault.SOAPFaultBuilder;
import com.hh.xml.internal.ws.message.jaxb.JAXBMessage;
import com.hh.xml.internal.ws.model.JavaMethodImpl;
import com.hh.xml.internal.ws.model.ParameterImpl;
import com.hh.xml.internal.ws.model.WrapperParameter;

import javax.jws.WebParam.Mode;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import com.hh.webservice.ws.Holder;
import com.hh.webservice.ws.ProtocolException;
import com.hh.webservice.ws.WebServiceException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p>
 * This class mainly performs the following two tasks:
 * <ol>
 *  <li>Takes a {@link Message] that represents a request,
 *      and extracts the arguments (and updates {@link Holder}s.)
 *  <li>Accepts return value and {@link Holder} arguments for a Java method,
 *      and creates {@link JAXBMessage} that represents a response message.
 * </ol>
 *
 * <h2>Creating {@link JAXBMessage}</h2>
 * <p>
 * At the construction time, we prepare {@link EndpointArgumentsBuilder} that knows how to create endpoint {@link Method}
 * invocation arguments.
 * we also prepare {@link EndpointResponseMessageBuilder} and {@link MessageFiller}s
 * that know how to move arguments into a {@link Message}.
 * Some arguments go to the payload, some go to headers, still others go to attachments.
 *
 * @author Jitendra Kotamraju
 */
final class EndpointMethodHandler {

    private final SOAPVersion soapVersion;
    private final Method method;
    private final int noOfArgs;
    private final JavaMethodImpl javaMethodModel;

     private final Boolean isOneWay;

    // Converts {@link Message} --> Object[]
    private final EndpointArgumentsBuilder argumentsBuilder;

    // these objects together create a response message from method parameters
    private final EndpointResponseMessageBuilder bodyBuilder;
    private final MessageFiller[] outFillers;

    private final SEIInvokerTube owner;

    public EndpointMethodHandler(SEIInvokerTube owner, JavaMethodImpl method, WSBinding binding) {
        this.owner = owner;
        this.soapVersion = binding.getSOAPVersion();
        this.method = method.getMethod();
        this.javaMethodModel = method;
        argumentsBuilder = createArgumentsBuilder();
        List<MessageFiller> fillers = new ArrayList<MessageFiller>();
        bodyBuilder = createResponseMessageBuilder(fillers);
        this.outFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
        this.noOfArgs = this.method.getParameterTypes().length;
    }

    /**
     * It builds EndpointArgumentsBuilder which converts request {@link Message} to endpoint method's invocation
     * arguments Object[]
     *
     * @return EndpointArgumentsBuilder
     */
    private EndpointArgumentsBuilder createArgumentsBuilder() {
        EndpointArgumentsBuilder argsBuilder;
        List<ParameterImpl> rp = javaMethodModel.getRequestParameters();
        List<EndpointArgumentsBuilder> builders = new ArrayList<EndpointArgumentsBuilder>();

        for( ParameterImpl param : rp ) {
            EndpointValueSetter setter = EndpointValueSetter.get(param);
            switch(param.getInBinding().kind) {
            case BODY:
                if(param.isWrapperStyle()) {
                    if(param.getParent().getBinding().isRpcLit())
                        builders.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)param));
                    else
                        builders.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)param, Mode.OUT));
                } else {
                    builders.add(new EndpointArgumentsBuilder.Body(param.getBridge(),setter));
                }
                break;
            case HEADER:
                builders.add(new EndpointArgumentsBuilder.Header(soapVersion, param, setter));
                break;
            case ATTACHMENT:
                builders.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
                break;
            case UNBOUND:
                builders.add(new EndpointArgumentsBuilder.NullSetter(setter,
                    EndpointArgumentsBuilder.getVMUninitializedValue(param.getTypeReference().type)));
                break;
            default:
                throw new AssertionError();
            }
        }

        // creates {@link Holder} arguments for OUT parameters
        List<ParameterImpl> resp = javaMethodModel.getResponseParameters();
        for( ParameterImpl param : resp ) {
            if (param.isWrapperStyle()) {
                WrapperParameter wp = (WrapperParameter)param;
                List<ParameterImpl> children = wp.getWrapperChildren();
                for (ParameterImpl p : children) {
                    if (p.isOUT() && p.getIndex() != -1) {
                        EndpointValueSetter setter = EndpointValueSetter.get(p);
                        builders.add(new EndpointArgumentsBuilder.NullSetter(setter, null));
                    }
                }
            } else if (param.isOUT() && param.getIndex() != -1) {
                EndpointValueSetter setter = EndpointValueSetter.get(param);
                builders.add(new EndpointArgumentsBuilder.NullSetter(setter, null));
            }
        }

        switch(builders.size()) {
        case 0:
            argsBuilder = EndpointArgumentsBuilder.NONE;
            break;
        case 1:
            argsBuilder = builders.get(0);
            break;
        default:
            argsBuilder = new EndpointArgumentsBuilder.Composite(builders);
        }
        return argsBuilder;
    }

    /**
    * prepare objects for creating response {@link Message}
    */
    private EndpointResponseMessageBuilder createResponseMessageBuilder(List<MessageFiller> fillers) {

        EndpointResponseMessageBuilder bodyBuilder = null;
        List<ParameterImpl> rp = javaMethodModel.getResponseParameters();

        for (ParameterImpl param : rp) {
            ValueGetter getter = ValueGetter.get(param);

            switch(param.getOutBinding().kind) {
            case BODY:
                if(param.isWrapperStyle()) {
                    if(param.getParent().getBinding().isRpcLit()) {
                        bodyBuilder = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)param,
                            soapVersion);
                    } else {
                        bodyBuilder = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)param,
                            soapVersion);
                    }
                } else {
                    bodyBuilder = new EndpointResponseMessageBuilder.Bare(param, soapVersion);
                }
                break;
            case HEADER:
                fillers.add(new MessageFiller.Header(param.getIndex(), param.getBridge(), getter ));
                break;
            case ATTACHMENT:
                fillers.add(MessageFiller.AttachmentFiller.createAttachmentFiller(param, getter));
                break;
            case UNBOUND:
                break;
            default:
                throw new AssertionError(); // impossible
            }
        }

        if (bodyBuilder == null) {
            // no parameter binds to body. we create an empty message
            switch(soapVersion) {
            case SOAP_11:
                bodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP11;
                break;
            case SOAP_12:
                bodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP12;
                break;
            default:
                throw new AssertionError();
            }
        }
        return bodyBuilder;
    }


    public Packet invoke(Packet req) {
        Message reqMsg = req.getMessage();
        Object[] args = new Object[noOfArgs];
        try {
            argumentsBuilder.readRequest(reqMsg,args);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        } catch (XMLStreamException e) {
            throw new WebServiceException(e);
        }
        // Some transports(like HTTP) may want to send response before envoking endpoint method
        // Doing this here so that after closing the response stream, cannot read
        // request from some transports(light weight http server)
        if (isOneWay && req.transportBackChannel != null) {
            req.transportBackChannel.close();
        }
        Message responseMessage;
        try {
            Object ret = owner.getInvoker(req).invoke(req, method, args);
            responseMessage = isOneWay ? null : createResponseMessage(args, ret);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (!(cause instanceof RuntimeException) && cause instanceof Exception) {
                // Service specific exception
                LOGGER.log(Level.FINE, cause.getMessage(), cause);
                responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion,
                        javaMethodModel.getCheckedException(cause.getClass()), cause);
            } else {
                if (cause instanceof ProtocolException) {
                    // Application code may be throwing it intentionally
                    LOGGER.log(Level.FINE, cause.getMessage(), cause);
                } else {
                    // Probably some bug in application code
                    LOGGER.log(Level.SEVERE, cause.getMessage(), cause);
                }
                responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, cause);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, e);
        }
        return req.createServerResponse(responseMessage, req.endpoint.getPort(), javaMethodModel.getOwner(), req.endpoint.getBinding());
    }

    /**
     * Creates a response {@link JAXBMessage} from method arguments, return value
     *
     * @return response message
     */
    private Message createResponseMessage(Object[] args, Object returnValue) {
        Message msg = bodyBuilder.createMessage(args, returnValue);

        for (MessageFiller filler : outFillers)
            filler.fillIn(args, returnValue, msg);

        return msg;
    }

    private static final Logger LOGGER = Logger.getLogger(EndpointMethodHandler.class.getName());
}
