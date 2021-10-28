package com.hh.util.viettel;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LivyUtils {

    private static final Logger LOG = Logger.getLogger(LivyUtils.class);
    private static final String SERVER_ENDPOINT = "http://10.254.136.41:9220";
    private static final String SUCCESS_KEY = "success";
    private static final String LOCAL_SAVE_PATH = "./data";

    public static Map<Object, Object> submitQuery(String query) {
        String endpoint = SERVER_ENDPOINT + "/query";
        Map<String, String> params = new HashMap<>();
        params.put("query", query);
        Map<Object, Object> result = RequestUtils.postJson(endpoint, params);
        for (Object k : result.keySet()) {
            LOG.debug(String.format("Key: %s - Value: %s", k, result.get(k)));

        }
        return result;
    }

    /***
     * {
     *   "status": "success",
     *   "theDate": "20210916",
     *   "outputFileName": "livy_1631788054053_549257"
     * }
     * @param submitResult
     * @return
     */
    public static String downloadToLocal(Map<Object, Object> submitResult) {
        Map<String, String> params = new HashMap<>();
        params.put("status", SUCCESS_KEY);
        params.put("theDate", submitResult.get("theDate").toString());
        params.put("outputFileName", submitResult.get("outputFileName").toString());
        String endpoint = SERVER_ENDPOINT + "/download";
        File f = new File(LOCAL_SAVE_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        return RequestUtils.postDownloadFile(endpoint, params, LOCAL_SAVE_PATH + File.separator + params.get("outputFileName") + ".csv");
    }

    public static boolean isQueryDone(Map<Object, Object> submitResult) {
        String endpoint = SERVER_ENDPOINT + "/query";
        int count = 0;

        Map<String, String> params = new HashMap<>();
        params.put("queryId", submitResult.get("queryId").toString());
        params.put("sessionId", submitResult.get("sessionId").toString());
        Map<Object, Object> isQueryDone;
        while (count <= 200) {
            LOG.info("Waiting for result from DonNN's service");
            try {
                isQueryDone = RequestUtils.get(endpoint, params);

//                for (Object k : isQueryDone.keySet()) {
//                    System.out.println(String.format("key: %s - value: %s", k, isQueryDone.get(k)));
//                }

                if (isQueryDone != null && isQueryDone.containsKey("statement")) {
                    HashMap<Object, Object> statement = (HashMap) isQueryDone.get("statement");
                    if (statement.get("state").equals("available")) {
                        return true;
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
            } finally {
                count++;
            }
        }
        return false;
    }

    public static String queryToFile(String query) {
        // First we need to submit to livy first
        Map<Object, Object> submitResult = submitQuery(query);
        if (submitResult != null) {
            // Then go to the next step
            if (submitResult.get("status").equals("success")) {
                // Good looking
                LOG.info(String.format("Submitted success with ID: %s", submitResult.get("queryId")));
                if (isQueryDone(submitResult)) {
                    return downloadToLocal(submitResult);
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        queryToFile("select * from parquet.`hdfs://dmp/storage/encrypted_zone/bi/viettelpay/level2/mct_chain` where PARTITION_DATE = 20210701");
    }
}
