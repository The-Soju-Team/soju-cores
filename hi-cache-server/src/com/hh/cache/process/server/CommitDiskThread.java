/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.hh.cache.run.StartApp;

/**
 * @author HienDM
 */
public class CommitDiskThread extends Thread {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommitDiskThread.class.getSimpleName());

    public static FileOutputStream cacheWriter;
    public static ConcurrentLinkedQueue<byte[]> content = new ConcurrentLinkedQueue();
    private static final File cacheFile = new File(StartApp.config.getConfig("data-path") + "/cache.log").getAbsoluteFile();
    private static final int MAX_SIZE_CONTENT_COMMIT_TO_DISK;
    static {
        int config = 1000;
        try {
            config = new Integer(StartApp.config.getConfig("max_size_content_commit_to_disk"));
            if (0 > config) {
                log.error("MAX_SIZE_CONTENT_COMMIT_TO_DISK config error !!!  " + config);
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("MAX_SIZE_CONTENT_COMMIT_TO_DISK default 1000  ");
            config = 1000;
        }
        MAX_SIZE_CONTENT_COMMIT_TO_DISK = config;
    }
    @Override
    public void run() {
        writeCacheToFile();
    }

    private static synchronized void writeCacheToFile() {
        while (true) {
            log.info("Committing Cache To Disk");
            try {
                if (cacheWriter == null) {
                    cacheWriter = new FileOutputStream(cacheFile, true);
                }
                byte[] data = null;
                while (!content.isEmpty()) {
                    data = content.poll();
                    if (data != null) {
                        cacheWriter.write(data);
                    }
                }
                if (data != null) {
                    cacheWriter.flush();
                    log.debug("write data to disk");
                }
                log.info("Done Committing Cache To Disk");
                log.info("Compressing Cache To Disk");
                LoadCacheProcess.compressCache();
                log.info("Done Compressing Cache To Disk");
                Thread.sleep(30000l);
            } catch (Exception ex) {
                log.error("Error when commit to disk", ex);
            } finally {

            }
        }
    }

    public static void append(String json) {
        log.info("content.size()" + content.size());
        log.debug("COMMAND: " + json);
        byte[] bytes = (json + "\n").getBytes();
        int length = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] lengthArr = buffer.putInt(length).array();
        byte[] data = new byte[length + 4];
        System.arraycopy(lengthArr, 0, data, 0, 4); // add do dai vao 4 byte dau
        System.arraycopy(bytes, 0, data, 4, length); // add du lieu Object
        content.offer(data);

        //        if (content.size() > MAX_SIZE_CONTENT_COMMIT_TO_DISK) {
        //            content.poll();
        //        }
    }
}
