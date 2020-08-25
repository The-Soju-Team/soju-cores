/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hh.connector.netty.server;

import akka.actor.UntypedActor;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;

/**
 *
 * @author HienDM
 */
public class ServerSender extends UntypedActor {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerSender.class.getSimpleName());
	public ChannelHandlerContext ctx;
	public Server server;

	public ServerSender(ChannelHandlerContext ctx, Server server) {
		this.ctx = ctx;
		this.server = server;
	}

	@Override
	public void onReceive(Object obj) {
		try {
			try {
				LinkedTreeMap msg = (LinkedTreeMap) obj;
				Config.printServerMessage((String) msg.get("server-code"), msg, null, false,
						server.config.getConfig("server-code"));
				byte[] output = ServerDecoder.mapToByteArray(msg);
				ByteBuf buf = ctx.channel().alloc().buffer().writeBytes(output);
				ctx.channel().writeAndFlush(buf);
			} catch (Exception ex) {
				log.error("Error receive message", ex);
				try {
					getContext().stop(getSelf());
				} catch (Exception e) {
				}
			}
		} catch (Throwable e) {
			log.error("Co loi Fatal ", e);
		}
	}
}
