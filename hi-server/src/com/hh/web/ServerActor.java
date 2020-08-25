package com.hh.web;

import org.apache.log4j.*;
import com.hh.connector.server.Server;
import com.hh.server.HHServer;
import com.hh.server.WebImpl;
import com.hh.util.FileUtils;
import java.io.File;
import java.net.URI;
import java.util.HashMap;

public class ServerActor {
	private static HashMap mapConfig;
	private HttpUtils httpUtil;
	private static final Logger log = Logger.getLogger(ServerActor.class.getName());

	public ServerActor(HttpUtils hu, Server server) {
		this.httpUtil = hu;
		this.httpUtil.server = server;
	}

	public void onReceive() {
		try {
			handleRequest();
		} catch (Exception ex) {
			HHServer.mainLogger.error("HHServer error: ", ex);
//            try {
//                httpUtil.sendStringResponse(404, "HHServer error (ServerActor.java:27)");
//            } catch (Exception e) {
//                HHServer.mainLogger.error("HHServer error: ", e);
//            }
		} finally {
			if (httpUtil.isAutoClose() && httpUtil.httpExchange != null) {
				httpUtil.httpExchange.close();
				httpUtil.httpExchange = null;
			}
		}
	}

	/**
	 * 
	 * @param url input URL
	 * @return return a proper end-point
	 */
	private String uriParse(String url) {
		// abc.xyz/risk would return 200, but abc.xyz/risk/ would return 404, this is
		// not a correct behavior
		url = url.trim();
		// Remove the last "/" characters at the end of string
		while (true) {
			if (url.charAt(url.length() - 1) == '/') {
				url = url.substring(0, url.length() - 1);
			} else {
				break;
			}
		}
		// Replace "//+" with "/" only
		url = url.replaceAll("[\\/]+", "/");
		return url;

	}

	private void handleRequest() throws Exception {
		URI original = httpUtil.httpExchange.getRequestURI();
		String path = "";
		if (original.getRawPath() != null) {
			path = uriParse(original.getRawPath());
		}

		httpUtil.path = path;
		httpUtil.contextPath = path.substring(0, path.indexOf("/"));
		log.debug("TOGREP | URI: " + original);
		String webAction = WebImpl.getConnectorFromAction(path);
		log.debug("TOGREP | WebAction " + webAction);
		if (webAction == null) { // Nếu không phải action
			String sharePath = "";
			if (path.length() > 0)
				sharePath = path.substring(1);
			if (FileUtils.checkSafeFileName(path) && sharePath.contains("/")
					&& (sharePath.length() > sharePath.indexOf("/") + 7)
					&& sharePath.substring(sharePath.indexOf("/"), sharePath.indexOf("/") + 7).equals("/share/")) {
				// Nếu là file javascript, css, image...
				File file = new File("../app" + path);
				if (!file.isFile()) {
					httpUtil.sendNotFoundResponse();
				} else {
					httpUtil.sendStaticFile(file, path);
				}
			} else {
				httpUtil.sendNotFoundResponse();
			}
		} else {
			if (!httpUtil.doFilter())
				return;
			httpUtil.sendToProcess(path);
		}
	}
}
