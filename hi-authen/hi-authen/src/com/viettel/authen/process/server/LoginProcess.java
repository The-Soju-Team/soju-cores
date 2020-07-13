/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.hh.connector.server.Server;
import com.hh.util.EncryptDecryptUtils;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.StartApp;

import io.netty.channel.ChannelHandlerContext;
import com.google.gson.internal.LinkedTreeMap;

import java.util.UUID;

/**
 *
 * @author HienDM
 */
public class LoginProcess extends ServerProcess {

	public static final String LOGIN_SUCCESS = "0";
	public static final String LOGIN_INCORRECT = "1";
	public static final String CAPTCHA_INCORRECT = "2";
	public static final String USER_EMPTY = "3";
	public static final String PASSWORD_EMPTY = "4";
	public static final String CAPTCHA_EMPTY = "5";
	public static final String PASSWORD_EXPIRY = "6";

	public LoginProcess(ChannelHandlerContext ctx, Server server) {
		super(ctx, server);
	}

	@Override
	public void process(LinkedTreeMap obj) throws Exception {
		String captCha = "";
		Integer failCount = null;
		if (getSessionStringAttribute(obj, "ssoFailCount") != null)
			failCount = Integer.parseInt(getSessionStringAttribute(obj, "ssoFailCount"));
		if (failCount == null)
			failCount = 0;
		else if (failCount > 2)
			captCha = (String) obj.get("captcha");

		String password = (String) obj.get("password");
		String userName = (String) obj.get("userName");
		if (userName == null || userName.isEmpty()) {
			failCount++;
			sendLoginResponse(obj, USER_EMPTY, failCount);
			return;
		}
		if (password == null || password.isEmpty()) {
			failCount++;
			sendLoginResponse(obj, PASSWORD_EMPTY, failCount);
			return;
		}

		// Kiem tra captcha
		String mobile = (String) obj.get("gm");
		boolean checkCaptcha = true;
		if (!(mobile == null || mobile.isEmpty())) {
			if ((captCha != null) && (!captCha.isEmpty()) && (!"null".equals(captCha))) {
				String pixCaptcha = (String) getSessionStringAttribute(obj, "sso_captcha");
				if (pixCaptcha != null) {
					checkCaptcha = pixCaptcha.toLowerCase().equals(captCha.toLowerCase());
				}
				if (!checkCaptcha) {
					failCount++;
					sendLoginResponse(obj, CAPTCHA_INCORRECT, failCount);
					return;
				}
			} else if (failCount > 2) {
				checkCaptcha = false;
				failCount++;
				sendLoginResponse(obj, CAPTCHA_EMPTY, failCount);
				return;
			}
			removeSessionAttribute(obj, "sso_captcha");
		}

		// Neu nhap dung captcha
		if (checkCaptcha) {
			EncryptDecryptUtils edu = new EncryptDecryptUtils();
			Gson gson = new Gson();
			String json = StartApp.hicache.getStringAttribute("credentials", userName);
			LinkedTreeMap user = null;
			if (json != null)
				user = gson.fromJson(json, LinkedTreeMap.class);
			String correctPassword = null;
			if (user != null)
				correctPassword = (String) user.get("password");
			// User has input the correct information
			if (user != null && correctPassword != null && correctPassword.equals(edu.encodePassword(password))) {
				String strUserInfo = gson.toJson(user);
				String newCookie = UUID.randomUUID().toString();
				StartApp.hicache.createStore(newCookie, 30000l);
				StartApp.hicache.setStoreAttribute(newCookie, "sso_username", strUserInfo);
				obj.put("cookie", newCookie);
				sendLoginResponse(obj, LOGIN_SUCCESS, 0);
			} else {
				failCount++;
				setSessionAttribute(obj, "ssoFailCount", "" + failCount);
				sendLoginResponse(obj, LOGIN_INCORRECT, failCount);
			}
		}
	}

	public void sendLoginResponse(LinkedTreeMap request, String status, Integer failCount) throws Exception {
		LinkedTreeMap<String, String> data = new LinkedTreeMap();
		data.put("status", status);
		if (failCount == null) {
			failCount = 0;
		}
		data.put("failCount", failCount.toString());
		data.put("accessToken", (String) request.get("cookie"));
		String json = new Gson().toJson(data);
		returnStringToFrontend(request, json);
	}
}
