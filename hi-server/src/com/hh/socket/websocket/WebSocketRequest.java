package com.hh.socket.websocket;

import java.util.List;
import java.util.Map;


/**
 * Please comment here
 *
 * @version $Id$
 */
public interface WebSocketRequest {
    boolean isValid();

    String getKey();

    String getHandlerName();

    String getPath();

    Map<String, List<String>> getParameters();
}
