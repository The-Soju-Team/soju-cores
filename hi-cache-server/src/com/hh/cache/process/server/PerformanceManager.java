package com.hh.cache.process.server;


import org.apache.log4j.Logger;

/**
 * @author TruongNX25
 */

public class PerformanceManager implements Runnable {
    private static final Logger log = Logger.getLogger(PerformanceManager.class);
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    @Override
    public void run() {
        while (true) {
            try {
                LRU();
                Thread.sleep(60000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Least recently used algo
    private static void LRU() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        log.info("Total memory: " + bytesToMegabytes(runtime.totalMemory()) + " MB");
        log.info("Used memory is megabytes: "
                + bytesToMegabytes(memory) + " MB");
        log.info("Used memory is bytes: " + memory + " bytes");
    }
}
