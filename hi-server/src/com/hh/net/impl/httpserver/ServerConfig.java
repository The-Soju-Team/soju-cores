/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.net.impl.httpserver;

import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Parameters that users will not likely need to set
 * but are useful for debugging
 */

class ServerConfig {

    static final int DEFAULT_CLOCK_TICK = 10000; // 10 sec.
    /* These values must be a reasonable multiple of clockTick */
    static final long DEFAULT_IDLE_INTERVAL = 30; // 5 min
    static final int DEFAULT_MAX_IDLE_CONNECTIONS = 100;
    static final long DEFAULT_MAX_REQ_TIME = 600; // -1: forever, don vi giay
    static final long DEFAULT_MAX_RSP_TIME = 600; // -1: forever, don vi giay
    static final long DEFAULT_TIMER_MILLIS = 1000;
    static final int DEFAULT_MAX_REQ_HEADERS = 200;
    static final long DEFAULT_DRAIN_AMOUNT = 64 * 1024;
    static int clockTick;
    static long idleInterval;
    static long drainAmount;    // max # of bytes to drain from an inputstream
    static int maxIdleConnections;
    // max time a request or response is allowed to take
    static long maxReqTime;
    static long maxRspTime;
    static long timerMillis;
    static boolean debug;
    // the value of the TCP_NODELAY socket-level option
    static boolean noDelay;
    // The maximum number of request headers allowable
    private static int maxReqHeaders;

    static {
        java.security.AccessController.doPrivileged(
                new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        System.out.println("CONFIG HI-SERVER");

                        idleInterval = Long.getLong("sun.net.httpserver.idleInterval",
                                DEFAULT_IDLE_INTERVAL) * 1000;

                        clockTick = Integer.getInteger("sun.net.httpserver.clockTick",
                                DEFAULT_CLOCK_TICK);

                        maxIdleConnections = Integer.getInteger(
                                "sun.net.httpserver.maxIdleConnections",
                                DEFAULT_MAX_IDLE_CONNECTIONS);

                        drainAmount = Long.getLong("sun.net.httpserver.drainAmount",
                                DEFAULT_DRAIN_AMOUNT);

                        maxReqHeaders = Integer.getInteger(
                                "sun.net.httpserver.maxReqHeaders",
                                DEFAULT_MAX_REQ_HEADERS);

                        maxReqTime = Long.getLong("sun.net.httpserver.maxReqTime",
                                DEFAULT_MAX_REQ_TIME);

                        maxRspTime = Long.getLong("sun.net.httpserver.maxRspTime",
                                DEFAULT_MAX_RSP_TIME);

                        timerMillis = Long.getLong("sun.net.httpserver.timerMillis",
                                DEFAULT_TIMER_MILLIS);

                        debug = Boolean.getBoolean("sun.net.httpserver.debug");

                        noDelay = Boolean.getBoolean("sun.net.httpserver.nodelay");

                        Properties config = new Properties();

                        if (config.get("DEFAULT_MAX_REQ_TIME") != null)
                            maxReqTime = Long.valueOf(config.get("DEFAULT_MAX_REQ_TIME").toString()).longValue();

                        if (config.get("DEFAULT_MAX_RSP_TIME") != null)
                            maxRspTime = Long.valueOf(config.get("DEFAULT_MAX_RSP_TIME").toString()).longValue();

                        return null;
                    }
                });

    }


    static void checkLegacyProperties(final Logger logger) {

        // legacy properties that are no longer used
        // print a warning to logger if they are set.

        java.security.AccessController.doPrivileged(
                new PrivilegedAction<Void>() {
                    public Void run() {
                        if (System.getProperty("sun.net.httpserver.readTimeout")
                                != null) {
                            logger.warning("sun.net.httpserver.readTimeout " +
                                    "property is no longer used. " +
                                    "Use sun.net.httpserver.maxReqTime instead."
                            );
                        }
                        if (System.getProperty("sun.net.httpserver.writeTimeout")
                                != null) {
                            logger.warning("sun.net.httpserver.writeTimeout " +
                                    "property is no longer used. Use " +
                                    "sun.net.httpserver.maxRspTime instead."
                            );
                        }
                        if (System.getProperty("sun.net.httpserver.selCacheTimeout")
                                != null) {
                            logger.warning("sun.net.httpserver.selCacheTimeout " +
                                    "property is no longer used."
                            );
                        }
                        return null;
                    }
                }
        );
    }

    static boolean debugEnabled() {
        return debug;
    }

    static long getIdleInterval() {
        return idleInterval;
    }

    static int getClockTick() {
        return clockTick;
    }

    static int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    static long getDrainAmount() {
        return drainAmount;
    }

    static int getMaxReqHeaders() {
        return maxReqHeaders;
    }

    static long getMaxReqTime() {
        return maxReqTime;
    }

    static long getMaxRspTime() {
        return maxRspTime;
    }

    static long getTimerMillis() {
        return timerMillis;
    }

    static boolean noDelay() {
        return noDelay;
    }
}
