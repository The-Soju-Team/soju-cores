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

package com.hh.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.hh.xml.internal.ws.api.server.PortAddressResolver;
import com.hh.xml.internal.ws.transport.http.HttpAdapter;
import com.hh.xml.internal.ws.transport.http.WSHTTPConnection;
import com.hh.xml.internal.ws.developer.JAXWSProperties;
import com.hh.xml.internal.ws.resources.WsservletMessages;

import com.hh.webservice.ws.handler.MessageContext;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.spi.http.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * {@link WSHTTPConnection} used with Java SE endpoints. It provides connection
 * implementation using {@link HttpExchange} object.
 *
 * @author Jitendra Kotamraju
 */
final class PortableConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate {

    private final HttpExchange httpExchange;
    private int status;
    private final HttpAdapter adapter;
    private boolean outputWritten;

    public PortableConnectionImpl(@NotNull HttpAdapter adapter, @NotNull HttpExchange httpExchange) {
        this.adapter = adapter;
        this.httpExchange = httpExchange;
    }

    @Override
    @Property(value = {MessageContext.HTTP_REQUEST_HEADERS, Packet.INBOUND_TRANSPORT_HEADERS})
    public @NotNull Map<String,List<String>> getRequestHeaders() {
        return httpExchange.getRequestHeaders();
    }

    @Override
    public String getRequestHeader(String headerName) {
        return httpExchange.getRequestHeader(headerName);
    }

    @Override
    public void setResponseHeaders(Map<String,List<String>> headers) {
        Map<String, List<String>> r = httpExchange.getResponseHeaders();
        r.clear();
        for(Map.Entry <String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            // ignore headers that interfere with our correct operations
            if (!name.equalsIgnoreCase("Content-Length") && !name.equalsIgnoreCase("Content-Type")) {
                r.put(name,new ArrayList<String>(values));
            }
        }
    }

    @Override
    @Property({MessageContext.HTTP_RESPONSE_HEADERS,Packet.OUTBOUND_TRANSPORT_HEADERS})
    public Map<String,List<String>> getResponseHeaders() {
        return httpExchange.getResponseHeaders();
    }

    @Override
    public void setContentTypeResponseHeader(@NotNull String value) {
        httpExchange.addResponseHeader("Content-Type", value);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    @Property(MessageContext.HTTP_RESPONSE_CODE)
    public int getStatus() {
        return status;
    }

    public @NotNull InputStream getInput() throws IOException {
        return httpExchange.getRequestBody();
    }

    public @NotNull OutputStream getOutput() throws IOException {
        assert !outputWritten;
        outputWritten = true;

        httpExchange.setStatus(getStatus());
        return httpExchange.getResponseBody();
    }

    public @NotNull WebServiceContextDelegate getWebServiceContextDelegate() {
        return this;
    }

    public Principal getUserPrincipal(Packet request) {
        return httpExchange.getUserPrincipal();
    }

    public boolean isUserInRole(Packet request, String role) {
        return httpExchange.isUserInRole(role);
    }

    public @NotNull String getEPRAddress(Packet request, WSEndpoint endpoint) {
        PortAddressResolver resolver = adapter.owner.createPortAddressResolver(getBaseAddress());
        String address = resolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
        if(address==null)
            throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(endpoint.getPortName()));
        return address;
    }

    @Property(MessageContext.SERVLET_CONTEXT)
    public Object getServletContext() {
        return httpExchange.getAttribute(MessageContext.SERVLET_CONTEXT);
    }

    @Property(MessageContext.SERVLET_RESPONSE)
    public Object getServletResponse() {
        return httpExchange.getAttribute(MessageContext.SERVLET_RESPONSE);
    }

    @Property(MessageContext.SERVLET_REQUEST)
    public Object getServletRequest() {
        return httpExchange.getAttribute(MessageContext.SERVLET_REQUEST);
    }

    public String getWSDLAddress(@NotNull Packet request, @NotNull WSEndpoint endpoint) {
        String eprAddress = getEPRAddress(request,endpoint);
        if(adapter.getEndpoint().getPort() != null)
            return eprAddress+"?wsdl";
        else
            return null;
    }

    @Override
    public boolean isSecure() {
        return httpExchange.getScheme().equals("https");
    }

    @Override
    @Property(MessageContext.HTTP_REQUEST_METHOD)
    public @NotNull String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    @Override
    @Property(MessageContext.QUERY_STRING)
    public String getQueryString() {
        return httpExchange.getQueryString();
    }

    @Override
    @Property(MessageContext.PATH_INFO)
    public String getPathInfo() {
        return httpExchange.getPathInfo();
    }

    @Property(JAXWSProperties.HTTP_EXCHANGE)
    public HttpExchange getExchange() {
        return httpExchange;
    }

    @Override @NotNull
    public String getBaseAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(httpExchange.getScheme());
        sb.append("://");
        sb.append(httpExchange.getLocalAddress().getHostName());
        sb.append(":");
        sb.append(httpExchange.getLocalAddress().getPort());
        sb.append(httpExchange.getContextPath());
        return sb.toString();
    }

    @Override
    public String getProtocol() {
        return httpExchange.getProtocol();
    }

    @Override
    public void setContentLengthResponseHeader(int value) {
        httpExchange.addResponseHeader("Content-Length", ""+value);
    }

    protected PropertyMap getPropertyMap() {
        return model;
    }

    private static final PropertyMap model;

    static {
        model = parse(PortableConnectionImpl.class);
    }
}
