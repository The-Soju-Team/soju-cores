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

package com.hh.xml.internal.ws.transport.http;


import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.hh.xml.internal.ws.api.PropertySet;
import com.hh.xml.internal.ws.api.ha.HaInfo;
import com.hh.xml.internal.ws.api.ha.StickyFeature;
import com.hh.xml.internal.ws.api.message.ExceptionHasMessage;
import com.hh.xml.internal.ws.api.message.Message;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.pipe.Codec;
import com.hh.xml.internal.ws.api.pipe.ContentType;
import com.hh.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import com.hh.xml.internal.ws.api.server.Adapter;
import com.hh.xml.internal.ws.api.server.BoundEndpoint;
import com.hh.xml.internal.ws.api.server.DocumentAddressResolver;
import com.hh.xml.internal.ws.api.server.EndpointComponent;
import com.hh.xml.internal.ws.api.server.Module;
import com.hh.xml.internal.ws.api.server.PortAddressResolver;
import com.hh.xml.internal.ws.api.server.SDDocument;
import com.hh.xml.internal.ws.api.server.ServiceDefinition;
import com.hh.xml.internal.ws.api.server.TransportBackChannel;
import com.hh.xml.internal.ws.api.server.WSEndpoint;
import com.hh.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.hh.xml.internal.ws.resources.WsservletMessages;
import com.hh.xml.internal.ws.server.UnsupportedMediaException;
import com.hh.xml.internal.ws.util.ByteArrayBuffer;
import com.hh.xml.internal.ws.util.Pool;

import com.hh.webservice.ws.Binding;
import com.hh.webservice.ws.WebServiceException;
import com.hh.webservice.ws.WebServiceFeature;
import com.hh.webservice.ws.http.HTTPBinding;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link Adapter} that receives messages in HTTP.
 *
 * <p>
 * This object also assigns unique query string (such as "xsd=1") to
 * each {@link SDDocument} so that they can be served by HTTP GET requests.
 *
 * @author Kohsuke Kawaguchi
 * @author Jitendra Kotamraju
 */
public class HttpAdapter extends Adapter<HttpAdapter.HttpToolkit> {

    /**
     * {@link SDDocument}s keyed by the query string like "?abc".
     * Used for serving documents via HTTP GET.
     *
     * Empty if the endpoint doesn't have {@link ServiceDefinition}.
     * Read-only.
     */
    private Map<String,SDDocument> wsdls;

    /**
     * Reverse map of {@link #wsdls}. Read-only.
     */
    private Map<SDDocument,String> revWsdls;

    /**
     * A reference to the service definition from which the map of wsdls/revWsdls
     * was created. This allows us to establish if the service definition documents
     * have changed in the meantime.
     */
    private ServiceDefinition serviceDefinition = null;

    public final HttpAdapterList<? extends HttpAdapter> owner;

    /**
     * Servlet URL pattern with which this {@link HttpAdapter} is associated.
     */
    public final String urlPattern;

    protected boolean stickyCookie;


    /**
     * Creates a lone {@link HttpAdapter} that does not know of any other
     * {@link HttpAdapter}s.
     *
     * This is convenient for creating an {@link HttpAdapter} for an environment
     * where they don't know each other (such as JavaSE deployment.)
     *
     * @param endpoint web service endpoint
     * @return singe adapter to process HTTP messages
     */
    public static HttpAdapter createAlone(WSEndpoint endpoint) {
        return new DummyList().createAdapter("","",endpoint);
    }

    /**
     * @deprecated
     *      remove as soon as we can update the test util.
     * @param endpoint web service endpoint
     * @param owner list of related adapters
     */
    protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner) {
        this(endpoint,owner,null);
    }

    protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner, String urlPattern) {
        super(endpoint);
        this.owner = owner;
        this.urlPattern = urlPattern;

        initWSDLMap(endpoint.getServiceDefinition());
    }

    /**
     * Return the last known service definition of the endpoint.
     *
     * @return The service definition of the endpoint
     */
    public ServiceDefinition getServiceDefinition() {
        return this.serviceDefinition;
    }

    /**
     * Fill in WSDL map.
     *
     * @param sdef service definition
     */
    public void initWSDLMap(ServiceDefinition sdef) {
        this.serviceDefinition = sdef;
        if(sdef==null) {
            wsdls = Collections.emptyMap();
            revWsdls = Collections.emptyMap();
        } else {
            wsdls = new HashMap<String, SDDocument>();  // wsdl=1 --> Doc
            // Sort WSDL, Schema documents based on SystemId so that the same
            // document gets wsdl=x mapping
            Map<String, SDDocument> systemIds = new TreeMap<String, SDDocument>();
            for (SDDocument sdd : sdef) {
                if (sdd == sdef.getPrimary()) { // No sorting for Primary WSDL
                    wsdls.put("wsdl", sdd);
                    wsdls.put("WSDL", sdd);
                } else {
                    systemIds.put(sdd.getURL().toString(), sdd);
                }
            }

            int wsdlnum = 1;
            int xsdnum = 1;
            for (Map.Entry<String, SDDocument> e : systemIds.entrySet()) {
                SDDocument sdd = e.getValue();
                if (sdd.isWSDL()) {
                    wsdls.put("wsdl="+(wsdlnum++),sdd);
                }
                if (sdd.isSchema()) {
                    wsdls.put("xsd="+(xsdnum++),sdd);
                }
            }

            revWsdls = new HashMap<SDDocument,String>();    // Doc --> wsdl=1
            for (Entry<String,SDDocument> e : wsdls.entrySet()) {
                if (!e.getKey().equals("WSDL")) {           // map Doc --> wsdl, not WSDL
                    revWsdls.put(e.getValue(),e.getKey());
                }
            }
        }
    }

    /**
     * Returns the "/abc/def/ghi" portion if
     * the URL pattern is "/abc/def/ghi/*".
     */
    public String getValidPath() {
        if (urlPattern.endsWith("/*")) {
            return urlPattern.substring(0, urlPattern.length() - 2);
        } else {
            return urlPattern;
        }
    }

    protected HttpToolkit createToolkit() {
        return new HttpToolkit();
    }

    /**
     * Receives the incoming HTTP connection and dispatches
     * it to JAX-WS. This method returns when JAX-WS completes
     * processing the request and the whole reply is written
     * to {@link WSHTTPConnection}.
     *
     * <p>
     * This method is invoked by the lower-level HTTP stack,
     * and "connection" here is an HTTP connection.
     *
     * <p>
     * To populate a request {@link Packet} with more info,
     * define {@link PropertySet.Property properties} on
     * {@link WSHTTPConnection}.
     *
     * @param connection to receive/send HTTP messages for web service endpoints
     * @throws IOException when I/O errors happen
     */
    public void handle(@NotNull WSHTTPConnection connection) throws IOException {
        if (handleGet(connection)) {
            return;
        }

        // Make sure the Toolkit is recycled by the same pool instance from which it was taken
        final Pool<HttpToolkit> currentPool = getPool();
        // normal request handling
        final HttpToolkit tk = currentPool.take();
        try {
            tk.handle(connection);
        } finally {
            currentPool.recycle(tk);
        }
    }

    public boolean handleGet(@NotNull WSHTTPConnection connection) throws IOException {
        if (connection.getRequestMethod().equals("GET")) {
            // metadata query. let the interceptor run
            for (EndpointComponent c : endpoint.getComponentRegistry()) {
                HttpMetadataPublisher spi = c.getSPI(HttpMetadataPublisher.class);
                if (spi != null && spi.handleMetadataRequest(this, connection))
                    return true; // handled
            }

            if (isMetadataQuery(connection.getQueryString())) {
                // Sends published WSDL and schema documents as the default action.
                publishWSDL(connection);
                return true;
            }

            Binding binding = getEndpoint().getBinding();
            if (!(binding instanceof HTTPBinding)) {
                // Writes HTML page with all the endpoint descriptions
                writeWebServicesHtmlPage(connection);
                return true;
            }
        } else if (connection.getRequestMethod().equals("HEAD")) {
            connection.getInput().close();
            Binding binding = getEndpoint().getBinding();
            if (isMetadataQuery(connection.getQueryString())) {
                SDDocument doc = wsdls.get(connection.getQueryString());
                connection.setStatus(doc != null
                        ? HttpURLConnection.HTTP_OK
                        : HttpURLConnection.HTTP_NOT_FOUND);
                connection.getOutput().close();
                connection.close();
                return true;
            } else if (!(binding instanceof HTTPBinding)) {
                connection.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                connection.getOutput().close();
                connection.close();
                return true;
            }
            // Let the endpoint handle for HTTPBinding
        }

        return false;

    }
    /*
     *
     * @param con
     * @param codec
     * @return
     * @throws IOException
     *         ExceptionHasMessage exception that contains particular fault message
     *         UnsupportedMediaException to indicate to send 415 error code
     */
    private Packet decodePacket(@NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
        String ct = con.getRequestHeader("Content-Type");
        InputStream in = con.getInput();
        Packet packet = new Packet();
        packet.soapAction = fixQuotesAroundSoapAction(con.getRequestHeader("SOAPAction"));
        packet.wasTransportSecure = con.isSecure();
        packet.acceptableMimeTypes = con.getRequestHeader("Accept");
        packet.addSatellite(con);
        packet.transportBackChannel = new Oneway(con);
        packet.webServiceContextDelegate = con.getWebServiceContextDelegate();

        if (dump) {
            ByteArrayBuffer buf = new ByteArrayBuffer();
            buf.write(in);
            in.close();
            dump(buf, "HTTP request", con.getRequestHeaders());
            in = buf.newInputStream();
        }
        codec.decode(in, ct, packet);
        return packet;
    }

    /**
     * Some stacks may send non WS-I BP 1.2 conformant SoapAction.
     * Make sure SOAPAction is quoted as {@link Packet#soapAction} expectsa quoted soapAction value.
     *
     * @param soapAction SoapAction HTTP Header
     * @return quoted SOAPAction value
     */
    private String fixQuotesAroundSoapAction(String soapAction) {
        if(soapAction != null && (!soapAction.startsWith("\"") || !soapAction.endsWith("\"")) ) {
            LOGGER.warning("Received WS-I BP non-conformant Unquoted SoapAction HTTP header: "+ soapAction);
            String fixedSoapAction = soapAction;
            if(!soapAction.startsWith("\""))
                fixedSoapAction = "\"" + fixedSoapAction;
            if(!soapAction.endsWith("\""))
                fixedSoapAction = fixedSoapAction + "\"";
            return fixedSoapAction;
        }
        return soapAction;
    }


    private void encodePacket(@NotNull Packet packet, @NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
        if (con.isClosed()) {
            return;                 // Connection is already closed
        }
        Message responseMessage = packet.getMessage();
        addStickyCookie(con);
        addReplicaCookie(con, packet);
        if (responseMessage == null) {
            if (!con.isClosed()) {
                // set the response code if not already set
                // for example, 415 may have been set earlier for Unsupported Content-Type
                if (con.getStatus() == 0)
                    con.setStatus(WSHTTPConnection.ONEWAY);
                // close the response channel now
                try {
                    con.getOutput().close(); // no payload
                } catch (IOException e) {
                    throw new WebServiceException(e);
                }
            }
        } else {
            if (con.getStatus() == 0) {
                // if the appliation didn't set the status code,
                // set the default one.
                con.setStatus(responseMessage.isFault()
                        ? HttpURLConnection.HTTP_INTERNAL_ERROR
                        : HttpURLConnection.HTTP_OK);
            }

            ContentType contentType = codec.getStaticContentType(packet);
            if (contentType != null) {
                con.setContentTypeResponseHeader(contentType.getContentType());
                OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
                if (dump) {
                    ByteArrayBuffer buf = new ByteArrayBuffer();
                    codec.encode(packet, buf);
                    dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf.writeTo(os);
                } else {
                    codec.encode(packet, os);
                }
                os.close();
            } else {

                ByteArrayBuffer buf = new ByteArrayBuffer();
                contentType = codec.encode(packet, buf);
                con.setContentTypeResponseHeader(contentType.getContentType());
                if (dump) {
                    dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                }
                OutputStream os = con.getOutput();
                buf.writeTo(os);
                os.close();
            }
        }
    }

    /*
     * GlassFish Load-balancer plugin always add a header proxy-jroute on
     * request being send from load-balancer plugin to server
     *
     * JROUTE cookie need to be stamped in two cases
     * 1 : At the time of session creation. In this case, request will not have
     * any JROUTE cookie.
     * 2 : At the time of fail-over. In this case, value of proxy-jroute
     * header(will point to current instance) and JROUTE cookie(will point to
     * previous failed instance) will be different. This logic can be used
     * to determine fail-over scenario.
     */
    private void addStickyCookie(WSHTTPConnection con) {
        if (stickyCookie) {
            String proxyJroute = con.getRequestHeader("proxy-jroute");
            if (proxyJroute == null) {
                // Load-balancer plugin is not front-ending this instance
                return;
            }

            String jrouteId = con.getCookie("JROUTE");
            if (jrouteId == null || !jrouteId.equals(proxyJroute)) {
                // Initial request or failover
                con.setCookie("JROUTE", proxyJroute);
            }
        }
    }

    private void addReplicaCookie(WSHTTPConnection con, Packet packet) {
        if (stickyCookie) {
            HaInfo haInfo = null;
            if (packet.supports(Packet.HA_INFO)) {
                haInfo = (HaInfo)packet.get(Packet.HA_INFO);
            }
            if (haInfo != null) {
                con.setCookie("METRO_KEY", haInfo.getKey());
                con.setCookie("JREPLICA", haInfo.getReplicaInstance());
            }
        }
    }

    public void invokeAsync(final WSHTTPConnection con) throws IOException {
        invokeAsync(con, NO_OP_COMPLETION_CALLBACK);
    }

    public void invokeAsync(final WSHTTPConnection con, final CompletionCallback callback) throws IOException {

            if (handleGet(con)) {
                callback.onCompletion();
                return;
            }
            final Pool<HttpToolkit> currentPool = getPool();
            final HttpToolkit tk = currentPool.take();
            final Packet request;

            try {

                request = decodePacket(con, tk.codec);
            } catch (ExceptionHasMessage e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                Packet response = new Packet();
                response.setMessage(e.getFaultMessage());
                encodePacket(response, con, tk.codec);
                currentPool.recycle(tk);
                con.close();
                callback.onCompletion();
                return;
            } catch (UnsupportedMediaException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                Packet response = new Packet();
                con.setStatus(WSHTTPConnection.UNSUPPORTED_MEDIA);
                encodePacket(response, con, tk.codec);
                currentPool.recycle(tk);
                con.close();
                callback.onCompletion();
                return;
            }

            endpoint.process(request, new WSEndpoint.CompletionCallback() {
                public void onCompletion(@NotNull Packet response) {
                    try {
                        try {
                            encodePacket(response, con, tk.codec);
                        } catch (IOException ioe) {
                            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
                        }
                        currentPool.recycle(tk);
                    } finally {
                        con.close();
                        callback.onCompletion();

                    }
                }
            },null);

    }

    public static  final CompletionCallback NO_OP_COMPLETION_CALLBACK = new CompletionCallback() {

        public void onCompletion() {
            //NO-OP
        }
    };

    public interface CompletionCallback{
        void onCompletion();
    }

    final class AsyncTransport extends AbstractServerAsyncTransport<WSHTTPConnection> {

        public AsyncTransport() {
            super(endpoint);
        }

        public void handleAsync(WSHTTPConnection con) throws IOException {
            super.handle(con);
        }

        protected void encodePacket(WSHTTPConnection con, @NotNull Packet packet, @NotNull Codec codec) throws IOException {
            HttpAdapter.this.encodePacket(packet, con, codec);
        }

        protected @Nullable String getAcceptableMimeTypes(WSHTTPConnection con) {
            return null;
        }

        protected @Nullable TransportBackChannel getTransportBackChannel(WSHTTPConnection con) {
            return new Oneway(con);
        }

        protected @NotNull
        PropertySet getPropertySet(WSHTTPConnection con) {
            return con;
        }

        protected @NotNull WebServiceContextDelegate getWebServiceContextDelegate(WSHTTPConnection con) {
            return con.getWebServiceContextDelegate();
        }
    }

    final class Oneway implements TransportBackChannel {
        WSHTTPConnection con;
        boolean closed;

        Oneway(WSHTTPConnection con) {
            this.con = con;
        }
        public void close() {
            if (!closed) {
                closed = true;
                // close the response channel now
                if (con.getStatus() == 0) {
                    // if the appliation didn't set the status code,
                    // set the default one.
                    con.setStatus(WSHTTPConnection.ONEWAY);
                }

                try {
                    con.getOutput().close(); // no payload
                } catch (IOException e) {
                    throw new WebServiceException(e);
                }
                con.close();
            }
        }
    }

    final class HttpToolkit extends Adapter.Toolkit {
        public void handle(WSHTTPConnection con) throws IOException {
            try {
                boolean invoke = false;
                Packet packet;
                try {
                    packet = decodePacket(con, codec);
                    invoke = true;
                } catch(Exception e) {
                    packet = new Packet();
                    if (e instanceof ExceptionHasMessage) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        packet.setMessage(((ExceptionHasMessage)e).getFaultMessage());
                    } else if (e instanceof UnsupportedMediaException) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        con.setStatus(WSHTTPConnection.UNSUPPORTED_MEDIA);
                    } else {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        con.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                }
                if (invoke) {
                    try {
                        packet = head.process(packet, con.getWebServiceContextDelegate(),
                                packet.transportBackChannel);
                    } catch(Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        if (!con.isClosed()) {
                            writeInternalServerError(con);
                        }
                        return;
                    }
                }
                encodePacket(packet, con, codec);
            } finally {
                if (!con.isClosed()) {
                    con.close();
                }
            }
        }
    }

    /**
     * Returns true if the given query string is for metadata request.
     *
     * @param query
     *      String like "xsd=1" or "perhaps=some&amp;unrelated=query".
     *      Can be null.
     * @return true for metadata requests
     *         false for web service requests
     */
    private boolean isMetadataQuery(String query) {
        // we intentionally return true even if documents don't exist,
        // so that they get 404.
        return query != null && (query.equals("WSDL") || query.startsWith("wsdl") || query.startsWith("xsd="));
    }

    /**
     * Sends out the WSDL (and other referenced documents)
     * in response to the GET requests to URLs like "?wsdl" or "?xsd=2".
     *
     * @param con
     *      The connection to which the data will be sent.
     *
     * @throws IOException when I/O errors happen
     */
    public void publishWSDL(@NotNull WSHTTPConnection con) throws IOException {
        con.getInput().close();

        SDDocument doc = wsdls.get(con.getQueryString());
        if (doc == null) {
            writeNotFoundErrorPage(con,"Invalid Request");
            return;
        }

        con.setStatus(HttpURLConnection.HTTP_OK);
        con.setContentTypeResponseHeader("text/xml;charset=utf-8");

        OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);

        final PortAddressResolver portAddressResolver = owner.createPortAddressResolver(con.getBaseAddress());
        final String address = portAddressResolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
        assert address != null;
        DocumentAddressResolver resolver = new DocumentAddressResolver() {
            public String getRelativeAddressFor(@NotNull SDDocument current, @NotNull SDDocument referenced) {
                // the map on endpoint should account for all SDDocument
                assert revWsdls.containsKey(referenced);
                return address+'?'+ revWsdls.get(referenced);
            }
        };

        doc.writeTo(portAddressResolver, resolver, os);
        os.close();
    }

    /**
     * HTTP/1.0 connections require Content-Length. So just buffer to find out
     * the length.
     */
    private final static class Http10OutputStream extends ByteArrayBuffer {
        private final WSHTTPConnection con;

        Http10OutputStream(WSHTTPConnection con) {
            this.con = con;
        }

        @Override
        public void close() throws IOException {
            super.close();
            con.setContentLengthResponseHeader(size());
            OutputStream os = con.getOutput();
            writeTo(os);
            os.close();
        }
    }

    private void writeNotFoundErrorPage(WSHTTPConnection con, String message) throws IOException {
        con.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");

        PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(),"UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(message));
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    private void writeInternalServerError(WSHTTPConnection con) throws IOException {
        con.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        con.getOutput().close();        // Sets the status code
    }

    private static final class DummyList extends HttpAdapterList<HttpAdapter> {
        @Override
        protected HttpAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
            return new HttpAdapter(endpoint,this,urlPattern);
        }
    }

    private void dump(ByteArrayBuffer buf, String caption, Map<String, List<String>> headers) throws IOException {
        System.out.println("---["+caption +"]---");
        if (headers != null) {
            for (Entry<String, List<String>> header : headers.entrySet()) {
                if (header.getValue().isEmpty()) {
                    // I don't think this is legal, but let's just dump it,
                    // as the point of the dump is to uncover problems.
                    System.out.println(header.getValue());
                } else {
                    for (String value : header.getValue()) {
                        System.out.println(header.getKey() + ": " + value);
                    }
                }
            }
        }
        buf.writeTo(System.out);
        System.out.println("--------------------");
    }

    /*
     * Generates the listing of all services.
     */
    private void writeWebServicesHtmlPage(WSHTTPConnection con) throws IOException {
        if (!publishStatusPage) return;

        // TODO: resurrect the ability to localize according to the current request.

        con.getInput().close();

        // standard browsable page
        con.setStatus(WSHTTPConnection.OK);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");

        PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(),"UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        // out.println("Web Services");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        // out.println("<h1>Web Services</h1>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE_2());

        // what endpoints do we have in this system?
        Module module = getEndpoint().getContainer().getSPI(Module.class);
        List<BoundEndpoint> endpoints = Collections.emptyList();
        if(module!=null) {
            endpoints = module.getBoundEndpoints();
        }

        if (endpoints.isEmpty()) {
            // out.println("<p>No JAX-WS context information available.</p>");
            out.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
        } else {
            out.println("<table width='100%' border='1'>");
            out.println("<tr>");
            out.println("<td>");
            // out.println("Endpoint");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
            out.println("</td>");

            out.println("<td>");
            // out.println("Information");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
            out.println("</td>");
            out.println("</tr>");

            for (BoundEndpoint a : endpoints) {
                String endpointAddress = a.getAddress(con.getBaseAddress()).toString();
                out.println("<tr>");

                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(
                    a.getEndpoint().getServiceName(),
                    a.getEndpoint().getPortName()
                ));
                out.println("</td>");

                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(
                    endpointAddress,
                    a.getEndpoint().getImplementationClass().getName()
                ));
                out.println("</td>");

                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /**
     * Dumps what goes across HTTP transport.
     */
    public static boolean dump = false;

    public static boolean publishStatusPage = true;

    static {
        try {
            dump = Boolean.getBoolean(HttpAdapter.class.getName()+".dump");
        } catch( Throwable t ) {
            // OK to ignore this
        }
        try {
            publishStatusPage = System.getProperty(HttpAdapter.class.getName()+".publishStatusPage").equals("true");
        } catch( Throwable t ) {
            // OK to ignore this
        }
    }

    private static final Logger LOGGER = Logger.getLogger(HttpAdapter.class.getName());
}
