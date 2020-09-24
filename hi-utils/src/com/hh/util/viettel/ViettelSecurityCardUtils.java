package com.hh.util.viettel;

import java.util.Arrays;

/**
 * @author TruongNX25
 */

public class ViettelSecurityCardUtils {
    public static String encrypt(String data) {
        String str2 = null;
        if (data != null) {
            // System.out.println("input for encrypt: " + data + " length: " + data.length());
            int mid = (data.length() + 1) / 2;
            String[] spstr = {data.substring(0, mid), data.substring(mid)};
            String firstMix = "" + spstr[1] + spstr[0];

            String[] temp2 = firstMix.split("");
            // 450000150031
            // 503015000014

            String versionJdk = System.getProperty("java.class.version");
            // System.out.println("JDK version: " + versionJdk);
            String[] array;
            if ((null != versionJdk) && (!versionJdk.isEmpty()) && (Float.parseFloat(versionJdk) >= 52.0F)) {
                array = new String[temp2.length + 1];
                array[0] = "";
                for (int j = 0; j < temp2.length; j++) {
                    array[(j + 1)] = temp2[j];
                }
            } else {
                array = firstMix.split("");
            }
            String str1 = Arrays.toString(array);
            for (int j = 1; j < 3; j++) {
                String temp = array[j];
                array[j] = array[(array.length - j)];
                array[(array.length - j)] = temp;
            }
            str1 = Arrays.toString(array);
            str1 = str1.substring(1, str1.length() - 1).replaceAll(",", "");
            int j = 2;
            for (int i = array.length / 2 - 2; i < array.length / 2; i++) {
                String temp = array[i];
                array[i] = array[(array.length / 2 + j)];
                array[(array.length / 2 + j)] = temp;
                j--;
            }
            str2 = Arrays.toString(array);
            str2 = str2.substring(1, str2.length() - 1).replaceAll(",", "");
            str2 = str2.replaceAll("\\s", "");
            // System.out.println("result of encrypt: " + str2 + " length: " + str2.length());
        }
        return str2;
    }

    public static String decrypt(String data) {
        String firstMix = null;
        if (data != null) {
            // System.out.println("input for decrypt: " + data + " length: " + data.length());

            String[] array = data.split("");

            String versionJdk = System.getProperty("java.class.version");
            if ((null != versionJdk) && (!versionJdk.isEmpty()) && (Float.parseFloat(versionJdk) >= 52.0F)) {
                String[] tmp = data.split("");
                String[] tmp1 = new String[tmp.length + 1];
                tmp1[0] = "";
                for (int i = 0; i < tmp.length; i++) {
                    tmp1[i + 1] = tmp[i];
                }
                array = tmp1;
            }

            int j = 2;
            for (int i = array.length / 2 - 2; i < array.length / 2; i++) {
                String temp = array[i];
                array[i] = array[(array.length / 2 + j)];
                array[(array.length / 2 + j)] = temp;
                j--;
            }
            String str2 = Arrays.toString(array);
            for (int i = 1; i < 3; i++) {
                String temp = array[i];
                array[i] = array[(array.length - i)];
                array[(array.length - i)] = temp;
            }
            String str1 = Arrays.toString(array);

            str1 = str1.substring(1, str1.length() - 1).replaceAll(",", "");

            int mid = (str1.length() + 1) / 2;
            String[] spstr;
            if (str1.length() % 2 == 1) {
                spstr = new String[]{str1.substring(0, mid - 1), str1.substring(mid - 1)};
            } else {
                spstr = new String[]{str1.substring(0, mid), str1.substring(mid)};
            }
            firstMix = spstr[1] + spstr[0];
            firstMix = firstMix.replaceAll("\\s", "");
            // System.out.println("result of Decrypt: " + firstMix + " length: " + firstMix.length());
        }
        return firstMix;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("9704229228126665"));
    }

}
