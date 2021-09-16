package com.hh.spark;

import com.hh.constant.Constants;
import com.hh.util.ConfigUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author TruongNX25
 */

public class SparkUtils {
    public static final Logger log = Logger.getLogger(SparkUtils.class);

    @Deprecated // Useful only in local session
    private static final int MAXIMUM_JOB = 20;
    private static final int NO_OF_SPARK_SESSION = 2;
    private static int currentNumberOfJobs = 0;
    private static final Vector<SparkSession> listSpark = new Vector<>();
    private static final int[] availableFlag = new int[NO_OF_SPARK_SESSION];
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * isPrivateApp: allow an app to run full resource from (8h pm -> 7h am)
     */
    private static String isPrivateApp = "public";

    private SparkUtils(ConfigUtils config) {

    }

    /**
     * Create SparkSession using preconfig in StartApp.config
     */


    private static void initLocalSpark() {
        if (listSpark.size() == 0) {
            SparkSession spark = SparkSession.builder().appName("AML")
                    .config("spark.master", "local[10]").getOrCreate();
            for (int i = 0; i < NO_OF_SPARK_SESSION; i++) {
                listSpark.add(spark);
            }
            Arrays.fill(availableFlag, 0);
        }
    }

    private static void initYarnSpark() {
        if (listSpark.size() == 0) {
            SparkSession spark = SparkSession.builder().getOrCreate();
            for (int i = 0; i < NO_OF_SPARK_SESSION; i++)
                listSpark.add(spark);
            Arrays.fill(availableFlag, 0);
        }
    }

    private static void initStandaloneSpark() {
        if (listSpark.size() == 0) {
            currentNumberOfJobs = 0;
            log.info("TOGREP | CREATE " + NO_OF_SPARK_SESSION + " SPARK SESSIONS");
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            String appType = Constants.config.getConfig("app-type");
            if (appType != null && appType.equals("private")) {
                isPrivateApp = appType;
            }
            for (int i = 0; i < NO_OF_SPARK_SESSION; i++) {
                SparkSession spark;
                /**
                 * We need to add our jar path to avoid class cast exception
                 * https://stackoverflow.com/questions/39953245/how-to-fix-java-lang-classcastexception-cannot-assign-instance-of-scala-collect
                 */
                if ((hour >= 20 || hour <= 7) && isPrivateApp.equals("private")) {
                    /**
                     * This block is used for creating spark session for our private app, it should have more memory, CPU cores to do the jobs faster
                     */
                    spark = SparkSession.builder().appName(Constants.config.getConfig("spark.appname"))
                            .config("spark.master", Constants.config.getConfig("spark.master"))
                            .config("spark.executor.memory", Constants.config.getConfig("spark.executor.memory"))
                            .config("spark.cores.max", "60")
                            .config("spark.ui.retainedJobs", Constants.config.getConfig("spark.ui.retainedJobs"))
                            .config("spark.ui.retainedStages", Constants.config.getConfig("spark.ui.retainedStages"))
                            .config("spark.ui.retainedTasks", Constants.config.getConfig("spark.ui.retainedTasks"))
                            .config("spark.worker.ui.retainedExecutors",
                                    Constants.config.getConfig("spark.worker.ui.retainedExecutors"))
                            .config("spark.worker.ui.retainedDrivers",
                                    Constants.config.getConfig("spark.worker.ui.retainedDrivers"))
                            .config("spark.sql.ui.retainedExecutions",
                                    Constants.config.getConfig("spark.sql.ui.retainedExecutions"))
                            .config("spark.streaming.ui.retainedBatches",
                                    Constants.config.getConfig("spark.streaming.ui.retainedBatches"))
                            .config("spark.ui.port", Constants.config.getConfig("spark.ui.port"))
                            .config("spark.scheduler.mode", Constants.config.getConfig("spark.scheduler.mode"))
                            /**
                             * We need to add our jar path to avoid class cast exception
                             * https://stackoverflow.com/questions/39953245/how-to-fix-java-lang-classcastexception-cannot-assign-instance-of-scala-collect
                             */
                            .config("spark.jars", Constants.config.getConfig("spark.jars")).getOrCreate();
                } else {
                    spark = SparkSession.builder().appName(Constants.config.getConfig("spark.appname"))
                            .config("spark.master", Constants.config.getConfig("spark.master"))
                            .config("spark.executor.memory", Constants.config.getConfig("spark.executor.memory"))
                            .config("spark.cores.max", Constants.config.getConfig("spark.cores.max"))
                            .config("spark.ui.retainedJobs", Constants.config.getConfig("spark.ui.retainedJobs"))
                            .config("spark.ui.retainedStages", Constants.config.getConfig("spark.ui.retainedStages"))
                            .config("spark.ui.retainedTasks", Constants.config.getConfig("spark.ui.retainedTasks"))
                            .config("spark.worker.ui.retainedExecutors",
                                    Constants.config.getConfig("spark.worker.ui.retainedExecutors"))
                            .config("spark.worker.ui.retainedDrivers",
                                    Constants.config.getConfig("spark.worker.ui.retainedDrivers"))
                            .config("spark.sql.ui.retainedExecutions",
                                    Constants.config.getConfig("spark.sql.ui.retainedExecutions"))
                            .config("spark.streaming.ui.retainedBatches",
                                    Constants.config.getConfig("spark.streaming.ui.retainedBatches"))
                            .config("spark.ui.port", Constants.config.getConfig("spark.ui.port"))
                            .config("spark.scheduler.mode", Constants.config.getConfig("spark.scheduler.mode"))
                            /**
                             * We need to add our jar path to avoid class cast exception
                             * https://stackoverflow.com/questions/39953245/how-to-fix-java-lang-classcastexception-cannot-assign-instance-of-scala-collect
                             */
                            .config("spark.jars", Constants.config.getConfig("spark.jars")).getOrCreate();
                }
                listSpark.add(spark);
                availableFlag[i] = 0;
            }
        }
    }

    public static void createSparkSession() {
        // Create spark session if null;
        lock.lock();
        String sparkType = Constants.config.getConfig("sparkType");
        if (null != sparkType && sparkType.equals("yarn")) {
            log.info("INITIALIZING SPARK YARN MODE");
            initYarnSpark();
        } else if (null != sparkType && sparkType.equals("standalone")) {
            log.info("INITIALIZING SPARK SPARK MODE");
            initStandaloneSpark();
        } else {
            log.info("INITIALIZING SPARK LOCAL MODE");
            initLocalSpark();
        }
        lock.unlock();

    }

    /**
     * We no longer need this method, it used to be return the available flag, but since we change our spark session from local -> cluster, standalone, this method is useless
     *
     * @return 0
     */
    @Deprecated
    private static int checkAvailableFlag() {
        currentNumberOfJobs++;
        return 0;
    }

    /**
     * Deprecated, same as @checkAvailableFlag
     *
     * @return
     */
    @Deprecated
    public static SparkSession getAvailableSparkSession() {
        if (listSpark.size() == 0) {
            createSparkSession();
        }
        while (true) {
            int returnValue = checkAvailableFlag();
            if (returnValue != -1) {
                log.info("TOGREP | USING SPARK SESSION NUMBER " + returnValue);
                return listSpark.get(returnValue);
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * If we do not close spark session, we should run out of memory soon, so at least call it sometimes if enough jobs has been done
     */
    public static void stopSpark() {
        lock.lock();
        try {
            log.info("TOGREP | Killing spark session");
            if (listSpark.size() != 0) {
                listSpark.get(0).stop();
                listSpark.clear();
            }
            log.info("TOGREP | Done killing spark session");
        } catch (Exception ignored) {

        } finally {
            lock.unlock();
        }
    }

    /**
     * The same as @checkAvailableFlag
     *
     * @param session
     */
    @Deprecated
    public static void releaseSparkSession(SparkSession session) {
    }

}
