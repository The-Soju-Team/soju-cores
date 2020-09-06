/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.net.impl.httpserver;

import com.hh.net.httpserver.Filter;
import com.hh.net.httpserver.HttpExchange;
import com.hh.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * @author hiendm1
 */
public class LinkHandler implements HttpHandler {

    Filter.Chain nextChain;

    LinkHandler(Filter.Chain nextChain) {
        this.nextChain = nextChain;
    }

    public void handle(HttpExchange exchange) throws IOException {
        nextChain.doFilter(exchange);
    }
}
