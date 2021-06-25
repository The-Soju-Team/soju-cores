package com.hh.util;

import com.hh.constant.Constants;
import com.hh.util.viettel.ViettelSecurityCardUtils;
import gnu.trove.map.hash.TCharCharHashMap;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruongNX25
 */

public class StringUtils {
    private static final Logger log = Logger.getLogger(StringUtils.class.getName());
    public static TCharCharHashMap T_CHARACTER_MAP = null;

    public static Map<String, String> lstBankCode = null;

    public static Map<String, String> lstNetworkOperatorCode = null;
    // Mang cac ky tu goc co dau
    static char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý',
            'à', 'á', 'â', 'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ', 'Ĩ',
            'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể',
            'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ',
            'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử',
            'ử', 'Ữ', 'ữ', 'Ự', 'ự', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ', 'ỳ', 'ỷ', 'ỹ', 'ỵ'};
    // Mang cac ky tu thay the khong dau
    static char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E', 'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U',
            'Y', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A', 'a', 'D', 'd',
            'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u', 'U', 'u', 'Y', 'Y', 'Y', 'Y', 'y', 'y', 'y', 'y',};

    private StringUtils() {
    }

    public static void init() {
        T_CHARACTER_MAP = new TCharCharHashMap();
        for (int i = 0; i < SOURCE_CHARACTERS.length; i++) {
            T_CHARACTER_MAP.put(SOURCE_CHARACTERS[i], DESTINATION_CHARACTERS[i]);
        }

        lstBankCode = new HashMap<String, String>();

        lstBankCode.put("ABBANK", "ABBANK");
        lstBankCode.put("ABBANK_SLA", "ABBANK");
        lstBankCode.put("ACB", "ACB");
        lstBankCode.put("ACB TN", "ACB");
        lstBankCode.put("ACB_TTDX", "ACB");
        lstBankCode.put("AGRIBANK", "AGRIBANK");
        lstBankCode.put("AGRIBANK HN", "AGRIBANK");
        lstBankCode.put("AGRIBANK.BN", "AGRIBANK");
        lstBankCode.put("AGRIBANK.ST", "AGRIBANK");
        lstBankCode.put("AGRIBANK_BD", "AGRIBANK");
        lstBankCode.put("AGRIBANK_HD", "AGRIBANK");
        lstBankCode.put("AGRIBANK_LD", "AGRIBANK");
        lstBankCode.put("AGRIBANK_SG", "AGRIBANK");
        lstBankCode.put("AGRIBANK_TQ", "AGRIBANK");
        lstBankCode.put("AGRIBANK10", "AGRIBANK");
        lstBankCode.put("AGRIBANK-BR", "AGRIBANK");
        lstBankCode.put("AGRIBANKCN5", "AGRIBANK");
        lstBankCode.put("AGRIBANKCN8", "AGRIBANK");
        lstBankCode.put("AGRIBANKCN9", "AGRIBANK");
        lstBankCode.put("AGRIBANK-DN", "AGRIBANK");
        lstBankCode.put("AGRIBANK-KT", "AGRIBANK");
        lstBankCode.put("AGRIBANKLD2", "AGRIBANK");
        lstBankCode.put("AGRIBOOK", "AGRIBANK");
        lstBankCode.put("AGRI-TPHONG", "AGRIBANK");
        lstBankCode.put("AGRITHANHDO", "AGRIBANK");
        lstBankCode.put("BACA BANK", "BACABANK");
        lstBankCode.put("BACABANK", "BACABANK");
        lstBankCode.put("BIDV", "BIDV");
        lstBankCode.put("BIDVDAKNONG", "BIDV");
        lstBankCode.put("BIDVTG", "BIDV");
        lstBankCode.put("HDBANK", "HDBANK");
        lstBankCode.put("HDBANK_TB", "HDBANK");
        lstBankCode.put("LPB", "LPB");
        lstBankCode.put("LPB_CAOBANG", "LPB");
        lstBankCode.put("LPB-LAMDONG", "LPB");
        lstBankCode.put("NHKIENLONG", "NHKIENLONG");
        lstBankCode.put("NH.KIENLONG", "NHKIENLONG");
        lstBankCode.put("TPBANK", "TPBANK");
        lstBankCode.put("TPBANK-CSKH", "TPBANK");
        lstBankCode.put("VPBANK", "VPBANK");
        lstBankCode.put("VPBANKPLUS", "VPBANK");
        lstBankCode.put("VPBANK-Q10", "VPBANK");
        lstBankCode.put("VPB-ITINFO", "VPBANK");
        lstBankCode.put("FE CREDIT", "FECREDIT");
        lstBankCode.put("FECREDIT", "FECREDIT");
        lstBankCode.put("TRUEMONEY", "TRUEMONEY");
        lstBankCode.put("TRUEMONEYVN", "TRUEMONEY");

        initLstNetworkOperator();
    }

    private static void initLstNetworkOperator() {
        // Network Operator
        lstNetworkOperatorCode = new HashMap<String, String>();

        lstNetworkOperatorCode.put("8486", "VIETTEL");
        lstNetworkOperatorCode.put("8496", "VIETTEL");
        lstNetworkOperatorCode.put("8497", "VIETTEL");
        lstNetworkOperatorCode.put("8498", "VIETTEL");
        lstNetworkOperatorCode.put("8432", "VIETTEL");
        lstNetworkOperatorCode.put("8433", "VIETTEL");
        lstNetworkOperatorCode.put("8434", "VIETTEL");
        lstNetworkOperatorCode.put("8435", "VIETTEL");
        lstNetworkOperatorCode.put("8436", "VIETTEL");
        lstNetworkOperatorCode.put("8437", "VIETTEL");
        lstNetworkOperatorCode.put("8438", "VIETTEL");
        lstNetworkOperatorCode.put("8439", "VIETTEL");
        lstNetworkOperatorCode.put(Constants.NETWORK_OPERATOR_ID_VIETTEL, "VIETTEL");

        lstNetworkOperatorCode.put("8489", "MOBIFONE");
        lstNetworkOperatorCode.put("8490", "MOBIFONE");
        lstNetworkOperatorCode.put("8493", "MOBIFONE");
        lstNetworkOperatorCode.put("8470", "MOBIFONE");
        lstNetworkOperatorCode.put("8479", "MOBIFONE");
        lstNetworkOperatorCode.put("8477", "MOBIFONE");
        lstNetworkOperatorCode.put("8476", "MOBIFONE");
        lstNetworkOperatorCode.put("8478", "MOBIFONE");
        lstNetworkOperatorCode.put(Constants.NETWORK_OPERATOR_ID_MOBIFONE, "MOBIFONE");

        lstNetworkOperatorCode.put("8488", "VINAPHONE");
        lstNetworkOperatorCode.put("8491", "VINAPHONE");
        lstNetworkOperatorCode.put("8494", "VINAPHONE");
        lstNetworkOperatorCode.put("8483", "VINAPHONE");
        lstNetworkOperatorCode.put("8484", "VINAPHONE");
        lstNetworkOperatorCode.put("8485", "VINAPHONE");
        lstNetworkOperatorCode.put("8481", "VINAPHONE");
        lstNetworkOperatorCode.put("8482", "VINAPHONE");
        lstNetworkOperatorCode.put(Constants.NETWORK_OPERATOR_ID_VINAPHONE, "VINAPHONE");

        lstNetworkOperatorCode.put("8492", "VIETNAMOBILE");
        lstNetworkOperatorCode.put("8456", "VIETNAMOBILE");
        lstNetworkOperatorCode.put("8458", "VIETNAMOBILE");
        lstNetworkOperatorCode.put(Constants.NETWORK_OPERATOR_ID_VIETNAMOBILE, "VIETNAMOBILE");

        lstNetworkOperatorCode.put("8499", "VIETNAMOBILE");
        lstNetworkOperatorCode.put("8459", "VIETNAMOBILE");
        log.info("Done adding listNetworkOperator");
    }

    public static String getNetwork(String msisdn) {
        if (msisdn == null) {
            return null;
        }
        if (msisdn.length() >= 4) {
            msisdn = msisdn.substring(0, 4);
        } else if (msisdn.length() >= 2) {
            msisdn = msisdn.substring(0, 2);
        }
        return lstNetworkOperatorCode.get(msisdn);
    }

    public static String getNetworkStr(String msisdn) {
        if (msisdn == null) {
            return "";
        }
        if (msisdn.length() >= 4) {
            msisdn = msisdn.substring(0, 4);
        } else if (msisdn.length() >= 2) {
            msisdn = msisdn.substring(0, 2);
        }
        if (lstNetworkOperatorCode == null) {
            initLstNetworkOperator();
        }
        String network = lstNetworkOperatorCode.get(msisdn);
        if (network != null) {
            return network;
        }
        return "";
    }

    /**
     * Remove whitespace in string
     *
     * @param inputString a string
     */
    public static String processString(String inputString) {
        if (null != inputString) {
            // return string not contains whitespace
            return inputString.trim();
        }
        // return null if input as null or whitespace
        return null;
    }

    public static String encryptAccNo(String s) {
        if (s == null)
            return "";
        return ViettelSecurityCardUtils.encrypt(s);
    }

    public static String decryptAccNo(String s) {
        if (s == null)
            return "";
        return ViettelSecurityCardUtils.decrypt(s);
    }

    public static String convertBankCode(String s) {
        if (s == null)
            return "";
        s = s.toUpperCase();
        String x = lstBankCode.get(s);
        if (x != null) {
            return x;
        }
        return s;
    }

    public static String str2Ascii(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(Integer.toHexString((int) s.charAt(i))).append("-");
        }
        return sb.toString();
    }

    public static String vi2en(String str) {
        if (str == null) {
            return "";
        }

        char[] arr = str.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (char ch : arr) {
            char mapChar = T_CHARACTER_MAP.get(ch);
            if (mapChar > 0) {
                sb.append(mapChar);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * converet calendar to String yyyy-MM-dd
     *
     * @param date
     * @return String format yyyy-MM-dd
     */
    public static String calendarToString(Calendar date) {
        Date d = date.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    public static String DateTimeToString(Date date) {
        final SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return FORMAT_DATE_TIME.format(date);
    }

}
