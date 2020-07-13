/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hh.action.BaseAction;
import com.hh.net.httpserver.Headers;
import com.hh.net.httpserver.HttpExchange;
import com.hh.server.WebImpl;
import com.hh.util.FileUtils;
import com.hh.webservice.websocket.IWebsocketConnection;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.internal.LinkedTreeMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import com.hh.action.HttpFilter;
import com.hh.action.ReturnFilter;
import com.hh.connector.server.Config;
import com.hh.connector.server.Server;
import com.hh.server.HHServer;
import com.hh.server.SoapImpl;
import com.hh.socket.websocket.WebSocket;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Set;
import org.apache.log4j.*;

/**
 *
 * @author vtsoft
 */
public class HttpUtils {
	public static final String ACCESS_TOKEN = "access-token";
	public HttpExchange httpExchange;
	public LinkedTreeMap currentSession;
	public HttpFilter filter;
	public ReturnFilter returnFilter;
	public IWebsocketConnection websocketConnection;
	public String contextPath;
	public String path;
	public String sessionId;
	public Map<String, Object> parameters = new LinkedTreeMap<String, Object>();
	private static final int NONE = 0;
	private static final int DATAHEADER = 1;
	private static final int FILEDATA = 2;
	private static final int FIELDDATA = 3;
	private static final int MXA_SEGSIZE = 1000 * 1024 * 10;
	private static final String JSON_TYPE = "application/json";
	private static AtomicInteger messageIds = new AtomicInteger(1);
	private boolean autoClose = true;
	public Server server = null;
	private static final Logger log = Logger.getLogger(HttpUtils.class.getName());
	private static Cache<Object, Object> responseMessage = CacheBuilder.newBuilder().maximumSize(10000000)
			.expireAfterAccess(900, TimeUnit.SECONDS).build();

	public static Object popResponseMessage(String messageId) {
		Object msg = responseMessage.getIfPresent(messageId);
		if (msg != null)
			responseMessage.invalidate(messageId);
		return msg;
	}

	public static void putResponseMessage(String messageId, Object data) {
		responseMessage.put(messageId, data);
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getPath() {
		return path;
	}

	public void setFilter(HttpFilter filter) {
		this.filter = filter;
	}

	public void setReturnFilter(ReturnFilter filter) {
		this.returnFilter = filter;
	}

	public void setWebsocketConnection(IWebsocketConnection websocketConnection) {
		this.websocketConnection = websocketConnection;
	}

	public HttpUtils() {
	}

	public HttpUtils(HttpExchange he) {
		httpExchange = he;
		this.sessionId = getCookie(HttpUtils.ACCESS_TOKEN);
	}

	public void sendNotFoundResponse() throws IOException {
		String response = "404 (Not Found)";
		httpExchange.sendResponseHeaders(404, response.getBytes().length);
		try (OutputStream os = httpExchange.getResponseBody();) {
			os.write(response.getBytes());
			os.close();
		}
	}

	public void sendStringResponse(int status, String response) throws IOException {
		httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		httpExchange.sendResponseHeaders(status, response.getBytes(Charset.forName(FileUtils.UTF_8)).length);
		try (OutputStream os = httpExchange.getResponseBody();) {
			os.write(response.getBytes(Charset.forName(FileUtils.UTF_8)));
			os.close();
		}
	}

	public void sendJsonResponse(int status, String response) throws IOException {
		httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
		httpExchange.sendResponseHeaders(status, response.getBytes(Charset.forName(FileUtils.UTF_8)).length);
		try (OutputStream os = httpExchange.getResponseBody();) {
			os.write(response.getBytes(Charset.forName(FileUtils.UTF_8)));
			os.close();
		}
	}

	public void sendSessionTimeoutDefaultPage() throws IOException {
		String response = "Session timeout!";
		httpExchange.sendResponseHeaders(404, response.getBytes().length);
		try (OutputStream os = httpExchange.getResponseBody();) {
			os.write(response.getBytes());
			os.close();
		}
	}

	public void sendStaticFile(File file, String path) throws IOException {
		String ifModifiedSince = httpExchange.getRequestHeaders().getFirst("If-Modified-Since");
		Headers responseHeaders = httpExchange.getResponseHeaders();
		if (ifModifiedSince != null && ifModifiedSince.length() > 0
				&& ifModifiedSince.equals("" + file.lastModified())) {
			responseHeaders.set("Last-Modified", "" + file.lastModified());
			httpExchange.sendResponseHeaders(304, -1);
			return;
		}

		String fileName = path.substring(path.lastIndexOf("/"));
		String mime = HHServer.config.getMimeType(FileUtils.extractFileExt(fileName));

		responseHeaders.set("Content-Type", mime);
		responseHeaders.set("Last-Modified", "" + file.lastModified());
		responseHeaders.set("Content-Encoding", "gzip");
		/*
		 * Date d = new Date(); d.setTime(d.getTime() + 365*24*3600*1000); // 1 year in
		 * milliseconds responseHeaders.set("Expires", d.toGMTString());
		 */

		httpExchange.sendResponseHeaders(200, 0);
		try (GZIPOutputStream os = new GZIPOutputStream(httpExchange.getResponseBody());
				FileInputStream fs = new FileInputStream(file);) {
			IOUtils.copy(fs, os);
			os.finish();
		}
	}

	public void sendToProcess(String path) throws Exception {
		if (!"post".equalsIgnoreCase(httpExchange.getRequestMethod())) {
			if ((new BaseAction(this)).returnPage(path))
				return;
		}
		parseGetParameters();
		parsePostParameters();
		sendToProcess((LinkedTreeMap) parameters);
	}

	public void sendToProcess(LinkedTreeMap message) throws Exception {
		String connector = WebImpl.getConnectorFromAction(path);
		int index = messageIds.incrementAndGet();
		if (index == 2000000000) {
			messageIds = new AtomicInteger(1);
		}

		message.put("hi-message-id", "" + index);
		message.put("access-token", sessionId);
		Config.printServerMessage("CLIENT", message, null, true, server.config.getConfig("server-code"));
		String originalClientIp = httpExchange.getRemoteAddress().toString();
		int beginIndex = 1;
		int endIndex = originalClientIp.indexOf(":");
		originalClientIp = originalClientIp.substring(beginIndex, endIndex);
		message.put("hi-process", message.get("hi-process"));
		message.put("original-ip-request", originalClientIp);
		message.put("original-ip-request-full", httpExchange.getRemoteAddress().toString());
		log.info(String.format("Receving request from client: %s", message.get("original-ip-request").toString()));
		putResponseMessage("" + index, this);
		if ("websocket".equals(message.get("protocol-type"))) {
			Channel channel = ((WebSocket) message.get("socket")).getChannelHandlerContext().channel();
			Object msg = responseMessage.getIfPresent("socket_" + channel.hashCode());
			if (msg == null) {
				HHServer.mainLogger.info("Put channel: " + "socket_" + channel.hashCode());
				putResponseMessage("socket_" + channel.hashCode(), channel);
			}
			message.put("hi-channel-id", "" + channel.hashCode());
			message.remove("socket");
		}
		setAutoClose(false);
		server.connector.send(message, connector);
	}

	public void returnToClient(HttpUtils hu, LinkedTreeMap responseData) throws Exception {
		if (responseData != null) {
			if ("websocket".equals(responseData.get("return-type"))) {
				String json = (String) responseData.get("data");
				HHServer.mainLogger.info(json);
				Channel channel = (Channel) responseMessage
						.getIfPresent("socket_" + responseData.get("hi-channel-id").toString());
				HHServer.mainLogger.info("Get channel: " + "socket_" + responseData.get("hi-channel-id").toString());
				HHServer.mainLogger.info("Channel: " + channel);
				channel.writeAndFlush(new TextWebSocketFrame(json));
				return;
			}
			Config.printServerMessage("CLIENT", responseData, null, false, server.config.getConfig("server-code"));
			BaseAction baseAction = new BaseAction(hu);
			if (responseData.get("cookie") != null) {
				hu.invalidateSession();
				boolean secure = "true".equals(HHServer.config.getConfig("http-ssl"));
				HHServer.mainLogger.info("add cookie: " + responseData.get("cookie") + " secure: " + secure);
				hu.addHHCookie((String) responseData.get("cookie"), true, secure);
			}
			if (responseData.get("cookie-map") != null) {
				String json = (String) responseData.get("cookie-map");
				GsonBuilder builder = new GsonBuilder();
				builder.setPrettyPrinting();
				Gson gson = builder.create();
				LinkedTreeMap message = gson.fromJson(json, LinkedTreeMap.class);
				Set<String> keys = message.keySet();
				for (String key : keys) {
					hu.addCookie(key, (String) message.get(key));
				}
			}
			if ("download".equals(responseData.get("return-type"))) {
				baseAction.returnDownloadFile(FileUtils.hexToByteArray((String) responseData.get("data")),
						(String) responseData.get("file-name"));
			} else if ("file".equals(responseData.get("return-type"))) {
				baseAction.returnFile(FileUtils.hexToByteArray((String) responseData.get("data")),
						(String) responseData.get("file-name"));
			} else if ("string".equals(responseData.get("return-type"))) {
				baseAction.returnString((String) responseData.get("data"));
			} else if ("redirect".equals(responseData.get("return-type"))) {
				baseAction.returnPage((String) responseData.get("page-name"));
			} else if ("soap".equals(responseData.get("return-type"))) {
				String json = new String(FileUtils.hexToByteArray((String) responseData.get("data")));
				GsonBuilder builder = new GsonBuilder();
				builder.setPrettyPrinting();
				Gson gson = builder.create();
				LinkedTreeMap message = gson.fromJson(json, LinkedTreeMap.class);
				Object lock = SoapImpl.cacheResponse.getIfPresent(message.get("hi-message-id"));
				SoapImpl.cacheResponse.put(message.get("hi-message-id"), json);
				if (lock != null) {
					synchronized (lock) {
						lock.notify();
					}
				}
			} else {
				if (responseData.get("data") != null) {
					hu.httpExchange.getResponseHeaders().set("Content-Type", (String) responseData.get("content-type"));
					hu.httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
					hu.httpExchange.getResponseHeaders().set("Cache-Control", "no-store");
					hu.httpExchange.getResponseHeaders().set("Pragma", "no-cache");
					hu.httpExchange.getResponseHeaders().set("Expires", "0");
					hu.httpExchange.sendResponseHeaders(200, 0);
					try (GZIPOutputStream os = new GZIPOutputStream(hu.httpExchange.getResponseBody());) {
						os.write(FileUtils.hexToByteArray((String) responseData.get("data")));
						os.close();
					}
				} else {
					sendNotFoundResponse();
				}
			}
		} else {
			sendNotFoundResponse();
		}
	}

	public void parseGetParameters() throws UnsupportedEncodingException {
		URI requestedUri = httpExchange.getRequestURI();
		String query = requestedUri.getRawQuery();
		parameters = parseQuery(query, parameters);
	}

	private void parsePostJSONParams(HttpExchange httpExchange) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			log.debug(String.format("TOGREP | Request body: %s", line));
			sb.append(line);
		}
		// Since we can't parse it right now, let's assume it
		Gson gson = new Gson();
		@SuppressWarnings("rawtypes")
		HashMap params = gson.fromJson(sb.toString(), HashMap.class);

		parameters.put("hi-process", params.get("hi-process"));
		parameters.put("jsonBody", sb.toString());
	}

	public void parsePostParameters() throws UnsupportedEncodingException, IOException {
		if ("post".equalsIgnoreCase(httpExchange.getRequestMethod())) {
			Headers headers = httpExchange.getRequestHeaders();
			log.debug("TOGREP | Headers of request:\n");
			for (String key : headers.keySet()) {
				List<String> headersValue = headers.get(key);
				for (String value : headersValue) {
					log.debug(String.format("\tTOGREP | Headers: %s - Value: %s", key, value));
				}
			}

			if (headers.get("Content-type") != null) {
//				String contentTypeHienDM1 = headers.get("Content-type").toString().replace("[", "").replace("]", ""); // We will keep this line as a reference to Mr.HienDM1
				// Let's assume that we always have content type, just a inject kind of thing,
				// no biggy
				String contentType = "application/json";
				if (headers.get("Content-type").size() != 0) {
					contentType = headers.get("Content-type").get(0);
				}
//				//
				log.debug(String.format("Content-type: %s", contentType));
				int length = Integer
						.parseInt(headers.get("Content-length").toString().replace("[", "").replace("]", ""));
				switch (contentType) {
				case JSON_TYPE:
					parsePostJSONParams(httpExchange);
					break;
				default:
					parameters = parseFile(parameters, httpExchange.getRequestBody(), contentType, length);
					break;
				}
			} else {
				try (InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);) {
					String query = br.readLine();
					parameters = parseQuery(query, parameters);
				}
			}
		}
	}

	public Map<String, Object> parseFile(Map<String, Object> formfields, InputStream ins, String contentType,
			int totalLength) throws IOException {

		FileInfo fileInfo = new FileInfo();
		String fieldname = "";
		String fieldvalue = "";
		String filename = "";
		String boundary = "";
		String lastboundary = "";
		String filefieldname = "";

		int filesize = 0;

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) {
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastboundary = boundary + "--";
		}
		int state = NONE;

		try (DataInputStream in = new DataInputStream(ins);) {
			int totalBytes = totalLength;
			String message = "";
			if (totalBytes > MXA_SEGSIZE) {
				message = "Each batch of data can not be larger than " + MXA_SEGSIZE / (1000 * 1024) + "M";
				return null;
			}
			byte[] b = new byte[totalBytes];
			in.readFully(b);
			in.close();
			String reqContent = new String(b, "UTF-8");//
			BufferedReader reqbuf = new BufferedReader(new StringReader(reqContent));

			boolean flag = true;
			int i = 0;
			String firstLine = reqbuf.readLine();
			formfields = parseQuery(firstLine, formfields);
			boolean first = true;
			while (flag == true) {
				String s = "";
				if (first) {
					s = firstLine;
					first = false;
				} else
					s = reqbuf.readLine();
				if ((s == null) || (s.equals(lastboundary))) {
					break;
				}

				switch (state) {
				case NONE:
					if (s.startsWith(boundary)) {
						state = DATAHEADER;
						i += 1;
					}
					break;
				case DATAHEADER:
					pos = s.indexOf("filename=");
					if (pos == -1) {
						pos = s.indexOf("name=");
						pos += "name=".length() + 1;
						s = s.substring(pos);
						int l = s.length();
						s = s.substring(0, l - 1);
						fieldname = s;
						state = FIELDDATA;
					} else {
						String temp = s;

						pos = s.indexOf("name=");
						pos += "name=".length() + 1;
						s = s.substring(pos);
						int pos1 = s.indexOf("\";");
						filefieldname = s.substring(0, pos1);

						pos = s.indexOf("filename=");
						pos += "filename=".length() + 1;
						s = s.substring(pos);
						int l = s.length();
						s = s.substring(0, l - 1);
						pos = s.lastIndexOf("\\");
						s = s.substring(pos + 1);
						filename = s;

						pos = byteIndexOf(b, temp, 0);
						b = subBytes(b, pos + temp.getBytes().length + 2, b.length);
						int n = 0;

						while ((s = reqbuf.readLine()) != null) {
							if (n == 1) {
								break;
							}
							if (s.equals("")) {
								n++;
							}

							b = subBytes(b, s.getBytes().length + 2, b.length);
						}
						pos = byteIndexOf(b, boundary, 0);
						if (pos != -1) {
							b = subBytes(b, 0, pos - 1);
						}

						filesize = b.length - 1;
						state = FILEDATA;
					}
					break;
				case FIELDDATA:
					s = reqbuf.readLine();
					fieldvalue = s;
					formfields.put(fieldname, fieldvalue);
					state = NONE;
					break;
				case FILEDATA:
					while ((!s.startsWith(boundary)) && (!s.startsWith(lastboundary))) {
						s = reqbuf.readLine();
						if (s.startsWith(boundary)) {
							state = DATAHEADER;
							break;
						}
					}
					break;
				}
			}
			fileInfo.setFieldname(filefieldname);
			fileInfo.setBytes(b);
			fileInfo.setFilename(filename);
			fileInfo.setLength(filesize);
			formfields.put(filefieldname, fileInfo);
		}
		return formfields;
	}

	public int byteIndexOf(byte[] b, String s, int start) {
		return byteIndexOf(b, s.getBytes(), start);
	}

	public int byteIndexOf(byte[] b, byte[] s, int start) {
		int i;
		if (s.length == 0) {
			return 0;
		}
		int max = b.length - s.length;
		if (max < 0) {
			return -1;
		}
		if (start > max) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}
		search: for (i = start; i <= max; i++) {
			if (b[i] == s[0]) {
				int k = 1;
				while (k < s.length) {
					if (b[k + i] != s[k]) {
						continue search;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	public byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	public String subBytesString(byte[] b, int from, int end) {
		return new String(subBytes(b, from, end));
	}

	private Map<String, Object> parseQuery(String query, Map<String, Object> parameters)
			throws UnsupportedEncodingException {
		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], "utf-8");
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], "utf-8");
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
		return parameters;
	}

	public boolean checkSessionTimeout(String sessionId) throws IOException, ClassNotFoundException {
		if (sessionId == null || sessionId.trim().isEmpty())
			return false;
		Object checkSession = getSessionAttribute(HttpSession.SESSION_DEFAULT_KEY);
		boolean checkTimeout = false;
		if (checkSession != null) {
			HttpSession.getInstance().refreshExpire(sessionId);
		} else {
			checkTimeout = true;
		}
		return checkTimeout;
	}

	public boolean doFilter() {
		if (filter != null)
			return filter.execute(this);
		else
			return true;
	}

	public void createSession(String sessionId) throws IOException {
		this.sessionId = sessionId;
		HttpSession.getInstance().createSession(sessionId);
	}

	public Object getSessionAttribute(String key) {
		return HttpSession.getInstance().getSessionAttribute(sessionId, key);
	}

	public void setSessionAttribute(String key, Object value) {
		HttpSession.getInstance().setSessionAttribute(sessionId, key, value);
	}

	public void removeSessionAttribute(String key) {
		HttpSession.getInstance().removeSessionAttribute(sessionId, key);
	}

	public void removeSession() {
		HttpSession.getInstance().removeSession(sessionId);
	}

	public void removeCookie(String key) {
		addCookie(key, "null", "/", 0l, true, false);
	}

	public void setCookie(String key, String value) {
		setCookie(key, value, false, false);
	}

	public void setCookie(String key, String value, Boolean isHttpOnly, Boolean isSecure) {
		setCookie(key, value, "/", null, isHttpOnly, isSecure);
	}

	public void setCookie(String key, String value, String path, Boolean isHttpOnly, Boolean isSecure) {
		setCookie(key, value, path, null, isHttpOnly, isSecure);
	}

	public void setCookie(String key, String value, String path, Long maxAge, Boolean isHttpOnly, Boolean isSecure) {
		httpExchange.getResponseHeaders().remove("Set-Cookie");
		addCookie(key, value, path, maxAge, isHttpOnly, isSecure);
	}

	public void addCookie(String key, String value) {
		addCookie(key, value, false, false);
	}

	public void addCookie(String key, String value, Boolean isHttpOnly, Boolean isSecure) {
		addCookie(key, value, "/", null, isHttpOnly, isSecure);
	}

	public void addCookie(String key, String value, String path, Boolean isHttpOnly, Boolean isSecure) {
		addCookie(key, value, path, null, isHttpOnly, isSecure);
	}

	public void addCookie(String key, String value, String path, Long maxAge, Boolean isHttpOnly, Boolean isSecure) {
		if (path == null || path.trim().isEmpty())
			path = "/";
		String cookie = key + "=" + value + ";Path=" + path;
		if (maxAge != null) {
			Date d = new Date();
			d.setTime(d.getTime() + maxAge); // in milliseconds
			cookie += ";Expires=" + d.toGMTString();
		}
		if (isHttpOnly) {
			cookie += ";HttpOnly";
		}
		if (isSecure) {
			cookie += ";Secure";
		}
		httpExchange.getResponseHeaders().add("Set-Cookie", cookie);
	}

	public void addHHCookie(String value, boolean isHttpOnly, boolean isSecure) {
		addCookie(ACCESS_TOKEN, value, isHttpOnly, isSecure);
	}

	public final String getCookie(String key) {
		List<String> cookies = httpExchange.getRequestHeaders().get("Cookie");
		String value = "";
		if (cookies != null) {
			for (String cookie : cookies) {
				String[] arrCookie = null;
				if (cookie.contains(";"))
					arrCookie = cookie.split(";");
				else {
					arrCookie = new String[1];
					arrCookie[0] = cookie;
				}
				for (String childCookie : arrCookie) {
					if (childCookie.substring(0, childCookie.indexOf("=")).trim().equals(key)) {
						value = childCookie.substring(childCookie.indexOf("=") + 1);
						break;
					}
				}
				if (!value.isEmpty())
					break;
			}
		}
		return value;
	}

	public String getHHCookie() {
		return getCookie(ACCESS_TOKEN);
	}

	public Object getParameter(String key) {
		if (parameters != null && !parameters.isEmpty()) {
			return parameters.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Redirect client to {@code location}. The client should retrieve the referred
	 * location via GET, independent of the method of this request.
	 *
	 * @param location location
	 * @throws IOException ioe
	 */
	public void sendRedirect(String location) throws IOException {
		httpExchange.getResponseHeaders().set("Location", location);
		httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void invalidateSession() throws IOException {
		String newSessionId = UUID.randomUUID().toString();
		createSession(newSessionId);
		boolean secure = HHServer.config.getConfig("http-ssl").equals("true");
		addHHCookie(newSessionId, true, secure);
	}
}
