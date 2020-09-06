/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.net.impl.httpserver;

import com.hh.net.httpserver.Filter;
import com.hh.net.httpserver.Headers;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;

/**
 * @author hiendm1
 */
public class RequestProcessor {

    Exchange exchange;
    Request req;
    int space;
    int start;
    String requestLine;
    String method;
    boolean newconnection;
    SSLEngine engine;
    SSLStreams sslStreams;

    RequestProcessor(Exchange exchange, Request req, int start, int space, String requestLine,
                     String method, boolean newconnection, SSLEngine engine, SSLStreams sslStreams) throws IOException {
        this.exchange = exchange;
        this.req = req;
        this.space = space;
        this.requestLine = requestLine;
        this.start = start;
        this.method = method;
        this.newconnection = newconnection;
        this.engine = engine;
        this.sslStreams = sslStreams;
    }

    public void handleRequest() throws Exception {
        try {
            String uriStr = requestLine.substring(start, space);
            URI uri = new URI(uriStr);
            int start = space + 1;
            String version = requestLine.substring(start);
            Headers headers = req.headers();
            String s = headers.getFirst("Transfer-encoding");
            long clen = 0L;
            if (s != null && s.equalsIgnoreCase("chunked")) {
                clen = -1L;
            } else {
                s = headers.getFirst("Content-Length");
                if (s != null) {
                    clen = Long.parseLong(s);
                }
                if (clen == 0) {
                    exchange.requestCompleted(exchange.connection);
                }
            }
            exchange.ctx = exchange.server.contexts.findContext(exchange.protocol, uri.getPath());
            if (exchange.ctx == null) {
                exchange.reject(Code.HTTP_NOT_FOUND,
                        requestLine, "No context found for request");
                return;
            }
            exchange.connection.setContext(exchange.ctx);
            if (exchange.ctx.getHandler() == null) {
                exchange.reject(Code.HTTP_INTERNAL_ERROR,
                        requestLine, "No handler for context");
                return;
            }
            exchange.tx = new ExchangeImpl(
                    method, uri, req, clen, exchange.connection
            );
            String chdr = headers.getFirst("Connection");
            Headers rheaders = exchange.tx.getResponseHeaders();

            if (chdr != null && chdr.equalsIgnoreCase("close")) {
                exchange.tx.close = true;
            }
            if (version.equalsIgnoreCase("http/1.0")) {
                exchange.tx.http10 = true;
                if (chdr == null) {
                    exchange.tx.close = true;
                    rheaders.set("Connection", "close");
                } else if (chdr.equalsIgnoreCase("keep-alive")) {
                    rheaders.set("Connection", "keep-alive");
                    int idle = (int) ServerConfig.getIdleInterval() / 1000;
                    int max = (int) ServerConfig.getMaxIdleConnections();
                    String val = "timeout=" + idle + ", max=" + max;
                    rheaders.set("Keep-Alive", val);
                }
            }

            if (newconnection) {
                exchange.connection.setParameters(
                        exchange.rawin, exchange.rawout, exchange.chan, engine, sslStreams,
                        exchange.server.sslContext, exchange.protocol, exchange.ctx, exchange.rawin
                );
            }
            /* check if client sent an Expect 100 Continue.
             * In that case, need to send an interim response.
             * In future API may be modified to allow app to
             * be involved in this process.
             */
            String exp = headers.getFirst("Expect");
            if (exp != null && exp.equalsIgnoreCase("100-continue")) {
                //server.logReply(100, requestLine, null);
                exchange.sendReply(
                        Code.HTTP_CONTINUE, false, null
                );
            }
            /* uf is the list of filters seen/set by the user.
             * sf is the list of filters established internally
             * and which are not visible to the user. uc and sc
             * are the corresponding Filter.Chains.
             * They are linked together by a LinkHandler
             * so that they can both be invoked in one call.
             */
            List<Filter> sf = exchange.ctx.getSystemFilters();
            List<Filter> uf = exchange.ctx.getFilters();

            Filter.Chain sc = new Filter.Chain(sf, exchange.ctx.getHandler());
            Filter.Chain uc = new Filter.Chain(uf, new LinkHandler(sc));

            /* set up the two stream references */
            exchange.tx.getRequestBody();
            exchange.tx.getResponseBody();
            if (exchange.server.https) {
                uc.doFilter(new HttpsExchangeImpl(exchange.tx));
            } else {
                uc.doFilter(new HttpExchangeImpl(exchange.tx));
            }

        } catch (IOException e1) {
            exchange.server.logger.log(Level.FINER, "ServerImpl.Exchange (1)", e1);
            exchange.connection.close();
        } catch (NumberFormatException e3) {
            exchange.reject(Code.HTTP_BAD_REQUEST,
                    requestLine, "NumberFormatException thrown");
        } catch (URISyntaxException e) {
            exchange.reject(Code.HTTP_BAD_REQUEST,
                    requestLine, "URISyntaxException thrown");
        } catch (Exception e4) {
            exchange.server.logger.log(Level.FINER, "ServerImpl.Exchange (2)", e4);
            exchange.connection.close();
        }
    }
}
