/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.util.viettel;

import com.google.gson.Gson;
import com.hh.util.ConfigUtils;
import com.hh.util.StringUtils;

// import com.viettel.analysis.run.StartApp;
import it.sauronsoftware.cron4j.Scheduler;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
// import sendmt.MtStub;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author donnn
 */
public class SMSUtils {

    private static ConfigUtils config;
    // private static MtStub stub;
    private static String url;
    private static String xmlns;
    private static String user;
    private static String pass;
    private static String sender;
    private static String schedulePattern = "*/5 * * * *";

    private static Scheduler scheduler;

    private static Gson gson = new Gson();

    private static ConcurrentHashMap<String, ArrayList<Object>> smsQueue = null;

    private static void init() {
        // config = StartApp.config;
        // url = config.getConfig("sms-url");
        // if ((url == null) || (url.length() < 1)) {
        //     System.out.println("Cannot initialize SMSlUtils.");
        //     System.out.println("Config credentials in etc/server.conf first.");
        // }

        //
        // scheduler = new Scheduler();
        // scheduler.schedule(schedulePattern, new Runnable() {
        // @Override
        // public void run() {
        // flushSMS();
        // }
        // });
        // scheduler.start();

        // Init queue
        smsQueue = new ConcurrentHashMap<String, ArrayList<Object>>();

        // Read config
        xmlns = config.getConfig("sms-xmlns");
        user = config.getConfig("sms-user");
        pass = config.getConfig("sms-pass");
        sender = config.getConfig("sms-sender");
        // stub = new MtStub(url, xmlns, user, pass);
    }

    public static void sendSMS(String receiver, String content) {
        try {
            sendViSMS(receiver, content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Send SMS
     *
     * @param receiver MSISDN to send. Ex: "84345678901"
     * @param content  SMS Content
     */

    // public static void sendSMSOld(String receiver, String content) {
    //     if (stub == null) {
    //         init();
    //     }
    //     int error = stub.send("0", "sendmt", sender, receiver, "0", content, "1");
    //     if (error == 0) {
    //         logSMS(receiver, content);
    //         System.out.println("SMS Sent Successfully");
    //     } else {
    //         System.out.println("Can not send SMS");
    //     }
    // }

    public static void sendViSMS(String receiver, String content) throws ClientProtocolException, IOException {
        sendViSMS(receiver, content, "DMP_MONITOR");
    }

    /**
     * Send SMS with Vietnamese content
     *
     * @param receiver MSISDN to send. Ex: "84345678901"
     * @param content  SMS Content
     * @throws IOException
     * @throws ClientProtocolException
     */

    public static void sendViSMS(String receiver, String content, String sender) throws ClientProtocolException, IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://10.58.244.170:9110/sms");

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("sender", sender));
        params.add(new BasicNameValuePair("receiver", receiver));
        params.add(new BasicNameValuePair("text", content));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpClient.execute(httppost);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Map m = gson.fromJson(responseString, HashMap.class);
            if ("success".equals(m.get("status"))) {
                System.out.println("SMS Sent Successfully via DMP");
                // logSMS(receiver, content);
            } else {
                System.out.println("Can not send SMS via DMP");
                System.out.println(responseString);
            }
        } catch (Exception e) {
            System.out.println("Can not send SMS via DMP: " + e.getMessage());
        }
    }

    public static void queueSMS(String receiver, String content) {
        queueSMS(receiver, content, new HashMap());
    }

    public static void queueSMS(String receiver, String content, Map extra) {
        // if (stub == null) {
        //     init();
        // }
        System.out.println("Now add a sms to queue of " + receiver);
        Map<String, String> sms = new HashMap<String, String>();
        sms.put("receiver", receiver);
        String group = (extra.get("group") == null) ? "" : (String) extra.get("group");
        sms.put("group", (String) extra.get("group"));
        sms.put("content", content);
        ArrayList queuedSMS = smsQueue.get(receiver);
        if (queuedSMS != null) {
            // this user has already had some pending messages
            queuedSMS.add(sms);
        } else {
            // init new queue for this user, update the main queue
            queuedSMS = new ArrayList();
            queuedSMS.add(sms);
            smsQueue.put(receiver, queuedSMS);
        }
        System.out.println("Queued " + queuedSMS.size() + " SMS for " + receiver);
    }

    private static void flushSMS() {
        if (smsQueue == null) {
            init();
        }
        System.out.println("Start flushing smsQueue");
        for (Map.Entry<String, ArrayList<Object>> entry : smsQueue.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            String receiver = entry.getKey();
            ArrayList messages = entry.getValue();
            if (messages.size() < 1) {
                continue;
            }

            messages.sort(new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Map m1 = (Map) o1;
                    Map m2 = (Map) o2;
                    String g1 = (String) m1.get("group");
                    String g2 = (String) m2.get("group");
                    if ((g1 == null) && (g2 == null)) {
                        return 0;
                    }
                    if (g1 == null) {
                        return -1;
                    }
                    if (g2 == null) {
                        return 1;
                    }
                    return g1.compareTo(g2);
                }
            });
            String mergedContent = (messages.size() > 1) ? "Cảnh báo gộp" : "";
            String oldGroup = null;
            String currentGroup = null;
            for (int i = 0; i < messages.size(); i++) {
                Map msg = (Map) messages.get(i);
                currentGroup = (String) msg.get("group");
                if ((oldGroup == null) || (currentGroup.compareTo(oldGroup) != 0)) {
                    mergedContent += "\n\n+++++ Group: " + currentGroup + " +++++\n";
                    oldGroup = currentGroup;
                }
                mergedContent += "\n===============\n";
                mergedContent += msg.get("content");
            }
//            System.out.println(mergedContent);
            mergedContent = StringUtils.vi2en(mergedContent);
            sendSMS(receiver, mergedContent);
            System.out.println("Flushed " + messages.size() + " SMS to " + receiver);
            messages.clear();
            System.out.println("Current queued SMS for " + receiver + ": " + messages.size());
        }
    }

}
