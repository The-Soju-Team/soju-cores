/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.connector.server.Server;
import com.hh.util.EncryptDecryptUtils;
import com.hh.util.FileUtils;
import com.hh.web.FileInfo;
import com.viettel.authen.db.dao.UserDao;
import com.viettel.authen.db.daoImpl.AppDaoImpl;
import com.viettel.authen.db.daoImpl.UserDaoImpl;
import com.viettel.authen.run.ServerProcess;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.viettel.authen.global.CommandConstants;
import com.viettel.authen.run.StartApp;
import com.viettel.authen.run.UpdateTransToDBThread;
import org.apache.log4j.Logger;

/**
 *
 * @author HienDM
 */
public class UserProcess extends ServerProcess {
	private static Logger log = Logger.getLogger(UserProcess.class.getSimpleName());
	private static Gson gson = new Gson();
	private static final String JSON_TYPE = "application/json";

	public UserProcess(ChannelHandlerContext ctx, Server server) {
		super(ctx, server);
	}

	@Override
	public void process(LinkedTreeMap msg) throws Exception {
		HashMap<String, String> result = new HashMap<>();
		String cmd = (String) msg.get(CommandConstants.COMMAND);
		if (isPermissionGranted(msg)) {
			switch (cmd) {
			case CommandConstants.UPLOAD_USER_PIC:
				uploadUserPic(msg);
				break;
			case CommandConstants.ADD_USER:
				addUser(msg);
				break;
			case CommandConstants.SEARCH_USER:
				searchUser(msg);
				break;
			case CommandConstants.LOAD_VIEW_USER:
				loadViewUser(msg);
				break;
			case CommandConstants.UPDATE_USER:
				updateUser(msg);
				break;
			case CommandConstants.VIEW_USER_IMAGE:
				viewUserImage(msg);
				break;
			case CommandConstants.LOAD_APP_DATA:
				loadAppData(msg);
			default:
				this.returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
				break;
			}
		} else {
			result.put("message", "You are not allowed to do this action");
			this.returnDataToFrontend(msg, gson.toJson(result), JSON_TYPE);
		}
		msg.put("result-code", "000");
		UpdateTransToDBThread.transQueue.offer(msg);
	}

	private boolean isPermissionGranted(LinkedTreeMap msg) {
		HashMap userInfo = gson.fromJson(
				StartApp.hicache.getStringAttribute(msg.get("access-token").toString(), "sso_username"), HashMap.class);
		double userType = Double.parseDouble(userInfo.get("user_type").toString());
		if (userType == 1.0) { // Well if he is a administrator, let him do whatever he wants
			return true;
		}
		String cmd = (String) msg.get(CommandConstants.COMMAND);
		switch (cmd) {
		case CommandConstants.UPLOAD_USER_PIC:
			break;
		case CommandConstants.ADD_USER:
			return false;
		case CommandConstants.SEARCH_USER:
			break;
		case CommandConstants.LOAD_VIEW_USER:
			try {
				Map user = (new UserDaoImpl()).getUserById(Integer.parseInt((String) msg.get("userid")));
				if (userInfo.get("user_name").equals(user.get("user_name"))) {
					return true;
				} else {
					log.debug(String.format("TOGREP | User %s is trying to inject viewing user %s ==== DENY ====",
							userInfo.get("user_name").toString(), user.get("user_name")));
					return false;
				}
			} catch (NumberFormatException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		case CommandConstants.UPDATE_USER:
			try {
				Map user = (new UserDaoImpl()).getUserById(Integer.parseInt((String) msg.get("userid")));
				if (userInfo.get("user_name").equals(user.get("user_name"))) {
					return true;
				} else {
					log.debug(String.format("TOGREP | User %s is trying to inject updating user %s ==== DENY ====",
							userInfo.get("user_name").toString(), user.get("user_name")));
					return false;
				}
			} catch (NumberFormatException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		case CommandConstants.VIEW_USER_IMAGE:
			break;
		case CommandConstants.LOAD_APP_DATA:
			break;
		default:
			break;
		}
		return true;
	}

	public void uploadUserPic(LinkedTreeMap msg) throws Exception {
		if (msg.get("file") != null) {
			String filePath = StartApp.config.getConfig("DataDirectory") + File.separator + "user";
			if (!filePath.isEmpty()) {
				filePath = filePath.replace("/", File.separator);
				filePath = filePath.replace("\\", File.separator);
				Calendar c = Calendar.getInstance();
				filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator
						+ (c.get(Calendar.MONTH) + 1) + File.separator + c.get(Calendar.DAY_OF_MONTH) + File.separator;
				File directory = new File(filePath);
				if (!directory.exists())
					directory.mkdirs();

				FileInfo fileInfo = (FileInfo) msg.get("file");
				String fileName = "" + (new Date()).getTime() + "_" + UUID.randomUUID().toString()
						+ FileUtils.extractFileExt(fileInfo.getFilename());
				HashMap data = new HashMap();
				data.put("localFileName", fileInfo.getFilename());
				data.put("serverFileName", fileName);

				FileOutputStream fios = new FileOutputStream(filePath + fileName);
				fios.write(fileInfo.getBytes());
				fios.close();

				String json = new Gson().toJson(data);
				returnStringToFrontend(msg, json);
			}
		} else {
			returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
		}
	}

	public void searchUser(LinkedTreeMap msg) throws Exception {
		UserDao udb = new UserDaoImpl();
		if (msg.get("isdelete") != null && msg.get("isdelete").equals("1")) {
			String deleteUsers = (String) msg.get("userid");
			if (deleteUsers != null) {
				deleteUsers = deleteUsers.replace("userid=", "");
				deleteUsers = deleteUsers.replace("&", ",");
				udb.deleteUser(deleteUsers);

				String[] arrUserId = deleteUsers.split(",");
				for (int i = 0; i < arrUserId.length; i++) {
					String userName = StartApp.hicache.getStringAttribute("credentials_id", arrUserId[i]);
					StartApp.hicache.deleteStoreAttribute("credentials_id", arrUserId[i]);
					StartApp.hicache.deleteStoreAttribute("credentials", userName);
				}
			}
		}

		int pageLength = 10;
		if (msg.get("length") != null) {
			pageLength = Integer.parseInt((String) msg.get("length"));
			if (pageLength == 0)
				pageLength = 10;
		}
		// Get user name from cache using access_token
		String userName = gson
				.fromJson(StartApp.hicache.getStringAttribute(msg.get("access-token").toString(), "sso_username"),
						HashMap.class)
				.get("user_name").toString();

		int numberRow = 0;
		if (msg.get("start") != null) {
			numberRow = Integer.parseInt((String) msg.get("start"));
		}

		String name = (String) msg.get("username");
		String mobile = (String) msg.get("mobile");
		String fullName = (String) msg.get("fullname");
		String fromdate = (String) msg.get("fromdate");
		String todate = (String) msg.get("todate");

		Integer userType = null;
		if (msg.get("usertype") != null)
			userType = Integer.parseInt((String) msg.get("usertype"));

		if (!(name != null && !name.trim().isEmpty()))
			name = null;
		if (!(mobile != null && !mobile.trim().isEmpty()))
			mobile = null;
		if (!(fullName != null && !fullName.trim().isEmpty()))
			fullName = null;
		Date fromDate = null;
		if (fromdate != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			fromDate = df.parse(fromdate.trim() + " 00:00:00");
		}
		Date toDate = null;
		if (todate != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			toDate = df.parse(todate.trim() + " 23:59:59");
		}

		List<List> listResult = udb.searchUser(userName, numberRow, pageLength, name, mobile, fullName, userType,
				fromDate, toDate);
		List<List> listData = listResult.get(1);
		for (int i = 0; i < listData.size(); i++) {
			listData.get(i).set(0, numberRow + i + 1);
		}
		Integer count = Integer.parseInt(((List<List>) listResult.get(0)).get(0).get(0).toString());
		HashMap returnData = new HashMap();
		returnData.put("recordsTotal", count);
		returnData.put("recordsFiltered", count);
		returnData.put("data", listData);
		returnStringToFrontend(msg, new Gson().toJson(returnData));
	}

	public void loadAppData(LinkedTreeMap msg) throws Exception {
		HashMap returnData = new HashMap();
		returnData.put("apps", (new AppDaoImpl()).getAllApps());
		returnStringToFrontend(msg, new Gson().toJson(returnData));
	}

	public void addUser(LinkedTreeMap msg) throws Exception {
		String userName = (String) msg.get("username");
		String hoten = (String) msg.get("fullname");
		String mobile = (String) msg.get("mobile");
		String birthday = (String) msg.get("birthday");
		String password = (String) msg.get("password");
		String userType = (String) msg.get("usertype");
		String email = (String) msg.get("email");

		HashMap user = new HashMap();

		List lstParam = new ArrayList();

		if (userName != null) {
			lstParam.add(userName.trim());
			user.put("user_name", userName.trim());
		} else
			lstParam.add(null);

		if (hoten != null) {
			lstParam.add(hoten.trim());
			user.put("full_name", hoten.trim());
		} else
			lstParam.add(null);

		if (mobile != null) {
			lstParam.add(mobile.trim());
			user.put("mobile", mobile.trim());
		} else
			lstParam.add(null);

		if (email != null) {
			lstParam.add(email.trim());
			user.put("email", email.trim());
		} else
			lstParam.add(null);

		if (birthday != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			Date birthDate = df.parse(birthday.trim());
			lstParam.add(birthDate);
			user.put("birthday", birthDate);
		} else
			lstParam.add(null);

		if (password != null) {
			String passwordEncode = (new EncryptDecryptUtils().encodePassword(password.trim()));
			lstParam.add(passwordEncode);
			user.put("password", passwordEncode);
		} else {
			lstParam.add(null);
			HashMap returnData = new HashMap();
			returnData.put("error_code", "createuser_02");
			returnData.put("error_message", "Mật khẩu bắt buộc nhập");
			returnData.put("response_message", "Hãy nhập mật khẩu");
			returnStringToFrontend(msg, new Gson().toJson(returnData));
			return;
		}

		Integer intUserType = null;
		if (userType != null && !userType.trim().isEmpty()) {
			intUserType = Integer.parseInt(userType);
			lstParam.add(intUserType);
			user.put("user_type", userType);
		} else
			lstParam.add(null);

		lstParam.add(new Date());
		user.put("create_date", new Date());

		Integer userId = null;
		UserDaoImpl udi = new UserDaoImpl();
		try {
			userId = udi.insertUser(lstParam);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex.getMessage().contains("Duplicate entry")) {
				HashMap returnData = new HashMap();
				returnData.put("error_code", "createuser_04");
				returnData.put("error_message", "Số điện thoại đã tồn tại");
				returnData.put("response_message", "Số điện thoại đã tồn tại");
				returnStringToFrontend(msg, new Gson().toJson(returnData));
				return;
			} else {
				HashMap returnData = new HashMap();
				returnData.put("error_message", "Đã có lỗi xảy ra");
				returnData.put("response_message", "Đã có lôi dảy ra");
				return;
			}
		}
		List appIds = new ArrayList();
		if (msg.get("appid") instanceof String) {
			appIds.add(msg.get("appid"));
		} else {
			appIds = (List) msg.get("appid");
		}
		udi.updateUserApp(userId, appIds);

		user.put("user_id", userId);
		user.put("appid", appIds);
		returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
	}

	public void loadViewUser(LinkedTreeMap msg) throws Exception {
		Map user = (new UserDaoImpl()).getUserById(Integer.parseInt((String) msg.get("userid")));
		returnStringToFrontend(msg, new Gson().toJson(user));
	}

	public void getMyInfo(LinkedTreeMap msg) throws IOException, SQLException {

	}

	public void viewUserImage(LinkedTreeMap msg) throws Exception {
		String filePath = "";
//        resHeader.set("Content-Type", "image/jpeg");
		String fileName = (String) msg.get("filename");
		if (fileName.contains("_")) {
			String[] arrData = fileName.split("_");
			long createTime = Long.parseLong(arrData[0]);
			Calendar c = Calendar.getInstance();
			c.setTime(new Date(createTime));
			filePath = StartApp.config.getConfig("DataDirectory") + File.separator + "user";
			filePath = filePath.replace("/", File.separator);
			filePath = filePath.replace("\\", File.separator);
			filePath = filePath + File.separator + c.get(Calendar.YEAR) + File.separator + (c.get(Calendar.MONTH) + 1)
					+ File.separator + c.get(Calendar.DAY_OF_MONTH) + File.separator;
		}
		String path = filePath + fileName;
		File tmpFile = new File(path);
		if (!tmpFile.exists())
			path = System.getProperty("user.dir") + File.separator + "share" + File.separator + "assets"
					+ File.separator + "images" + File.separator + "users" + File.separator + "no-image.jpg";
		returnFileToFrontend(msg, path);
	}

	public void updateUser(LinkedTreeMap msg) throws Exception {
		// ------- Update user --------------------------------------------
		String userId = (String) msg.get("userid");

		String userName = (String) msg.get("username");
		String fullName = (String) msg.get("fullname");
		String mobile = (String) msg.get("mobile");
		String birthday = (String) msg.get("birthday");
		String password = (String) msg.get("password");
		String userType = (String) msg.get("usertype");
		String email = (String) msg.get("email");

		List lstParam = new ArrayList();

		if (userName != null)
			lstParam.add(userName.trim());
		else
			lstParam.add(null);

		if (fullName != null)
			lstParam.add(fullName.trim());
		else
			lstParam.add(null);

		if (mobile != null)
			lstParam.add(mobile.trim());
		else
			lstParam.add(null);

		if (birthday != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			Date birthDate = df.parse(birthday.trim());
			lstParam.add(birthDate);
		} else
			lstParam.add(null);

		if (email != null)
			lstParam.add(email.trim());
		else
			lstParam.add(null);

		Integer intUserType = null;
		if (userType != null && !userType.trim().isEmpty()) {
			intUserType = Integer.parseInt(userType);
			lstParam.add(intUserType);
		} else
			lstParam.add(null);

		boolean changePassword = false;
		if (password != null && !password.trim().isEmpty()) {
			lstParam.add((new EncryptDecryptUtils()).encodePassword(password.trim()));
			changePassword = true;
		}

		Integer intUserId = null;
		if (userId != null && !userId.trim().isEmpty()) {
			intUserId = Integer.parseInt(userId);
			lstParam.add(intUserId);
		} else
			lstParam.add(null);

		UserDaoImpl udi = new UserDaoImpl();
		try {
			udi.updateUser(lstParam, changePassword);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex.getMessage().contains("Duplicate entry")) {
				HashMap returnData = new HashMap();
				returnData.put("error_code", "createuser_04");
				returnData.put("error_message", "Số điện thoại đã tồn tại");
				returnData.put("response_message", "Số điện thoại đã tồn tại");
				returnStringToFrontend(msg, new Gson().toJson(returnData));
				return;
			}
			log.error("Error when update user to db", ex);
		}

		List appIds = new ArrayList();
		if (msg.get("appid") instanceof String) {
			appIds.add(msg.get("appid"));
		} else {
			appIds = (List) msg.get("appid");
		}
		udi.updateUserApp(intUserId, appIds);

//		Map user = udi.getUserById(intUserId);
//		user.put("appid", appIds);
		returnStringToFrontend(msg, new Gson().toJson(new HashMap()));
	}
}
