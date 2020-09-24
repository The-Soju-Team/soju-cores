package com.hh.constant;

import com.hh.util.ConfigUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruongNX25
 */

public class Constants {

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
    // Network Operator Code
    public static final String NETWORK_OPERATOR_ID_MOBIFONE = "01";
    public static final String NETWORK_OPERATOR_ID_VINAPHONE = "02";
    public static final String NETWORK_OPERATOR_ID_VIETTEL = "04";
    public static final String NETWORK_OPERATOR_ID_VIETNAMOBILE = "05";
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String USE_KERBEROS_IN_HDFS = "1";

    // end Netrowk Operator Code
    public static final String HDFS_URL = config.getConfig("hdfs-url");
    public static final String HDFS_KEYTAB_USER = config.getConfig("hdfs-keytab-user");
    public static final String HDFS_KEYTAB_PATH = config.getConfig("hdfs-keytab-path");
    public static final String KERBEROS_HDFS_NAMENODE_PRINCIPAL = config
            .getConfig("kerberos-hdfs-namenode-principal");
    public static final String KERBEROS_HDFS_DATANODE_PRINCIPAL = config
            .getConfig("kerberos-hdfs-datanode-principal");
    public static final String KERBEROS_YARN_RESOURCEMANAGER_PRINCIPAL = config
            .getConfig("kerberos-yarn-resourcemanager-principal");

    public static final String KERBEROS_REALM = config.getConfig("kerberos-realm");
    public static final String KERBEROS_KDC = config.getConfig("kerberos-kdc");
    public static FileSystem fileSystem = null;

    private Constants() {

    }

    public static void getHDFSSystemFile() throws Exception {
        if (fileSystem == null) {
            if ("1".equals(USE_KERBEROS_IN_HDFS)) {
                // set kerberos host and realm
                // System.setProperty("java.security.krb5.realm", "BIGDATA.VN");
                System.setProperty("java.security.krb5.realm", Constants.KERBEROS_REALM);
                // log.info("=== 1");
                // System.setProperty("java.security.krb5.kdc", "10.58.244.224");
                System.setProperty("java.security.krb5.kdc", Constants.KERBEROS_KDC);
                // System.setProperty("java.security.krb5.kdc", "127.0.0.1");
                // log.info("=== 2");

                Configuration configuration = new Configuration();
                // log.info("=== 3");
                // HDFS authentication
                configuration.set("hadoop.security.authentication", "kerberos");
                configuration.set("hadoop.security.authorization", "true");
                // configuration.set("fs.defaultFS", "hdfs://10.58.244.172:8020");
                configuration.set("fs.defaultFS", Constants.HDFS_URL);
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
                configuration.set("dfs.namenode.kerberos.principal.pattern",
                        Constants.KERBEROS_HDFS_NAMENODE_PRINCIPAL);

                // log.info("=== 6");

                UserGroupInformation.setConfiguration(configuration);
                // log.info("=== 7");

                // UserGroupInformation.loginUserFromKeytab("dbathgate@DRB.COM",
                // "src/main/resources/dbathgate.keytab");
                // UserGroupInformation.loginUserFromKeytab("bi_admin@BIGDATA.VN",
                // "/home/donnn/Downloads/keytabs/admin.bi.keytab");
                UserGroupInformation.loginUserFromKeytab(Constants.HDFS_KEYTAB_USER,
                        Constants.HDFS_KEYTAB_PATH);

                // log.info("=== 8");

                fileSystem = FileSystem.get(URI.create(HDFS_URL), configuration);

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
                        Constants.KERBEROS_HDFS_NAMENODE_PRINCIPAL);
                sparkConfiguration.set("spark.hadoop.dfs.datanode.kerberos.principal",
                        Constants.KERBEROS_HDFS_DATANODE_PRINCIPAL);
                sparkConfiguration.set("spark.hadoop.yarn.resourcemanager.principal",
                        Constants.KERBEROS_YARN_RESOURCEMANAGER_PRINCIPAL);

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
                fileSystem = FileSystem.get(URI.create(HDFS_URL), conf);
                System.out.println("=== Spark normal HDFS configured");
            }

        }
    }

    public static enum GrowthType {
        SAMEPREV, MONTHSPREV
    }

}
