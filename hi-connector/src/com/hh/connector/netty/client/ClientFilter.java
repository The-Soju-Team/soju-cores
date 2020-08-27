/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.netty.client;

import com.hh.connector.server.Server;

/**
 * @author vtsoft
 */
public interface ClientFilter {
    public boolean doFilter(Object msg, Server server);
}
