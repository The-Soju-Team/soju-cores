/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.hh.socket.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class NettyWebSocket implements WebSocket
{
    private final Logger logger = LoggerFactory.getLogger(NettyWebSocket.class);
    private final WebSocketRequest wsRequest;
    private final ChannelHandlerContext ctx;
    private String currentMessage;
    private final List<WebSocket.Callback> messageHandlers = new ArrayList<WebSocket.Callback>();
    private final List<WebSocket.Callback> disconnectHandlers = new ArrayList<WebSocket.Callback>();

    NettyWebSocket(WebSocketRequest wsRequest,
                   ChannelHandlerContext ctx)
    {
        this.wsRequest = wsRequest;
        this.ctx = ctx;
    }
    
    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }

    @Override
    public String getPath()
    {
        return this.wsRequest.getHandlerName();
    }

    @Override
    public String getWiki()
    {
        return this.wsRequest.getPath();
    }

    @Override
    public Map<String, List<String>> getParameters()
    {
        return this.wsRequest.getParameters();
    }


    @Override
    public void send(String message)
    {
        this.ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    }

    @Override
    public String recv()
    {
        return this.currentMessage;
    }

    @Override
    public void onMessage(WebSocket.Callback cb)
    {
        this.messageHandlers.add(cb);
    }

    @Override
    public void onDisconnect(WebSocket.Callback cb)
    {
        this.disconnectHandlers.add(cb);
    }

    void message(String msg)
    {
        for (WebSocket.Callback cb : this.messageHandlers) {
            this.currentMessage = msg;
            try {
                cb.call(this);
            } catch (Exception e) {
                logger.warn("Exception in WebSocket.onMessage() [{}]",
                            e.getStackTrace());
            }
            this.currentMessage = null;
        }
    }

    void disconnect()
    {
        for (WebSocket.Callback cb : this.disconnectHandlers) {
            try {
                cb.call(this);
            } catch (Exception e) {
                logger.warn("Exception in WebSocket.onDisconnect() [{}]",
                            e.getStackTrace());
            }
        }
    }
}
