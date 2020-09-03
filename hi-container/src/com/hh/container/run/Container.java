/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.container.run;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.log4j.Level;

/**
 *
 * @author HienDM
 */
public class Container {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StartApp.class.getSimpleName());

	public static void execute(String command) throws Exception {
		if (command.equals("start"))
			log.info("=============== STARTING... ===============");
		if (command.equals("stop"))
			log.info("=============== STOPING... ===============");
		Properties prop = new Properties();
		String configPath = "../etc/container.conf";
		File file = new File(configPath);
		if (file.exists()) {
			try (FileInputStream inputStream = new FileInputStream(configPath)) {
				prop.load(inputStream);
			} catch (Exception ex) {
				log.error("Error when load server.conf", ex);
			}
		}

		String logLevel = prop.getProperty("log-level");
		log.info("log level: " + logLevel);
		if (logLevel == null || logLevel.isEmpty()) {
			logLevel = "ERROR";
		}
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

		int count = 1;
		while (prop.getProperty("app" + count + "." + command + ".class") != null) {
			log.info("------------------------------------------------");
			log.info((command + " application: " + prop.getProperty("app" + count + ".lib-path")).toUpperCase());
			log.info("------------------------------------------------");
			JarUtils.addDir(prop.getProperty("app" + count + ".lib-path"));
			Method start = Class.forName(prop.getProperty("app" + count + "." + command + ".class")).getMethod(command,
					String.class);
			start.invoke(null, prop.getProperty("app" + count + ".config-path"));
			count++;
		}
		if (command.equals("start"))
			log.info("=============== START COMPLETED ===============");
		if (command.equals("stop"))
			log.info("=============== STOP COMPLETED ===============");
	}
}
