package com.viettel.authen.util;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static Map<String, String> lstNetworkOperatorCode = null;

    private StringUtils() {

    }

    public static String padding(String str, int len) {
        if (str == null) {
            return "";
        }
        str += "";
        while (str.length() < len) {
            str = "0" + str;
        }
        return str;
    }

    private static String charToBinaryString(char ch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            sb.append(ch >> i & 1);
        }
        return sb.toString();
    }

    public static boolean checkIpInNetwork(String ipAddress, String networkAddress) {
        String ip = ip2Binary(ipAddress, true);
        String net = ip2Binary(networkAddress, false);
        if ((ip.length() > 0) && (net.length() > 0)) {
            return ip.startsWith(net);
        }
        return false;
    }

    private static String ip2Binary(String ip, boolean needPadding) {
        Pattern patternAllNumber = Pattern.compile("^(25[0-5]|2[0-4]\\d?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)$");

        Matcher m = null;

        m = patternAllNumber.matcher(ip);

        if (m.find()) {
            // log.info("ip " + ip + " matches patternAllNumber");
            String binaryAddress = "";
            String binaryAddress1 = "";
            for (int i = 1; i <= 4; i++) {
                String g = m.group(i);
                char ch = (char) Integer.parseInt(g);
                String binaryByte = StringUtils.padding(Integer.toBinaryString(ch), 8);
                String binaryByte1 = StringUtils.padding(charToBinaryString(ch), 8);
                binaryAddress += binaryByte;
                binaryAddress1 += binaryByte1;
            }

            // log.info("Binary : " + binaryAddress);
            // log.info("Binary1: " + binaryAddress1);
            return binaryAddress;
        }

        Pattern patternWithAsterisk = Pattern.compile("^(25[0-5]|2[0-4]\\d?|1\\d\\d|\\d\\d?)\\.((25[0-5]|2[0-4]\\d?|1\\d\\d|\\d\\d?)\\.){0,2}\\*$");

        m = patternWithAsterisk.matcher(ip);

        if (m.find()) {
            // log.info("ip " + ip + " matches patternWithAsterisk");
            String[] parts = ip.split("\\.");
            String binaryAddress = "";
            for (int i = 0; i < parts.length - 1; i++) {
                char ch = (char) Integer.parseInt(parts[i]);
                binaryAddress += StringUtils.padding(charToBinaryString(ch), 8);
            }
            if (needPadding) {
                for (int i = 0; i < 4 - parts.length + 1; i++) {
                    binaryAddress += "00000000";
                }
            }
            // log.info("Binary : " + binaryAddress);
            return binaryAddress;
        }

        Pattern patternWithMask = Pattern.compile("^(25[0-5]|2[0-4]\\d?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)\\.(2[0-5][0-5]?|1\\d\\d|\\d\\d?)\\/(3[012]|[012]\\d?)$");

        m = patternWithMask.matcher(ip);

        if (m.find()) {
            // log.info("ip " + ip + " matches patternWithMask");
            String binaryAddress = "";
            String binaryAddress1 = "";
            for (int i = 1; i <= 4; i++) {
                String g = m.group(i);
                char ch = (char) Integer.parseInt(g);
                String binaryByte = StringUtils.padding(Integer.toBinaryString(ch), 8);
                String binaryByte1 = StringUtils.padding(charToBinaryString(ch), 8);
                binaryAddress += binaryByte;
                binaryAddress1 += binaryByte1;
            }

            // log.info("Original Binary: " + binaryAddress);
            int mask = Integer.parseInt(m.group(5));
            binaryAddress = binaryAddress.substring(0, mask);
            binaryAddress1 = binaryAddress1.substring(0, mask);
            // log.info("Semantic Binary: " + binaryAddress);
            if (needPadding) {
                for (int i = 0; i < 32 - mask; i++) {
                    binaryAddress += "0";
                    binaryAddress1 += "0";
                }
            }
            // log.info("Binary:          " + binaryAddress);
            // log.info("Binar1:          " + binaryAddress1);
            return binaryAddress;
        }
        return "";
    }
}
