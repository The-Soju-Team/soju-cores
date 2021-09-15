/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.jar.Attributes;

public class JarUtils {

    static Logger logger = Logger.getLogger("Loader");
    static ArrayList<String> urls = new ArrayList();

    public static String getMainClassName(URL url)
            throws IOException {
        URL u = new URL("jar", "", url.toString() + "!/");
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
    }

    public static void addFile(String s)
            throws IOException, PrivilegedActionException {
        File f = new File(s);
        addFile(f);
    }

    public static void addFile(File f)
            throws IOException, PrivilegedActionException {
        if ((f.exists()) && ((f.getPath().endsWith(".jar")) || (f.getPath().endsWith(".zip")))) {
            addURL(f.toURI().toURL());
        }
    }

    public static void addURL(final URL u)
            throws IOException, PrivilegedActionException {
        for (String s : urls) {
            if (s.equals(u.toString())) {
                return;
            }
        }
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run()
                    throws IOException {
                URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class sysclass = URLClassLoader.class;
                try {
                    Class parameters = URL.class;
                    Method method = sysclass.getDeclaredMethod("addURL", new Class[]{parameters});
                    method.setAccessible(true);
                    method.invoke(sysloader, u);
                    return sysloader;
                } catch (Throwable t) {
                    throw new IOException("Error, could not add URL to system classloader");
                }
            }
        });
    }

    public static void addDir(String jarDir)
            throws MalformedURLException, IOException, PrivilegedActionException {
        File dir = new File(jarDir);
        File[] f = dir.listFiles();
        if ((f != null) && (f.length != 0)) {
            for (File ff : f) {
                if (ff.isDirectory()) {
                    addDir(ff.getPath());
                } else if (ff.getName().endsWith(".jar")) {
                    addFile(ff);
                }
            }
        }
    }

    public static URL toUrl(String file)
            throws MalformedURLException {
        return new File(file).toURI().toURL();
    }

    public static <T> T loadClass(String c)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        logger.info("load class  :" + c);
        return (T) Class.forName(c).newInstance();
    }

    public static <T> T loadClass(String jar, String c)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, PrivilegedActionException {
        addFile(jar);
        return (T) loadClass(c);
    }
}
