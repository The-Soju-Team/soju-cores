/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author HienDM
 */
public class SoapImpl {
    public static Cache<Object, Object> cacheResponse = CacheBuilder.newBuilder()
                .maximumSize(10000000)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .build();
    
    public void initSoapService(HHServer server) throws Exception {
    }
}
