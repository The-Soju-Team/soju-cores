package com.hh.constant;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;

import com.hh.util.ConfigUtils;

/**
 * @author TruongNX25
 */

public class Constants {
    private static final Logger LOG = Logger.getLogger(Constants.class);
    public static ConfigUtils config;
    // For QueryUtils only
    public static Map<String, String> dataSource;
    public static Map<String, HashMap> dataConfig;
    // End for QueryUtils only

    public static final String ENV = "dev";
    public static final String SSO_USERNAME = "sso_username";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String SSO_USER_NAME = "sso_user_name";
    public static final String USER_NAME = "user_name";
    public static final int PRED_4MTHS = 4;
    public static final int PRED_6MTHS = 6;
    public static final String STATUS_DEFAULT_OF_TREE = "Not found";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_COLON = ":";
    public static final String SYMBOL_HYPHEN = "-";
    public static final String SYMBOL_PERCENT = "%";
    public static final String SYMBOL_SPACE = " ";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_QUESTION = "?";
    public static final String SYMBOL_PRIME = "'";
    public static final String SYMBOL_SLASH = "/";
    public static final String SYMBOL_BACK_SLASH = "\\";

    public static final String TYPE_TXT = ".txt";
    public static final String TYPE_CSV = ".csv";
    public static final String TYPE_XLSX = ".xlsx";
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public static final String CO_LOI_XAY_RA = "Có lỗi xảy ra";

    // Network Operator Code
    public static final String NETWORK_OPERATOR_ID_MOBIFONE = "01";
    public static final String NETWORK_OPERATOR_ID_VINAPHONE = "02";
    public static final String NETWORK_OPERATOR_ID_VIETTEL = "04";
    public static final String NETWORK_OPERATOR_ID_VIETNAMOBILE = "05";
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public String USE_KERBEROS_IN_HDFS = config.getConfig("use-kerberos-in-hdfs");

    // Begin configs producer
    public static String BOOSTRAP_SERVER_DEFAULT;
    public static String BOOSTRAP_SERVER;
    public static String CLIENT_ID_DEFAULT;
    public static String REQUEST_TIMEOUT_MS_CONFIG;
    public static String ACKS;
    public static int RETRIES;
    public static int BATCH_SIZE;
    public static int LINGER_MS;
    public static int BUFFER_MEMORY;
    // End configs producer
    // Begin configs consumer
    public static String HEARTBEAT_INTERVAL_MS_CONFIG;
    public static String MAX_POLL_INTERVAL_MS_CONFIG;
    public static String ENABLE_AUTO_COMMIT;
    public static String AUTO_COMMIT_INTERVAL_MS;
    public static String SESSION_TIMEOUT_MS;
    // End configs consumer

    public static String HOST_REDIS_DEFAULT;
    public static int PORT_REDIS_DEFAULT;
    public static String REDIS_PASSWORD_DEFAULT;
    public static int REDIS_DATABASE_NUMBER_DEFAULT;

    // end Network Operator Code
    public String HDFS_URL = config.getConfig("hdfs-url");
    public String HDFS_KEYTAB_USER = config.getConfig("hdfs-keytab-user");
    public String HDFS_KEYTAB_PATH = config.getConfig("hdfs-keytab-path");
    public String KERBEROS_HDFS_NAMENODE_PRINCIPAL = config.getConfig("kerberos-hdfs-namenode-principal");
    public String KERBEROS_HDFS_DATANODE_PRINCIPAL = config.getConfig("kerberos-hdfs-datanode-principal");
    public String KERBEROS_YARN_RESOURCEMANAGER_PRINCIPAL = config.getConfig("kerberos-yarn-resourcemanager-principal");
    public String KERBEROS_REALM = config.getConfig("kerberos-realm");
    public String KERBEROS_KDC = config.getConfig("kerberos-kdc");
    public static FileSystem fileSystem = null;

    public static final String RW_CHECK_RULE = "rw_check_rule";
    public static final String RW_CHECK_SMS_QUERY = "rw_check_sms_query";
    public static final String RW_CHECK_MAIL_QUERY = "rw_check_mail_query";
    public static final String EMPTY = "";

    // static {
    // BOOSTRAP_SERVER_DEFAULT = Constants.config.getConfig("boostrap_server");
    // CLIENT_ID_DEFAULT = Constants.config.getConfig("client_id");
    // REQUEST_TIMEOUT_MS_CONFIG = Constants.config.getConfig("request_timeout_ms_config");
    // HEARTBEAT_INTERVAL_MS_CONFIG = Constants.config.getConfig("heartbeat_interval_ms_config");
    // MAX_POLL_INTERVAL_MS_CONFIG = Constants.config.getConfig("max_poll_interval_ms_config");
    // ACKS = Constants.config.getConfig("request_timeout_ms_config");
    // RETRIES = 0;
    // BATCH_SIZE = 16384;
    // LINGER_MS = 1;
    // BUFFER_MEMORY = 33554432;
    // ENABLE_AUTO_COMMIT = Constants.config.getConfig("request_timeout_ms_config");
    // AUTO_COMMIT_INTERVAL_MS = Constants.config.getConfig("request_timeout_ms_config");
    // SESSION_TIMEOUT_MS = Constants.config.getConfig("request_timeout_ms_config");
    // HOST_REDIS_DEFAULT = Constants.config.getConfig("host_redis_default");
    // PORT_REDIS_DEFAULT = new Integer(config.getConfig("port_redis_default"));
    // }

    public Constants() {

    }

    /**
     * Init HDFSFileSystem, this method should be called from other method
     *
     * @throws Exception
     */
    public void getHDFSSystemFile() throws Exception {
        if (fileSystem == null) {
            if ("1".equals(this.USE_KERBEROS_IN_HDFS)) {
                // set kerberos host and realm
                // System.setProperty("java.security.krb5.realm", "BIGDATA.VN");
                System.setProperty("java.security.krb5.realm", this.KERBEROS_REALM);
                // log.info("=== 1");
                // System.setProperty("java.security.krb5.kdc", "10.58.244.224");
                System.setProperty("java.security.krb5.kdc", this.KERBEROS_KDC);
                // System.setProperty("java.security.krb5.kdc", "127.0.0.1");
                // log.info("=== 2");

                Configuration configuration = new Configuration();
                // log.info("=== 3");
                // HDFS authentication
                configuration.set("hadoop.security.authentication", "kerberos");
                configuration.set("hadoop.security.authorization", "true");
                // configuration.set("fs.defaultFS", "hdfs://10.58.244.172:8020");
                configuration.set("fs.defaultFS", this.HDFS_URL);
                configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());

                configuration.set("ipc.client.fallback-to-simple-auth-allowed", "true"); // allow connect to INSECURE
                // cluster.

                // log.info("=== 4");

                // hack for running locally with fake DNS records
                // set this to true if overriding the host name in /etc/hosts
                configuration.set("dfs.client.use.datanode.hostname", "true");

                // log.info("=== 5");

                // server principal
                // the kerberos principle that the namenode is using
                // configuration.set("dfs.namenode.kerberos.principal.pattern",
                // "hduser/*@DRB.COM");
                // configuration.set("dfs.namenode.kerberos.principal.pattern",
                // "nn/host172.bigdata.vn@BIGDATA.VN");
                configuration.set("dfs.namenode.kerberos.principal.pattern", this.KERBEROS_HDFS_NAMENODE_PRINCIPAL);

                // log.info("=== 6");

                UserGroupInformation.setConfiguration(configuration);
                // log.info("=== 7");

                // UserGroupInformation.loginUserFromKeytab("dbathgate@DRB.COM",
                // "src/main/resources/dbathgate.keytab");
                // UserGroupInformation.loginUserFromKeytab("bi_admin@BIGDATA.VN",
                // "/home/donnn/Downloads/keytabs/admin.bi.keytab");
                UserGroupInformation.loginUserFromKeytab(this.HDFS_KEYTAB_USER, this.HDFS_KEYTAB_PATH);

                // log.info("=== 8");

                fileSystem = FileSystem.get(URI.create(this.HDFS_URL), configuration);

                // config spark session global
                SparkConf sparkConfiguration = new SparkConf();
                sparkConfiguration.set("spark.app.name", "Business Intelligence");

                // "spark.authenticate.secret" must be specified. but can be any string. WTF???
                // sparkConfiguration.set("spark.authenticate.secret", "#Bmin%2z19!");

                // use kerberos for HDFS authentication
                sparkConfiguration.set("spark.hadoop.hadoop.security.authentication", "kerberos");
                sparkConfiguration.set("spark.hadoop.hadoop.security.authorization", "true");

                // set principal
                sparkConfiguration.set("spark.hadoop.dfs.namenode.kerberos.principal",
                        this.KERBEROS_HDFS_NAMENODE_PRINCIPAL);
                sparkConfiguration.set("spark.hadoop.dfs.datanode.kerberos.principal",
                        this.KERBEROS_HDFS_DATANODE_PRINCIPAL);
                sparkConfiguration.set("spark.hadoop.yarn.resourcemanager.principal",
                        this.KERBEROS_YARN_RESOURCEMANAGER_PRINCIPAL);

                // config kerberos token for all spark-sessions
                UserGroupInformation.setConfiguration(SparkHadoopUtil.get().newConfiguration(sparkConfiguration));
                Credentials credentials = UserGroupInformation.getLoginUser().getCredentials();
                SparkHadoopUtil.get().addCurrentUserCredentials(credentials);

                // log.info("=== 9");
                System.out.println("=== Spark kerberos HDFS configured");
            } else {
                Configuration conf = new Configuration();
                conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
                conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
                fileSystem = FileSystem.get(URI.create(this.HDFS_URL), conf);
                System.out.println("=== Spark normal HDFS configured");
            }

        }
    }

    public static enum GrowthType {
        SAMEPREV, MONTHSPREV
    }

}
