package com.hh.util;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinuxUtils {
    private static final Logger LOG = Logger.getLogger(LinuxUtils.class);

    /**
     * @param command A linux command, Example: ls -lha
     * @return status code of that command
     */
    public static int executeShell(String command) {
        LOG.info("Preparing executing command: " + command);
        Runtime run = Runtime.getRuntime();
        try {
            LOG.info("Executing command: " + command);
            Process p = run.exec(command);
            p.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                LOG.info(line);
            }
            return p.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
