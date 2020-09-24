/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.util.viettel;

import com.hh.constant.Constants;
import com.hh.util.ConfigUtils;
import it.sauronsoftware.cron4j.Scheduler;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author donnn
 */
public class MailUtils {
    private static final Logger log = Logger.getLogger(MailUtils.class);
    private static ConfigUtils config;
    private static String emailAddr;
    private static String password;
    private static Authenticator auth;
    private static Properties props;
    private static boolean initialized = false;
    private static final String schedulePattern = "*/5 * * * *";

    private static Scheduler scheduler;

    private static ConcurrentHashMap<String, ArrayList<Object>> mailQueue = null;

    private static void init() {
        config = Constants.config;
        emailAddr = config.getConfig("mail-address");

        if ((emailAddr == null) || (emailAddr.length() < 1)) {
            log.info("Cannot initialize MailUtils.");
            log.info("Config credentials in etc/server.conf first.");
//            System.exit(1);
        }

        // 
        scheduler = new Scheduler();
        scheduler.schedule(schedulePattern, MailUtils::flushMail);
        scheduler.start();

        // Init queue
        mailQueue = new ConcurrentHashMap<>();

        password = config.getConfig("mail-password");

        props = new Properties();
        props.put("mail.smtp.host", config.getConfig("mail-server")); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port

        auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddr, password);
            }
        };
        initialized = true;
    }

    /**
     * Send text email
     *
     * @param recipient Email Address to send
     * @param subject   Email Subject
     * @param body      Text
     */

    public static void sendMail(String recipient, String subject, String body) {
        if (!initialized) init();

        Session session = Session.getDefaultInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(emailAddr, emailAddr));

            msg.setReplyTo(InternetAddress.parse(recipient, false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
            log.info("Message is ready");
            Transport.send(msg);

            log.info("Email Sent Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Send email with some attachments
     *
     * @param recipient   Email Address to send
     * @param subject     Email Subject
     * @param body        Text
     * @param attachments List of file paths
     */

    public static void sendMailWithAttachments(String recipient, String subject, String body, List<String> attachments) {
        if (!initialized) init();

        Session session = Session.getDefaultInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(emailAddr, emailAddr));

            msg.setReplyTo(InternetAddress.parse(recipient, false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

            // generate email

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // set text
            BodyPart textPart = new MimeBodyPart();
//            textPart.setHeader("Content-Type", "text/html");
//            textPart.setText(body);
            textPart.setContent(body, "text/html; charset=UTF-8");

            // add text to email
            multipart.addBodyPart(textPart);

            // add attachments to email
            for (String fileName : attachments) {
                BodyPart part = new MimeBodyPart();
                DataSource source = new FileDataSource(fileName);
                part.setDataHandler(new DataHandler(source));
                part.setFileName(fileName.substring((fileName.lastIndexOf("/") >= 0) ? (fileName.lastIndexOf("/") + 1) : 0));

                // add file to email
                multipart.addBodyPart(part);
            }

            msg.setContent(multipart);
            log.info("Message is ready");
            Transport.send(msg);

            log.info("Email Sent Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void queueMail(String recipient, String subject, String body, List<String> attachments) {
        queueMail(recipient, subject, body, attachments, new HashMap());
    }

    public static void queueMail(String recipient, String subject, String body, List<String> attachments, Map extra) {
        if (!initialized) init();
        log.info("Now add a mail to queue of " + recipient);
        Map<String, Object> mail = new HashMap<String, Object>();
        mail.put("receiver", recipient);
        mail.put("subject", subject);
        mail.put("body", body);
        String group = (extra.get("group") == null) ? "" : (String) extra.get("group");
        mail.put("group", (String) extra.get("group"));
        mail.put("attachments", attachments);
        ArrayList queuedMails = mailQueue.get(recipient);
        if (queuedMails != null) {
            // this user has already had some pending messages
            queuedMails.add(mail);
        } else {
            // init new queue for this user, update the main queue
            queuedMails = new ArrayList();
            queuedMails.add(mail);
            mailQueue.put(recipient, queuedMails);
        }
        log.info("Queued " + queuedMails.size() + " mail(s) for " + recipient);
    }

    private static void flushMail() {
        if (mailQueue == null) {
            init();
        }
        log.info("Start flushing mailQueue");
        for (Map.Entry<String, ArrayList<Object>> entry : mailQueue.entrySet()) {
//            log.info(entry.getKey() + "/" + entry.getValue());
            String receiver = entry.getKey();
            ArrayList messages = entry.getValue();
            if (messages.size() >= 1) {
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
                String subject = (messages.size() > 1) ? "Cảnh báo gộp" : ((Map) messages.get(0)).get("subject").toString();
                String mergedBody = (messages.size() > 1) ? "Cảnh báo gộp" : "";
                List<String> mergedAttachments = new ArrayList();
                String oldGroup = null;
                String currentGroup = null;
                for (int i = 0; i < messages.size(); i++) {
                    Map msg = (Map) messages.get(i);
                    currentGroup = (String) msg.get("group");
                    if ((oldGroup == null) || (currentGroup.compareTo(oldGroup) != 0)) {
                        mergedBody += "\n\n+++++ Group: " + currentGroup + " +++++\n";
                        oldGroup = currentGroup;
                    }
                    mergedBody += "\n===============\n";
                    mergedBody += msg.get("body");
                    mergedAttachments.addAll((List) (msg.get("attachments")));
                }
//                log.info(mergedBody);
                sendMailWithAttachments(receiver, subject, mergedBody, mergedAttachments);
            }


            log.info("Flushed " + messages.size() + " Mail(s) to " + receiver);
            messages.clear();
            log.info("Current queued Mail(s) for " + receiver + ": " + messages.size());
        }
    }

    public static int size() {
        return mailQueue.size();
    }
}
