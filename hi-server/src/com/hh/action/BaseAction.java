/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.action;

import com.google.gson.Gson;
import com.hh.net.httpserver.Headers;
import com.hh.server.HHServer;
import com.hh.server.WebImpl;
import com.hh.util.FileUtils;
import com.hh.utils.HDFSUtils;
import com.hh.web.HttpUtils;
import com.hh.web.PageFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * @author HienDM
 */

public class BaseAction {
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaseAction.class.getSimpleName());
    private static HashMap fileResources = new HashMap();
    private static HashMap cacheResource = new HashMap();
    public HttpUtils httpUtils;
    public HashMap returnData = new HashMap();

    public BaseAction(HttpUtils hu) {
        httpUtils = hu;
    }

    public boolean returnPage(String name) throws IOException {
        PageFactory pf = WebImpl.getPageFactory(name);
        if (pf != null) {
            returnPage(name, pf.getTemplate(), pf.getChildPages(), 200);
            return true;
        } else return false;
    }

    public void returnPage(String name, String template, HashMap<String, Object> child, int statusCode) throws IOException {
        byte[] data = new byte[1];
        String content = null;
        HashMap pageConfig = (HashMap) fileResources.get("page_" + name);
        String contextPath = template.substring(0, template.indexOf("/"));
        String cacheFile = contextPath + "/share/cache/" + name.replace("/", "-") + ".appcache";

        boolean reload = false;
        if (pageConfig == null) {
            pageConfig = new HashMap();
            reload = true;
        } else {
            if ("true".equals(HHServer.config.getConfig("use-browser-cache"))) {
                File fileCache = new File("../app/" + cacheFile);
                if (fileCache.exists()) {
                    FileUtils fu = new FileUtils();
                    String cacheContent = fu.readFileToString("../app/" + cacheFile, FileUtils.UTF_8);
                    String[] properties = cacheContent.split(System.getProperty("line.separator").toString());
                    for (String property : properties) {
                        if (property != null && property.trim().length() > 9 && property.trim().substring(0, 9).equals("../../../")) {
                            File file = new File("../app/" + property.substring(9));
                            if (file.exists()) {
                                if (cacheResource.get(property.substring(9)) == null ||
                                        file.lastModified() != (long) cacheResource.get(property.substring(9))) {
                                    reload = true;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    reload = true;
                }
            }

            File templateFile = new File("../app/" + template);
            if (templateFile.exists())
                if (templateFile.lastModified() != (long) pageConfig.get("template"))
                    reload = true;
            if (!reload && child != null) {
                for (Map.Entry<String, Object> entry : child.entrySet()) {
                    String childPath = (String) entry.getKey();
                    File file = new File("../app/" + childPath);
                    if (file.exists()) {
                        if (file.lastModified() != (long) pageConfig.get(childPath)) {
                            reload = true;
                            break;
                        }
                    }
                }
            }
        }

        if (reload) {
            FileUtils fu = new FileUtils();
            File templateFile = new File("../app/" + template);
            if (templateFile.exists()) {
                pageConfig.put("template", templateFile.lastModified());
                content = fu.readFileToString(templateFile, FileUtils.UTF_8);
                if (child != null) {
                    for (Map.Entry<String, Object> entry : child.entrySet()) {
                        String childPath = (String) entry.getKey();
                        File file = new File("../app/" + childPath);
                        if (file.exists()) {
                            String childContent = fu.readFileToString(file, FileUtils.UTF_8);
                            content = content.replace("<!-- " + entry.getValue() + " -->", childContent);
                            pageConfig.put(childPath, file.lastModified());
                        }
                    }
                }
            }

            if ("true".equals(HHServer.config.getConfig("use-browser-cache"))) {
                // reload client cache
                int index = content.indexOf("<html") + 5;
                content = "<html manifest=\"/" + cacheFile + "\" " + content.substring(index);
                reloadClientCache(content, "../app/" + cacheFile);
            }

            pageConfig.put("content", content);
            fileResources.put("page_" + name, pageConfig);

        } else {
            content = (String) pageConfig.get("content");
        }

        content = httpUtils.returnFilter.execute(content, httpUtils.getCookie("local"));

        data = content.getBytes(Charset.forName(FileUtils.UTF_8));

        httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        httpUtils.httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
        httpUtils.httpExchange.sendResponseHeaders(statusCode, 0);
        try (GZIPOutputStream os = new GZIPOutputStream(httpUtils.httpExchange.getResponseBody());) {
            os.write(data);
            os.close();
        }
    }

    private void reloadClientCache(String webContent, String cacheFile) {
        cacheFile = cacheFile.replace("/", File.separator);
        File cache = new File(cacheFile);
        if (!cache.exists()) {
            Pattern scriptPattern = Pattern.compile("src=\"(.*?)\"");
            Matcher scriptMatcher = scriptPattern.matcher(webContent);
            List lstString = new ArrayList();
            while (scriptMatcher.find()) {
                lstString.add(scriptMatcher.group(1));
            }

            Pattern cssPattern = Pattern.compile("href=\"(.*?)css\"");
            Matcher cssMatcher = cssPattern.matcher(webContent);
            while (cssMatcher.find()) {
                lstString.add(cssMatcher.group(1) + "css");
            }

            StringBuilder cacheContent = new StringBuilder();
            cacheContent.append("CACHE MANIFEST\n");
            cacheContent.append("# version: ");
            cacheContent.append((new Date()).getTime());
            cacheContent.append("\n");
            cacheContent.append("# Explicitly cached entries\n");

            for (int i = 0; i < lstString.size(); i++) {
                cacheContent.append("../../../");
                File resourceFile = new File("../app/" + lstString.get(i));
                if (resourceFile.exists()) cacheResource.put(lstString.get(i), resourceFile.lastModified());
                cacheContent.append(lstString.get(i));
                cacheContent.append("\n");
            }

            cacheContent.append("\n");
            cacheContent.append("# offline will be displayed if the user is offline\n");
            cacheContent.append("FALLBACK:\n");
            cacheContent.append("\n");
            cacheContent.append("# All other resources (e.g. sites) require the user to be online. \n");
            cacheContent.append("NETWORK:\n");
            cacheContent.append("*\n");
            cacheContent.append("\n");
            cacheContent.append("# Additional resources to cache\n");
            cacheContent.append("CACHE:");

            try {
                cache.createNewFile();
                FileUtils fu = new FileUtils();
                fu.writeStringToFile(cacheContent.toString(), cacheFile, FileUtils.UTF_8);
            } catch (Exception ex) {
                log.error("Error when write cache file", ex);
            }
        }
    }

    public void returnDownloadFile(String filePath) throws FileNotFoundException, IOException {
        String extension = FileUtils.extractFileExt(filePath);
        File resultFile = new File(filePath);
        try (InputStream is = new FileInputStream(resultFile);) {
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            httpUtils.httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"item." + extension + "\"");
            httpUtils.httpExchange.sendResponseHeaders(200, resultFile.length());
            try (OutputStream os = httpUtils.httpExchange.getResponseBody();) {
                IOUtils.copy(is, os);
                is.close();
                os.close();
            }
        }
    }

    public void returnDownloadFile(String fileName, String hdfsPath) throws Exception {
        log.info("TOGREP | Returning Download File");
        String downloadedFile = HDFSUtils.downLoadFromHDFS166ToLocal(hdfsPath);
        if (downloadedFile != null) {
            byte[] buf = new byte[1024];
            InputStream is = null;
            ByteArrayOutputStream out = null;
            try {
                is = new BufferedInputStream(new FileInputStream(downloadedFile));
                out = new ByteArrayOutputStream();
                int n = 0;
                while (-1 != (n = is.read(buf))) {
                    out.write(buf, 0, n);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
                if (is != null) {
                    is.close();
                }
            }
            byte[] response = out.toByteArray();
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            httpUtils.httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            httpUtils.httpExchange.getResponseHeaders().add("Content-Length", response.length + "");
            httpUtils.httpExchange.sendResponseHeaders(200, response.length);
            log.info("TOGREP | Returning Download File Output Stream Writing");
            try (OutputStream os = httpUtils.httpExchange.getResponseBody();) {
                os.write(response);
                os.close();
            }
            log.info("TOGREP | Returning Download File Done Output Stream Writing");
        } else {
            log.info("TOGREP | Cannot download file");
        }
    }

    public void returnDownloadFile(byte[] data, String fileName) throws FileNotFoundException, IOException {
        log.info("TOGREP | Returning Download File");
        httpUtils.httpExchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
        httpUtils.httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        httpUtils.httpExchange.getResponseHeaders().add("Content-Length", data.length + "");
        httpUtils.httpExchange.sendResponseHeaders(200, data.length);
        log.info("TOGREP | Returning Download File Output Stream Writing");
        try (OutputStream os = httpUtils.httpExchange.getResponseBody();) {
            os.write(data);
            os.close();
        }
        log.info("TOGREP | Returning Download File Done Output Stream Writing");
    }

    public void returnFile(String filePath) throws FileNotFoundException, IOException {
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        File resultFile = new File(filePath);
        String mime = HHServer.config.getMimeType(FileUtils.extractFileExt(fileName));
        try (InputStream is = new FileInputStream(resultFile);) {
            httpUtils.httpExchange.getResponseHeaders().set("Content-Type", mime);
            httpUtils.httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            httpUtils.httpExchange.sendResponseHeaders(200, resultFile.length());
            try (OutputStream os = httpUtils.httpExchange.getResponseBody();) {
                IOUtils.copy(is, os);
                is.close();
                os.close();
            }
        }
    }

    public void returnFile(byte[] data, String fileName) throws FileNotFoundException, IOException {
        String mime = HHServer.config.getMimeType(FileUtils.extractFileExt(fileName));
        Headers responseHeaders = httpUtils.httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", mime);
        responseHeaders.set("Content-Encoding", "gzip");

        httpUtils.httpExchange.sendResponseHeaders(200, 0);
        try (GZIPOutputStream os = new GZIPOutputStream(httpUtils.httpExchange.getResponseBody());) {
            os.write(data);
            os.close();
        }
    }

    public void setAjaxData(String key, Object value) {
        returnData.put(key, value);
    }

    public void returnAjax() throws IOException {
        httpUtils.sendJsonResponse(200, (new Gson()).toJson(returnData));
    }

    public void returnAjax(Object data) throws IOException {
        httpUtils.sendStringResponse(200, (new Gson()).toJson(data));
    }

    public void returnAjax(String key, Object value) throws IOException {
        setAjaxData(key, value);
        httpUtils.sendStringResponse(200, (new Gson()).toJson(returnData));
    }

    public void returnString(String text) throws IOException {
        httpUtils.sendStringResponse(200, text);
    }

    public void returnNotFound() throws IOException {
        httpUtils.sendNotFoundResponse();
    }
}
