/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.server;

import static com.hh.connector.netty.server.ServerHandler.padLeft;
import com.google.gson.internal.LinkedTreeMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HienDM
 */
public class Config {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Config.class.getSimpleName());
	public static final String CMD_KEEP_ALIVE = "/keepalive";

	public static final String RES_SUCCESS = "00";
	public static final String ERROR_NOT_SUPPORT = "11";
	public static final String ERROR_INVALID_USER = "12";

	public static final boolean IS_REQUEST = true;
	public static final boolean IS_RESPONSE = false;

	public Config() {
	}

	public static LinkedTreeMap responseMessage(LinkedTreeMap message, String responseCode, String response) {
		message.put("code", responseCode);
		message.put("message", response);
		return message;
	}

	public static LinkedTreeMap pingMessage(String serverCode) {
		LinkedTreeMap mapResponse = new LinkedTreeMap();
		mapResponse.put("hi-path", CMD_KEEP_ALIVE);
		mapResponse.put("message", "ping");
		mapResponse.put("server-code", serverCode);
		return mapResponse;
	}

	public static void printServerMessage(String client, LinkedTreeMap<String, Object> message, List notPrintField,
			boolean isRequest, String serverCode) {
		if (message.get("hi-message-id") == null) {
			return;
		}
		StringBuilder content = new StringBuilder();
		// Print stack trace so we can debug easier
		StackTraceElement[] a = Thread.currentThread().getStackTrace();
		StringBuilder stackTraceString = new StringBuilder();
		for (int i = a.length - 1; i > 0; i--) {
			stackTraceString.append("\n");
			for (int j = 0; j < a.length - 1 - i; j++) {
				stackTraceString.append("-");
			}
			stackTraceString.append("--> ");
			stackTraceString.append(a[i]);

		}
		log.debug(String.format("TOGREP | Server message stack trace: %s", stackTraceString.toString()));
		// End stack trace
		if (client == null || client.isEmpty())
			client = "CLIENT";
		content.append("\n");
		if (isRequest) {
			content.append("[");
			content.append(serverCode);
			content.append("|id-");
			content.append(message.get("hi-message-id"));
			content.append("]");
			content.append("----------------- REQUEST FROM ");
			content.append(client.toUpperCase());
			content.append(" ---------------- \n");
		} else {
			content.append("[");
			content.append(serverCode);
			content.append("|id-");
			content.append(message.get("hi-message-id"));
			content.append("]");
			content.append("----------------- RESPONSE TO ");
			content.append(client.toUpperCase());
			content.append(" ----------------- \n");
		}
		for (Map.Entry<String, Object> entry : message.entrySet()) {
			if (notPrintField == null || !notPrintField.contains(entry.getKey())) {
				content.append("[");
				content.append(serverCode);
				content.append("|id-");
				content.append(message.get("hi-message-id"));
				content.append("]");
				content.append(padLeft(entry.getKey(), 30));
				content.append(" : ");
				content.append(entry.getValue());
				content.append(": \n");
			}
		}
		content.append("[");
		content.append(serverCode);
		content.append("|id-");
		content.append(message.get("hi-message-id"));
		content.append("]");
		content.append("------------------------------------------------------- \n");
		log.debug(content);
	}

	public static void printClientMessage(String connector, LinkedTreeMap<String, Object> message, List notPrintField,
			boolean isRequest, String serverCode) {
		if (message.get("hi-message-id") == null) {
			return;
		}

		// Print stack trace so we can debug easier
		StackTraceElement[] a = Thread.currentThread().getStackTrace();
		StringBuilder stackTraceString = new StringBuilder();
		for (int i = a.length - 1; i > 0; i--) {
			stackTraceString.append("\n");
			for (int j = 0; j < a.length - 1 - i; j++) {
				stackTraceString.append("-");
			}
			stackTraceString.append("--> ");
			stackTraceString.append(a[i]);

		}
		log.debug(String.format("TOGREP | Client message stack trace: %s", stackTraceString.toString()));
		// End stack trace
		StringBuilder content = new StringBuilder();
		content.append("\n");
		if (isRequest) {
			content.append("[");
			content.append(serverCode);
			content.append("|id-");
			content.append(message.get("hi-message-id"));
			content.append("]");
			content.append("----------------- REQUEST TO ");
			content.append(connector.toUpperCase());
			content.append(" ---------------- \n");
		} else {
			content.append("[");
			content.append(serverCode);
			content.append("|id-");
			content.append(message.get("hi-message-id"));
			content.append("]");
			content.append("----------------- RESPONSE FROM ");
			content.append(connector.toUpperCase());
			content.append(" ----------------- \n");
		}
		for (Map.Entry<String, Object> entry : message.entrySet()) {
			if (notPrintField == null || !notPrintField.contains(entry.getKey())) {
				content.append("[");
				content.append(serverCode);
				content.append("|id-");
				content.append(message.get("hi-message-id"));
				content.append("]");
				content.append(padLeft(entry.getKey(), 30));
				content.append(" : ");
				content.append(entry.getValue());
				content.append(": \n");
			}
		}
		content.append("[");
		content.append(serverCode);
		content.append("|id-");
		content.append(message.get("hi-message-id"));
		content.append("]");
		content.append("------------------------------------------------------- \n");
		log.debug(content);
	}
}
