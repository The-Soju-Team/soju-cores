/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.cache.process.server;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author HienDM
 */
public class CommitDiskThread extends Thread {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommitDiskThread.class.getSimpleName());
    
    public static FileOutputStream cacheWriter;
    public static ConcurrentLinkedQueue<byte[]> content = new ConcurrentLinkedQueue();
    
    @Override
    public void run() {
        while (true) {
            try {
                if(cacheWriter != null) {
                    byte[] data = null;
                    while (!content.isEmpty()) {
                        data = content.poll();
                        if(data != null) cacheWriter.write(data);                
                    }
                    if(data != null) {
                        cacheWriter.flush();
                        log.debug("write data to disk");
                    }
                }
                LoadCacheProcess.compressCache();
                Thread.sleep(1000l);
            } catch(Exception ex) {
                log.error("Error when commit to disk", ex);
            }              
        }
    }
    
    private static byte[] reverseBit(byte[] input) {
        for(int i = 0; i < input.length; i++) {
            input[i] = (byte)~input[i];
        }
        return input;
    } 
    
    public static void append(String json) {
        log.debug("COMMAND: " + json);
        byte[] bytes = (json + "\n").getBytes();
        int length = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] lengthArr = buffer.putInt(length).array();
        byte[] data = new byte[length + 4];
        System.arraycopy(lengthArr, 0, data, 0, 4); // add do dai vao 4 byte dau
        System.arraycopy(bytes, 0, data, 4, length); // add du lieu Object
        content.offer(data);
    }
}
