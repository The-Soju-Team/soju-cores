/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.server;

import java.util.HashMap;
import java.util.List;

import com.hh.connector.netty.client.ClientFilter;
import com.hh.connector.netty.client.Connector;
import com.hh.connector.netty.server.NettyServer;
import com.hh.connector.netty.server.ServerFilter;
import com.hh.util.ConfigUtils;

/**
 *
 * @author HienDM
 */
public class Server {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Server.class.getSimpleName());
	public HashMap<String, Class> serverAction = new HashMap();
	public HashMap<String, Class> clientAction = new HashMap();
	public ConfigUtils config;
	public Connector connector = null;
	public NettyServer nettyServer = null;
	public ClientFilter clientFilter;
	public ServerFilter serverFilter;
	public LoadAfterReady loadAfterReady;

	public void setClientFilter(ClientFilter clientFilter) {
		this.clientFilter = clientFilter;
	}

	public void setServerFilter(ServerFilter serverFilter) {
		this.serverFilter = serverFilter;
	}

	public void setLoadAfterReady(LoadAfterReady loadAfterReady) {
		this.loadAfterReady = loadAfterReady;
	}

	public void start(final String configPath) {
		config = new ConfigUtils(configPath);
		connector = new Connector(this);
		nettyServer = new NettyServer(this);
		Thread netty = new Thread() {
			public void run() {
				try {
					initProcess();
					log.info("config path: " + configPath);
					log.info("Listener starting ...");
					nettyServer.startServer();
				} catch (Exception ex) {
					log.error("Error when start listener: ", ex);
				}
			}
		};
		netty.start();
		log.info("Listener started successfully!");
	}

	public void initProcess() {
		log.info("Init server processes....");
		List<String> serverProcesses = config.getServerProcesses();
		if (serverProcesses != null && serverProcesses.size() > 0) {
			for (String process : serverProcesses) {
				String[] action = process.split(",");
				try {
					log.info(String.format("[%s] : %s", action[0].split("=")[1], action[1]));
					serverAction.put(action[0].split("=")[1], Class.forName(action[1]));
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					log.error(String.format("Not found class %s for process %s", action[1], action[0]));
				}
			}
		}

		log.info("Init client processes....");
		List<String> clientProcesses = config.getClientProcesses();
		if (clientProcesses != null && clientProcesses.size() > 0) {
			for (String process : clientProcesses) {
				String[] action = process.split(",");
				try {
					log.info(String.format("[%s] : %s", action[0].split("=")[1], action[1]));
					clientAction.put(action[0].split("=")[1], Class.forName(action[1]));
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					log.error(String.format("Not found class %s for process %s", action[1], action[0]));
				}
			}
		}
	}

	public void stop() {
		nettyServer.stopServer();
	}
}
