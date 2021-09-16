package com.hh.util.viettel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static String postDownloadFile(String endpoint, Map<String, String> params, String savePath) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(new StringEntity(OBJECT_MAPPER.writeValueAsString(params)));
            HttpResponse resp = client.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            if (entity != null && resp.getStatusLine().getStatusCode() == 200) {
                InputStream is = entity.getContent();
                File f = new File(savePath);
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    LOG.info(String.format("Writing to %s", savePath));
                    int read;
                    byte[] bytes = new byte[8192];
                    while ((read = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, read);
                    }
                    LOG.info(String.format("Done writing file to %s", savePath));
                    return savePath;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Object, Object> get(String endpoint, Map<String, String> params) {
        try {
            HttpClient client = new DefaultHttpClient();
            URIBuilder uriBuilder = new URIBuilder(endpoint);

            for (String key : params.keySet()) {
                uriBuilder.setParameter(key, params.get(key));
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            HttpResponse resp = client.execute(httpGet);
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                return OBJECT_MAPPER.readValue(result, HashMap.class);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Object, Object> postJson(String endpoint, Map<String, String> params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(endpoint);
            StringEntity body = new StringEntity(OBJECT_MAPPER.writeValueAsString(params));
            httpPost.setEntity(body);
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse resp = client.execute(httpPost);
            LOG.info(String.format("Status code for endpoint: %s -> %d", endpoint, resp.getStatusLine().getStatusCode()));
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                return OBJECT_MAPPER.readValue(result, HashMap.class);
            }
            return null;
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
