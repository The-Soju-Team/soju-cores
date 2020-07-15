/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import com.hh.util.EncryptDecryptUtils;
import com.viettel.authen.run.ServerProcess;
import com.viettel.authen.run.StartApp;
import com.viettel.authen.run.UpdateTransToDBThread;
import com.viettel.authen.util.DateUtils;
import com.viettel.authen.util.StringUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author HienDM
 */
public class LoginProcess extends ServerProcess {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoginProcess.class.getSimpleName());
	private static org.apache.log4j.Logger logKafka = org.apache.log4j.Logger.getLogger("kafkaLogger");
	public static final String[] LOGIN_SUCCESS = new String[] { "0", "Correct credential" };
	public static final String[] LOGIN_INCORRECT = new String[] { "1", "Your login credential is incorrect" };
	public static final String[] CAPTCHA_INCORRECT = new String[] { "2", "Your captcha input is incorrect" };
	public static final String[] USER_EMPTY = new String[] { "3", "Username is required" };
	public static final String[] PASSWORD_EMPTY = new String[] { "4", "Password is required" };
	public static final String[] CAPTCHA_EMPTY = new String[] { "5", "Captcha is required" };
	public static final String[] NOT_ALLOWED_IP = new String[] { "6", "Your accout is not allowed to use on this IP" };
	public static final String[] PASSWORD_EXPIRED = new String[] { "7",
			"Your password is expired, click login again to be redirected to change password site" };
	public static final String[] NO_APPS_GRANTED = new String[] { "8",
			"The application  you are trying to reach has not been granted to your account" };
	private static Gson gson = new Gson();

	private static DateUtils dateUtils = new DateUtils();

	public LoginProcess(ChannelHandlerContext ctx, Server server) {
		super(ctx, server);
	}

	@Override
	public void process(LinkedTreeMap obj) throws Exception {
		logKafka.info((new Gson()).toJson(obj));
		log.info("========== ABCDEF ===========");
		log.info((new Gson()).toJson(obj));
		log.info("////////// ABCDEF ///////////");
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
		String forward = (String) obj.get("forward");
		String ip = (String) obj.get("original-ip-request");
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

			// 2020-04-13 17:27:04 donnn: Check IP
			String loginToken = (String) obj.get("loginToken");
			// String queryCheckIp = "select lt.*, ui.allowed_ip from (select * from
			// login_token where USER_NAME = ? and LOGIN_TOKEN = ? order by id desc limit 1)
			// lt INNER join user_ip ui on lt.user_name = ui.user_name";
			String queryCheckIp = " select user_name, allowed_ip from user_ip where user_name = ? ";
			String userIp = ip;
			List params = new ArrayList();
			params.add(userName);
			// params.add(loginToken);
			log.info("queryCheckIp");
			log.info(queryCheckIp);
			boolean checkAllowedIp = false;
			List<Map> resultQueryCheckIp = StartApp.database.queryData(queryCheckIp, params);
			if (!(userName.equals("donnn"))) {
				checkAllowedIp = true;
			} else if ((resultQueryCheckIp == null) || (resultQueryCheckIp.size() < 1)) {
				// allow all other users
				checkAllowedIp = false;
			} else {
				ArrayList<String> allowedIps = gson.fromJson(StartApp.hicache.getStringAttribute("user_ips", userName),
						ArrayList.class);
				log.info("ABCDEF Data from hicache for user " + userName
						+ StartApp.hicache.getStringAttribute("user_ips", userName) + " " + allowedIps);
				if (allowedIps != null) {
					for (int idx = 0; idx < allowedIps.size(); idx++) {
						String allowedIp = allowedIps.get(idx);
						// log.info("ABCDEF comparing ip " + userIp + " and allowedIp " + allowedIp + "
						// result " + StringUtils.checkIpInNetwork(userIp, allowedIp));
						if (StringUtils.checkIpInNetwork(userIp, allowedIp)) {
							checkAllowedIp = true;
							break;
						}
					}
				}
			}

			if (!checkAllowedIp) {
				log.info("ABCDEF user " + userName + " trying to log in at unauthorized IP " + userIp);
				sendLoginResponse(obj, NOT_ALLOWED_IP, failCount);
				return;
			}

			log.info("ABCDEF checkAllowedIp " + checkAllowedIp);

			// End check IP

			EncryptDecryptUtils edu = new EncryptDecryptUtils();
			Gson gson = new Gson();
			String json = StartApp.hicache.getStringAttribute("credentials", userName);
			log.info("ABCDEF " + json);
			LinkedTreeMap user = null;
			if (json != null)
				user = gson.fromJson(json, LinkedTreeMap.class);
			String correctPassword = null;
			if (user != null)
				correctPassword = (String) user.get("password");
			log.debug(String.format(
					"TOGREP | User %s is logging in with password: %s - Compare encode: user input: %s vs cache: %s = %s",
					userName, password, edu.encodePassword(password), correctPassword,
					correctPassword.equals(edu.encodePassword(password))));
			if (user != null && correctPassword != null && correctPassword.equals(edu.encodePassword(password))) {
				String strUserInfo = gson.toJson(user);
				String callBackKey = getSessionStringAttribute(obj, "callback-url");
				removeSessionAttribute(obj, "callback-url");
				if (callBackKey == null || "0".equals(forward)) {
					boolean checkApp = false;
					List<String> apps = (List) user.get("appid");
					log.debug(String.format("TOGREP | User %s app lists ", userName) + apps);
					if (apps != null) {
						for (String appId : apps) {
							Map row = (Map) StartApp.hicache.getStoreAttribute("application", appId);
							if ("bi_authen".equals(row.get("app_code"))) {
								checkApp = true;
								break;
							}
						}
					} else {
						// Workaround, allow access authen app
						checkApp = true;

					}
					if ("root".equals(userName) || checkApp) {
						String newCookie = UUID.randomUUID().toString();
						StartApp.hicache.createStore(newCookie, 86400000l);
						StartApp.hicache.setStoreAttribute(newCookie, "sso_username", strUserInfo);
						obj.put("cookie", newCookie);
						obj.put("username", userName);
						sendLoginResponse(obj, LOGIN_SUCCESS, 0);
					} else {
						log.info(String.format("TOGREP User %s failed checkApp", userName));
						failCount++;
						setSessionAttribute(obj, "ssoFailCount", "" + failCount);
						sendLoginResponse(obj, NO_APPS_GRANTED, failCount);
					}
				} else {
					List lstData = (List) StartApp.hicache.getStoreAttribute(callBackKey, "callback-url");
					String callBack = (String) lstData.get(0);
					String appCode = (String) lstData.get(1);
					List<String> apps = (List) user.get("appid");
					// log.info("ABCDEF Get app_code: " + appCode);
					boolean checkApp = false;
					if (apps != null) {
						for (String appId : apps) {
							// log.info("ABCDEF loop app_id: " + appId);
							Map row = (Map) StartApp.hicache.getStoreAttribute("application", appId);
							// log.info("ABCDEF loop app_code: " + row.get("app_code"));
							if (appCode.equals(row.get("app_code"))) {
								checkApp = true;
								break;
							}
						}
					} else {
						log.info("ABCDEF apps null");
					}
					// log.info("ABCDEF checkApp " + checkApp);
					log.info("Get session callback-url: " + callBack + " storeName: " + "login_"
							+ (String) obj.get("access-token"));
					if (checkApp) {
						// Check if user password is expired, and force user to change password
						if (isPasswordExpiry(obj)) {
							// If user's password is expired, then deny user to login
							sendLoginResponse(obj, PASSWORD_EXPIRED, 0);
							return;
						}
						// End check password expiry
						String newCookie = UUID.randomUUID().toString();
						StartApp.hicache.createStore(newCookie, 86400000l);
						StartApp.hicache.setStoreAttribute(newCookie, "sso_username", strUserInfo);
						obj.put("call_back", callBack + "&sso-token=" + newCookie);
						obj.put("cookie", newCookie);
						sendLoginResponse(obj, LOGIN_SUCCESS, 0);
					} else {
						failCount++;
						setSessionAttribute(obj, "ssoFailCount", "" + failCount);
						sendLoginResponse(obj, LOGIN_INCORRECT, failCount);
					}
				}
			} else {
				failCount++;
				setSessionAttribute(obj, "ssoFailCount", "" + failCount);
				sendLoginResponse(obj, LOGIN_INCORRECT, failCount);
			}
		}
		obj.put("result-code", "000");
		UpdateTransToDBThread.transQueue.offer(obj);
	}

	private boolean isPasswordExpiry(LinkedTreeMap msg) {
		String expiryDate = StartApp.hicache.getStringAttribute("expiry_password", msg.get("userName").toString());
		Calendar cal = Calendar.getInstance();
		Calendar expiryCal = dateUtils.stringToCalendar(expiryDate, "yyyy-MM-dd");
		if (expiryCal.compareTo(cal) == -1) {
			log.info(String.format("TOGREP | User %s password is expiried", msg.get("userName").toString()));
			return true;
		}
		return false;
	}

	public void sendLoginResponse(LinkedTreeMap request, String[] status, Integer failCount) throws Exception {
		LinkedTreeMap<String, String> data = new LinkedTreeMap<>();
		data.put("status", status[0]);
		if (failCount == null) {
			failCount = 0;
		}
		data.put("message", status[1]);
		data.put("call_back", (String) request.get("call_back"));
		data.put("failCount", failCount.toString());
		data.put("accessToken", (String) request.get("cookie"));
		String json = new Gson().toJson(data);
		returnStringToFrontend(request, json);
	}
}
