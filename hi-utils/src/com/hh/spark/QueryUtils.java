package com.hh.spark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hh.util.viettel.LivyUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import org.spark_project.guava.io.Files;

import com.hh.constant.Constants;
import com.hh.util.StringUtils;
import com.hh.util.WriteDataToExcel;

/**
 * @author TruongNX25
 */
public class QueryUtils {
    public static final String ERROR_DUPLICATE_HEADER_CSV = "Lỗi: Dữ liệu của bạn có cột trùng, không thể xuất file";
    private static final String FOLDER_REPORT = "/tmp";
    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(QueryUtils.class.getSimpleName());
    public static final Pattern p = Pattern.compile("^([0-9]+)\\.[0-9]+$");
    public static boolean doneWritingFile = false;
    public static Thread writeToHDFS;

    public static boolean isToND = false;


    public static final UserDefinedFunction encryptAccNo = functions.udf((UDF1<String, String>) StringUtils::encryptAccNo, DataTypes.StringType);

    public static final UserDefinedFunction decryptAccNo = functions.udf((UDF1<String, String>) StringUtils::decryptAccNo, DataTypes.StringType);

    public static final UserDefinedFunction convertBankCode = functions.udf((UDF1<String, String>) StringUtils::convertBankCode, DataTypes.StringType);

    public static final UserDefinedFunction str2Ascii = functions.udf((UDF1<String, String>) StringUtils::str2Ascii, DataTypes.StringType);

    public static final UserDefinedFunction getNetwork = functions.udf((UDF1<String, String>) StringUtils::getNetwork, DataTypes.StringType);

    public static final UserDefinedFunction getNetworkStr = functions.udf((UDF1<String, String>) StringUtils::getNetworkStr, DataTypes.StringType);

    public static final UserDefinedFunction vi2en = functions.udf((UDF1<String, String>) StringUtils::vi2en, DataTypes.StringType);

    public static final UserDefinedFunction convertMSISDN = functions.udf((UDF1<String, String>) msisdn -> {
        if (msisdn == null) {
            return null;
        }
        if (msisdn.length() < 9) {
            return msisdn;
        }
        if (msisdn.length() == 9) {
            return "84" + msisdn;
        }
        if (msisdn.charAt(0) == '0') {
            msisdn = "84" + msisdn.substring(1);
        }
        if (msisdn.startsWith("8416")) {
            return "843" + msisdn.substring(4);
        }
        if (msisdn.startsWith("84120")) {
            return "8470" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84121")) {
            return "8479" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84122")) {
            return "8477" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84126")) {
            return "8476" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84128")) {
            return "8478" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84124")) {
            return "8484" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84127")) {
            return "8481" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84129")) {
            return "8482" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84123")) {
            return "8483" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84125")) {
            return "8485" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84186")) {
            return "8456" + msisdn.substring(5);
        }
        if (msisdn.startsWith("84188")) {
            return "8458" + msisdn.substring(5);
        }
        if (msisdn.startsWith("8409")) {
            return "849" + msisdn.substring(4);
        }
        if (msisdn.startsWith("8403")) {
            return "843" + msisdn.substring(4);
        }
        return msisdn;

    }, DataTypes.StringType);
    public static final UserDefinedFunction hashSensitiveInformation = functions.udf((UDF1<String, String>) s -> {
        if (s == null) {
            return null;
        } else {
            if (s.startsWith("H-") && s.endsWith("-EH")) {
                return s;
            }
            return "H-" + encrypt(s) + "-EH";
        }
    }, DataTypes.StringType);

    public static final UserDefinedFunction decryptSensitiveInformation = functions.udf((UDF1<String, String>) s -> {
        if (s == null) {
            return null;
        } else {
            s = s.substring(2);
            s = s.substring(0, s.length() - 4);
            return decrypt(s).replace("ENCRYPTED-", "");
        }
    }, DataTypes.StringType);

    public static String encrypt(String s) {
        if (s == null) {
            return null;
        }
        s = "ENCRYPTED-" + s;
        String encrypt = null;
        byte[] abytes = s.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < abytes.length; i++) {
            abytes[i] = (byte) (abytes[i] + 5);
        }
        encrypt = new String(abytes);
        return encrypt;
    }

    public static String decrypt(String s) {
        if (s == null) {
            return null;
        }
        String decrypt = null;
        byte[] abytes = s.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < abytes.length; i++) {
            abytes[i] = (byte) (abytes[i] - 5);
        }
        decrypt = new String(abytes);
        return decrypt;
    }

    public static void addUDFToSpark(SparkSession spark) {
        spark.udf().register("convertMSISDN", convertMSISDN);
        spark.udf().register("vi2en", vi2en);
        spark.udf().register("str2Ascii", str2Ascii);
        spark.udf().register("convertBankCode", convertBankCode);
        spark.udf().register("encryptAccNo", encryptAccNo);
        spark.udf().register("decryptAccNo", decryptAccNo);
        spark.udf().register("getNetwork", getNetwork);
        spark.udf().register("getNetworkStr", getNetworkStr);
        spark.udf().register("hash", hashSensitiveInformation);
        // Decrypt msisdn
        spark.udf().register("ad89828579479b2985707279b565dcfe", decryptSensitiveInformation);
        // spark.conf().set("spark.sql.crossJoin.enabled", "true");
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName)
            throws IOException {
        return executeSparkQuery(query, fileName, false);
    }

    public static Map<String, Object> executeOracleQuery(String query, String dbSchema, String fileName)
            throws IOException {
        return executeOracleQuery(query, dbSchema, fileName, false);
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, boolean throwError)
            throws IOException {
        SparkSession spark = SparkUtils.getAvailableSparkSession();
        return executeSparkQuery(query, fileName, throwError, spark);
    }

    public static void writeFileToHDFS(String fileName, Dataset<Row> data) throws IOException {
        writeFileToHDFS(fileName, data, 1);
    }

    public static void writeFileToHDFS(String fileName, Dataset<Row> data, int partition) throws IOException {
        String[] nameSplit = fileName.split(File.separator);
        String hdfsPath = "hdfs://10.58.244.172:8020/storage/bi/hdfs/action-result/" + nameSplit[nameSplit.length - 1];
        log.info(String.format("TOGREP | Writing to CSV: %s", hdfsPath));

        data.repartition(partition).write().option("header", "true").csv(hdfsPath); // This would create a folder, with
        // part-000*.csv behind that
        log.info(String.format("TOGREP | Done writing to CSV to %s", hdfsPath));
        FileSystem fs = Constants.fileSystem;
        FileStatus[] fileStatus = fs.listStatus(new Path(hdfsPath));
        for (FileStatus status : fileStatus) {
            if (status.getPath().toString().toLowerCase().endsWith("csv")) {
                log.info("TOGREP | HDFS File: " + status.getPath().toString());
                fs.copyToLocalFile(new Path(hdfsPath), new Path(fileName + "-folder"));
                // No matter what, copy from hdfs to local,
                File f = new File(fileName + "-folder");
                if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    for (File file : files) {
                        if (file.getName().toLowerCase().endsWith(".csv")) {
                            Files.move(file, new File(fileName));
                            f.delete();
                            break;
                        }
                    }
                }
                break;
            }
        }
        log.info(String.format("TOGREP | File has been downloaded to: %s", fileName));
        doneWritingFile = true;
    }

    public static boolean validateHeaderCSV(String[] cols) {
        log.info("TOGREP | Validating CSV Header " + Arrays.asList(cols));
        Set<String> columnSet = new HashSet<>();
        for (String col : cols) {
            if (!columnSet.add(col)) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, boolean throwError,
                                                        SparkSession spark) throws IOException {
        return executeSparkQuery(query, fileName, throwError, spark, false);
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, boolean throwError,
                                                        SparkSession spark, boolean collect) throws IOException {
        Map result = new HashMap();
        if ((query == null) || (query.trim().length() < 1)) {
            result.put("status", false);
            result.put("result", "Query Empty");
            return result;
        }
        // Replace params
        for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
            query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
        }
        addUDFToSpark(spark);
        Dataset<Row> data = null;
        try {
            log.info("TOGREP | Preparing Query");
            log.info(String.format("TOGREP | Querying :%s", query));
            data = queryToDataset(spark, query);
            data.cache();
            data.show();
            //          data = spark.sql(query);
            log.info(String.format("TOGREP | Done Querying For %s", query));
            result.put("status", true);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while fetching data");
            result.put("status", false);
            result.put("result", "Error: " + e.toString());
            if (throwError) {
                throw e;
            }
            return result;
        } finally {
            SparkUtils.releaseSparkSession(spark);
        }

        // Fetch data
        log.info("TOGREP | Fucking here... " + data.count());
        // Inject by TruongNX25 here
        /**
         * Since data.collectAsList() consumes a lot of memory and time, and it would
         * double the size, so we will not collectAsList() anymore, write directly to
         * CSV, and get 1000 rows randomly to return to front-end, fork a thread, then
         * just return limit 1k rows to front-end to avoid bad user experience
         */
        final ArrayList<Dataset<Row>> listData = new ArrayList<>();
        listData.add(data);
        log.info(String.format("TOGREP | Forking new thread to write file %s", fileName));
        writeToHDFS = new Thread(() -> {
            try {
                writeFileToHDFS(fileName, listData.get(0));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
        writeToHDFS.start();
        log.info(String.format("TOGREP | A new thread has been forked to write file: %s", fileName));
        if (collect) {
            result.put("totalRows", data.count());
            if (data.count() > 1000) {
                data = data.limit(1000);
            }
            List<Row> listRows = data.collectAsList();
            log.info("TOGREP | Collected As List Successfully");

            ArrayList<ArrayList> rawResult = new ArrayList<ArrayList>();
            ArrayList<String> rawHeaders = new ArrayList<String>();

            // log.info("Collect done, " + listRows.size());
            StringBuilder sb = new StringBuilder();
            // Gson gson = new Gson();
            String[] cols = data.columns();
            // Let's validate the columns, for some reason, we can't export csv to hdfs with
            // duplicate headers
            if (!validateHeaderCSV(cols)) {
                result.put("status", false);
                result.put("result", ERROR_DUPLICATE_HEADER_CSV);
                return result;
            }
            // End validate
            int colNo = 0;

            for (String col : cols) {
                log.info("Column" + colNo++ + ": " + col);
                sb.append(col).append(",");

                rawHeaders.add(col);
            }
            sb.append("\n");
            log.info("Total columns: " + colNo);
            log.info("Total rows: " + listRows.size());

            int count = 0;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            for (Row row : listRows) {
                count++;

                ArrayList rawRow = new ArrayList();
                int stt = 0;
                for (String col : cols) {
                    stt++;
                    Object val = row.getAs(col);
                    if (val != null) {
                        String valStr = "";
                        if ((val instanceof Double) || (val instanceof Float)) {
                            // log.info("Double or Float: " + val + " convert to " +
                            // String.format("%.2f", val));
                            valStr = String.format("%.2f", val);
                        } else {
                            valStr = val.toString();
                        }
                        Matcher m = p.matcher(valStr);
                        if (m.find()) { // Float
                            // sb.append('"');
                            // sb.append(nf.format(Double.parseDouble(m.group(0))));
                            // sb.append('"');
                            sb.append('"').append(m.group(0)).append('"'); // tmp debug
                            // log.info(stt + " Double: " + m.group(0) + " : " + '"' + m.group(0)
                            // + '"');
                        } else { // String
                            // log.info(stt + " Not Double: " + valStr);
                            if (valStr.indexOf('"') >= 0) {
                                valStr = valStr.replace("\"", "\"\"");
                            }
                            if ((valStr.contains(",")) || (valStr.indexOf('"') >= 0)) {
                                sb.append('"').append(valStr.replace("\n", " ").replace("\r", " ")).append('"');
                            } else {
                                sb.append(valStr.replace("\n", " ").replace("\r", " "));
                            }
                        }
                    }

                    // add data to rawRow
                    rawRow.add(val);

                    sb.append(",");
                }
                if (count <= 1000) {
                    rawResult.add(rawRow);
                }
                sb.append("\n");

            }


            //		if (fileName != null) {
            //			File f = new File(fileName);
            //			FileOutputStream fos = new FileOutputStream(f);
            //			OutputStreamWriter osw = new OutputStreamWriter(fos, "utf8");
            //			BufferedWriter bw = new BufferedWriter(osw);
            //			bw.write("\uFEFF");
            //			bw.write(sb + "");
            //			bw.close();
            //			osw.close();
            //			fos.close();
            //			result.put("status", true);
            //			// result.put("result", sb + "");
            //			// log.info("============= SB ===================");
            //			// log.info("+++ SB +++ " + sb + "");
            //			// log.info("============= SB ////////////////////////");
            //
            //		}
            log.info("LENGTH:" + rawResult.size() + " - " + rawHeaders.size());
            result.put("oracleResult", rawResult);
            result.put("oracleHeaders", rawHeaders);
        }
        return result;
    }

    public static Map<String, Object> executeOracleQuery(String query, String dbSchema, String fileName,
                                                         boolean throwError) throws IOException {
        return executeOracleQuery(query, dbSchema, fileName, throwError, false);
    }

    public static Map<String, Object> executeOracleQuery(String query, String dbSchema, String fileName,
                                                         boolean throwError, boolean collect) throws IOException {
        Map result = new HashMap();
        if ((query == null) || (query.trim().length() < 1)) {
            result.put("status", false);
            result.put("result", "Query Empty");
            return result;
        }
        // Replace params
        // for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
        // query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
        // }

        // Query data
        SparkSession spark = SparkUtils.getAvailableSparkSession();

        addUDFToSpark(spark);


        Dataset<Row> data = null;
        log.info("QUERY: " + query);
        // log.info("dbSchema: " + dbSchema);
        // log.info((String)Constants.dataConfig.get(dbSchema).get("url"));
        // log.info((String)Constants.dataConfig.get(dbSchema).get("user"));
        // log.info((String)Constants.dataConfig.get(dbSchema).get("password"));
        try {
            data = spark.read().format("jdbc").option("driver", "oracle.jdbc.OracleDriver")
                    .option("url", (String) Constants.dataConfig.get(dbSchema).get("url")).option("fetchSize", "1000")
                    //                 .option("dbtable", "(select * from cust_mobile) cust_mobile")
                    .option("dbtable", "( " + query + " ) oracle_query_result")
                    .option("user", (String) Constants.dataConfig.get(dbSchema).get("user"))
                    .option("password", (String) Constants.dataConfig.get(dbSchema).get("password"))
                    .option("oracle.jdbc.mapDateToTimestamp", "true").load();
            result.put("status", true);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while fetching data");
            result.put("status", false);
            result.put("result", "Error: " + e.toString());
            if (throwError) {
                throw e;
            }
            return result;
        }

        // Fetch data
        log.info("Fucking here..." + data.count());
        List<Row> listRows = data.collectAsList();

        ArrayList<ArrayList> rawResult = new ArrayList<ArrayList>();
        ArrayList<String> rawHeaders = new ArrayList<String>();
        // log.info("Collect done, " + listRows.size());
        StringBuilder sb = new StringBuilder();
        // Gson gson = new Gson();
        String[] cols = data.columns();
        int colNo = 0;

        for (String col : cols) {
            log.info("Column" + colNo++ + ": " + col);
            sb.append(col).append(",");

            rawHeaders.add(col);
        }
        sb.append("\n");
        log.info("Total columns: " + colNo);
        log.info("Total rows: " + listRows.size());

        int count = 0;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        for (Row row : listRows) {
            count++;

            ArrayList rawRow = new ArrayList();

            for (String col : cols) {
                Object val = row.getAs(col);
                if (val != null) {
                    String valStr = val.toString();
                    Matcher m = p.matcher(valStr);
                    if (m.find()) { // Float
                        sb.append('"');
                        sb.append(nf.format(Double.parseDouble(m.group(0))));
                        sb.append('"');
                    } else { // String
                        if (valStr.indexOf('"') >= 0) {
                            valStr = valStr.replace("\"", "\"\"");
                        }
                        if ((valStr.indexOf(",") >= 0) || (valStr.indexOf('"') >= 0)) {
                            sb.append('"').append(valStr.replace("\n", " ").replace("\r", " ")).append('"');
                        } else {
                            sb.append(valStr.replace("\n", " ").replace("\r", " "));
                        }
                    }
                }
                // add data to rawRow
                rawRow.add(val);

                sb.append(",");
            }
            if (count <= 1000) {
                rawResult.add(rawRow);
            }
            sb.append("\n");

        }


        if (fileName != null) {
            File f = new File(fileName);
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write("\uFEFF");
            bw.write(sb.toString());
            bw.close();
            osw.close();
            fos.close();
            result.put("status", true);
            //          result.put("result", sb.toString());
        }

        result.put("oracleResult", rawResult);
        result.put("oracleHeaders", rawHeaders);
        result.put("totalRows", listRows.size());

        return result;
    }

    public static Dataset<Row> executeSparkQuery(String query) throws IOException {
        return executeSparkQuery(query, false);
    }

    public static Dataset<Row> executeSparkQuery(String query, boolean throwError)
            throws IOException {
        if ((query == null) || (query.length() < 1)) {
            return null;
        }
        if (!isToND) {
            // Replace params
            for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
                query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
            }

            // Query data
            SparkSession spark = SparkUtils.getAvailableSparkSession();
            addUDFToSpark(spark);
            Dataset<Row> data = null;
            try {
                data = queryToDataset(spark, query);
                //          data = spark.sql(query);
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Error while fetching data");
                if (throwError) {
                    throw e;
                } else {
                    return null;
                }
            } finally {
                SparkUtils.releaseSparkSession(spark);
            }
        } else {
            return executeToNDQuery(query);
        }
    }

    public static Dataset<Row> executeSparkQuery(String query, SparkSession spark, boolean closeSesion)
            throws IOException {
        if ((query == null) || (query.length() < 1)) {
            return null;
        }
        // Replace params
        for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
            query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
        }
        log.info("TOGREP | Query " + query);
        // Query data
        addUDFToSpark(spark);
        Dataset<Row> data = null;
        try {
            //          data = spark.sql(query);
            data = queryToDataset(spark, query);
            log.info("TOGREP | DONE QUERY");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while fetching data");
            return null;
        } finally {
            if (closeSesion) {
                SparkUtils.releaseSparkSession(spark);
            }
        }
    }

    public static Dataset<Row> executeSparkQuery(String query, SparkSession spark)
            throws IOException {
        if ((query == null) || (query.length() < 1)) {
            return null;
        }
        // Replace params
        for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
            query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
        }
        log.info("TOGREP | Query " + query);
        // Query data
        addUDFToSpark(spark);
        Dataset<Row> data = null;
        try {
            //          data = spark.sql(query);
            data = queryToDataset(spark, query);
            log.info("TOGREP | DONE QUERY");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while fetching data");
            return null;
        } finally {
            SparkUtils.releaseSparkSession(spark);
        }
    }

    /**
     * @param spark SparkSession
     * @param query The query you want to query, lol
     * @return a data set
     */
    public static Dataset<Row> queryToDataset(SparkSession spark, String query) {
        if (!isToND) {
            Dataset<Row> result = null;
            addUDFToSpark(spark);
            int count = 0;
            while (count < 10) {
                log.info(String.format("TOGREP | Trying query %s for the %d times", query, count + 1));
                try {
                    result = spark.sql(query);
                    result.cache();
                    result.show();
                    count = 10;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!e.toString().contains("SparkContext")) {
                        // It does look like Spark Context is going down
                        throw e;
                    }
                }
                // data would be available for the function
                // Due to the spark context might shutdown, we need to retry it to make sure
                count++;
                if (count < 10) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                }
            }
            return result;
        } else {
            return executeToNDQuery(query);
        }
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, String header, String type)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        return executeSparkQuery(query, fileName, header, false, type);
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, String header,
                                                        boolean throwError, String type) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        return executeSparkQuery(query, fileName, header, throwError, type, null, null);
    }

    public static Map<String, Object> executeSparkQuery(String query, String fileName, String header,
                                                        boolean throwError, String type, String rwCode, String rwName)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        SparkSession spark = SparkUtils.getAvailableSparkSession();
        try {
            Map result = new HashMap();
            if ((query == null) || (query.trim().length() < 1)) {
                result.put("status", false);
                result.put("result", "Query Empty");
                return result;
            }
            // Replace params
            for (Map.Entry<String, String> entry : Constants.dataSource.entrySet()) {
                query = query.replaceAll(":" + entry.getKey() + ":", entry.getValue());
            }
            addUDFToSpark(spark);
            // Query data
            // spark.conf().set("spark.sql.crossJoin.enabled", "true");
            Dataset<Row> data = null;
            try {
                if (!isToND) {
                    data = queryToDataset(spark, query);
                } else {
                    data = executeToNDQuery(query);
                }
                if (data != null) {
                    result.put("status", true);
                } else {
                    log.error("Error while fetching data");
                    result.put("status", false);
                    result.put("result", "Error: ");
                    return result;
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while fetching data");
                result.put("status", false);
                result.put("result", "Error: " + e.toString());
                if (throwError) {
                    throw e;
                }
                return result;
            } finally {
                // SparkUtils.releaseSparkSession(spark);
            }
            // Fetch data
            log.info("Fucking here..." + data.count());
            List<Row> listRows = data.collectAsList();
            ArrayList<ArrayList> rawResult = new ArrayList<ArrayList>();
            ArrayList<String> rawHeaders = new ArrayList<String>();
            StringBuilder sb = new StringBuilder();
            String[] cols = data.columns();
            int colNo = 0;
            for (String col : cols) {
                System.out.println("Column" + colNo++ + ": " + col);
                sb.append(col).append(",");

                rawHeaders.add(col);
            }
            sb.append("\n");
            log.info("Total columns: " + colNo);
            log.info("Total rows: " + listRows.size());
            int count = 0;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            for (Row row : listRows) {
                count++;
                ArrayList rawRow = new ArrayList();
                for (String col : cols) {
                    Object val = row.getAs(col);
                    if (val != null) {
                        String valStr = "";
                        if ((val instanceof Double) || (val instanceof Float)) {
                            String tmpValue = String.format("%.2f", val);
                            if (tmpValue.indexOf(".00") >= 0) {
                                valStr = tmpValue.substring(0, tmpValue.indexOf(".00"));
                            } else {
                                valStr = tmpValue;
                            }
                        } else {
                            valStr = val.toString();
                        }
                        Matcher m = p.matcher(valStr);
                        if (m.find()) { // Float
                            String tmpValue = m.group(0);
                            if (tmpValue.indexOf(".00") >= 0) {
                                sb.append(tmpValue.substring(0, tmpValue.indexOf(".00")));
                            } else {
                                sb.append(tmpValue); // tmp debug
                            }
                        } else { // String
                            if (valStr.indexOf('"') >= 0) {
                                valStr = valStr.toString().replace("\"", "\"\"");
                            }
                            if ((valStr.indexOf(",") >= 0) || (valStr.indexOf('"') >= 0)) {
                                sb.append('"').append(valStr.toString().replace("\n", " ").replace("\r", " "))
                                        .append('"');
                            } else {
                                sb.append(valStr.toString().replace("\n", " ").replace("\r", " "));
                            }
                        }
                    }
                    // add data to rawResult
                    rawRow.add(val);
                    sb.append(",");
                }
                if (count <= 1000) {
                    rawResult.add(rawRow);
                }
                sb.append("\n");
            }
            if (fileName != null) {
                if (null == type) {
                    type = "";
                }
                switch (type) {
                    case com.hh.constant.Constants.TYPE_XLSX:
                        exportReportEXCEL(data, fileName, true);
                        result.put("status", true);
                        result.put("result", sb.toString());
                        break;
                    case com.hh.constant.Constants.TYPE_TXT:
                        exportExcelTEXT(data, fileName, true);
                        result.put("status", true);
                        result.put("result", sb.toString());
                        break;
                    default:
                        File f = new File(fileName);
                        FileOutputStream fos = new FileOutputStream(f);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf8");
                        BufferedWriter bw = new BufferedWriter(osw);
                        bw.write("\uFEFF"); // BOM
                        if ((header != null) && (header.length() > 0)) {
                            bw.write('"');
                            bw.write(header);
                            bw.write('"');
                            bw.write("\n\n");
                        }
                        bw.write(sb.toString());
                        bw.close();
                        osw.close();
                        fos.close();
                        result.put("status", true);
                        result.put("result", sb.toString());
                        break;
                }
            }
            log.info("LENGTH:" + rawResult.size() + " - " + rawHeaders.size());
            result.put("oracleResult", rawResult);
            result.put("oracleHeaders", rawHeaders);
            result.put("totalRows", listRows.size());
            // BEGIN TIENLV Them du lieu file nhan duoc ra db
            try {
                // B1 Tao bang neu chua ton tai bang
                // RW_CODE RW_NAME CREATE_DATE RW_REQUEST_ID RW_REQUEST_DATE RW_STATUS RW_TRANS_AMOUNT
                // parquet.`hdfs://10.58.244.172:8020/storage/bi/hdfs/dbkd/dbkd_log_reconcile_cb.parquet`
                // B2 lay ket qua trong file
                // B3 Gop ket qua
                // B4 Ghi them vao cuoi file
                if (null != rwCode && !rwCode.isEmpty() && null != rwName && !rwName.isEmpty()) {
                    String dir = "parquet.`hdfs://10.58.244.172:8020/storage/bi/hdfs/dbkd/dbkd_log_reconcile_cb.parquet`"
                            .replace("parquet.", "").replace("`", "");
                    Dataset<Row> tempData = data.select(functions.lit("RW_REQUEST_ID"),
                            functions.lit("RW_REQUEST_DATE"), functions.lit("RW_STATUS"),
                            functions.lit("RW_TRANS_AMOUNT"));
                    tempData.withColumn("RW_CODE", functions.lit(rwCode));
                    tempData.withColumn("RW_NAME", functions.lit(rwName));
                    tempData.withColumn("RW_CREATED_DATE", functions.lit(new Date()));

                    tempData.repartition(1).write().mode(SaveMode.Append).option("header", "true").csv(dir);
                }
            } catch (Exception e) {
                log.info("some thing error happens");
            }
            // END TIENLV Them du lieu file nhan duoc ra db
            return result;
        } finally {
            SparkUtils.releaseSparkSession(spark);
        }
    }

    public static String exportReportEXCEL(Dataset<Row> data, String fileName) {
        return exportReportEXCEL(data, fileName, null);
    }

    public static String exportReportEXCEL(Dataset<Row> data, String fileName, Boolean isFullPath) {
        try {
            String[] headers = data.columns();
            String fullPath = "";
            if (isFullPath) {
                fullPath = fileName;
            } else {
                fullPath = FOLDER_REPORT.concat(fileName).concat(com.hh.constant.Constants.TYPE_XLSX);
            }
            List<Row> listData = data.collectAsList();
            final int heightheader = 1;
            WriteDataToExcel.importExcel(listData, fullPath, heightheader, headers);

            return fullPath;
        } catch (Exception e) {
            log.info("ERROR when export report format .xlsx");
            e.printStackTrace();
            return null;
        }
    }

    public static String exportReportCSV(Dataset<Row> data, String fileName) {
        try {
            String fullPath = FOLDER_REPORT.concat(fileName).concat(com.hh.constant.Constants.TYPE_CSV);
            data.repartition(1).write().option("header", "true").mode("overwrite").option("delimeter", "\t")
                    .format("com.databricks.spark.csv").save(fullPath);
            File dir = new File(fullPath);
            File[] matches = dir.listFiles();
            if (null == matches) {
                return null;
            }
            for (File file : matches) {
                System.out.println(file.getName());
                if (file.getName().endsWith(".csv")) {
                    fullPath = fullPath.concat("/").concat(file.getName());
                    return fullPath;
                }
            }
            return fullPath;
        } catch (Exception e) {
            log.info("ERROR when export report format .csv");
            e.printStackTrace();
            return null;
        }
    }

    public static String exportExcelTEXT(Dataset<Row> data, String fileName) {
        return exportExcelTEXT(data, fileName, null);
    }

    public static String exportExcelTEXT(Dataset<Row> data, String fileName, Boolean isFullPath) {
        try {
            String fullPath = "";
            if (isFullPath) {
                fullPath = fileName;
            } else {
                fullPath = FOLDER_REPORT.concat(fileName).concat(com.hh.constant.Constants.TYPE_TXT);
            }
            data.repartition(1).write().option("header", "true").option("delimeter", "\t")
                    .format("com.databricks.spark.text").save(fullPath);
            return fullPath;
        } catch (Exception e) {
            log.info("ERROR when export report format .txt");
            e.printStackTrace();
            return null;
        }
    }

    public static Dataset<Row> executeToNDQuery(String query) {
        String filePath = LivyUtils.queryToFile(query);
        if (filePath != null) {
            return csvToDS(SparkUtils.getAvailableSparkSession(), filePath);
        }
        return null;
    }

    public static Dataset<Row> csvToDS(SparkSession spark, String filePath) {
        try {
            Dataset<Row> ds = spark.read().option("header", "true").option("delimeter", ",").csv(filePath);
            return ds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            SparkUtils.releaseSparkSession(spark);
        }
    }
}
