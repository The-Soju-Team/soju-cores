package com.hh.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;

/**
 * @author TruongNX25
 */

public class HDFSUtils {
    private static final Logger log = Logger.getLogger(HDFSUtils.class);
    private static FileSystem fileSystem166 = null;
    // private static final String OUTPUT_HDFS_URL = StartApp.config.getConfig("output-hdfs-url");
    // output-hdfs-url=hdfs://10.58.244.166:9001
    private static final String OUTPUT_HDFS_URL = "hdfs://10.58.244.166:9001";

    public static FileSystem getHDFSSystemFile() throws Exception {
        if (fileSystem166 == null) {
            Configuration conf = new Configuration();
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
            fileSystem166 = FileSystem.get(URI.create(OUTPUT_HDFS_URL), conf);
        }
        return fileSystem166;
    }

    public static boolean downloadFromHDFSToLocal(String src, String dst) {
        return false;
    }

    /**
     * This function use destination folder in server.conf file
     *
     * @param src
     * @return absolute path
     */
    public static String downLoadFromHDFS166ToLocal(String src) throws Exception {
        // Hard-code, well, no choice at the moment guys
        String dest = "/u02/donnn/deploy/frontend/hdfs-download-local";
        if (dest != null && dest.length() > 0) {
            try {
                FileSystem fs = getHDFSSystemFile();
                FileStatus[] fileStatus = fs.listStatus(new Path(src));
                for (FileStatus status : fileStatus) {
                    if (!status.getPath().toString().toLowerCase().startsWith("_")) {
                        long startTime = System.nanoTime();
                        log.info("TOGREP | HDFS File: " + status.getPath().toString());
                        fs.copyToLocalFile(new Path(src), new Path(dest + File.separator + new Path(src).getName()));
                        log.info(String.format("TOGREP | Download %s took %f seconds", status.getPath().toString(), (System.nanoTime() - startTime) / Math.pow(10, 6)));
                        return new File(dest + File.separator + new Path(src).getName()).getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("TOGREP | Destination folder is null, cannot download to it");
        }
        return null;
    }
}
