package com.hh.socket.websocket;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Parse an URI to provide WebSocket service with request informations.
 *
 * @version $Id$
 */
public class NettyWebSocketRequest implements WebSocketRequest {
    private final String key;

    private final String handlerName;

    private final String wiki;

    private final Map<String, List<String>> parameters;

    NettyWebSocketRequest(FullHttpRequest req, WebSocketService wss) {
        QueryStringDecoder dec = new QueryStringDecoder(req.getUri());
        parameters = dec.parameters();
        final List<String> keyParam = parameters.get("k");
        if (keyParam != null && keyParam.size() == 1) {
            key = keyParam.get(0);
        } else {
            key = null;
        }
        final String[] path = dec.path().split("/");
        if (path.length >= 2) {
            handlerName = path[path.length - 1];
            wiki = path[path.length - 2];
        } else {
            handlerName = null;
            wiki = null;
        }
        //user = wss.getUser(wiki, key);
    }

    @Override
    public boolean isValid() {
        return key != null && handlerName != null && wiki != null;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getHandlerName() {
        return handlerName;
    }

    @Override
    public String getPath() {
        return wiki;
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return parameters;
    }
}
