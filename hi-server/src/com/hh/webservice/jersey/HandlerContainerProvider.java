/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.webservice.jersey;

import com.hh.net.httpserver.HttpHandler;
import org.glassfish.jersey.server.spi.ContainerProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;

/**
 * Container provider for containers based on lightweight Java SE HTTP Server's {@link HttpHandler}.
 *
 * @author Miroslav Fuksa
 */
public final class HandlerContainerProvider implements ContainerProvider {

    @Override
    public <T> T createContainer(Class<T> type, Application application) throws ProcessingException {
        if (type != HttpHandler.class && type != HandlerContainer.class) {
            return null;
        }
        return type.cast(new HandlerContainer(application));
    }
}