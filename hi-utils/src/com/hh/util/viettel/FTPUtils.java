package com.hh.util.viettel;

import org.apache.commons.net.ftp.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author truongnx25
 */

// I have rewrite this class because of VangCK
public class FTPUtils {

    private static final Logger LOG = Logger.getLogger(FTPUtils.class);
    public static final String SLASH = File.separator;
    private final int FTP_TIMEOUT = 60000;
    private final int BUFFER_SIZE = 1024 * 1024 * 1;
    private String FTP_SERVER_ADDRESS = "";
    private int FTP_SERVER_PORT_NUMBER = 21;
    private String FTP_USERNAME = "";
    private String FTP_PASSWORD = "";
    private FTPClient ftpClient;

    public String getFTP_SERVER_ADDRESS() {
        return FTP_SERVER_ADDRESS;
    }

    public void setFTP_SERVER_ADDRESS(String fTP_SERVER_ADDRESS) {
        FTP_SERVER_ADDRESS = fTP_SERVER_ADDRESS;
    }

    public int getFTP_SERVER_PORT_NUMBER() {
        return FTP_SERVER_PORT_NUMBER;
    }

    public void setFTP_SERVER_PORT_NUMBER(int fTP_SERVER_PORT_NUMBER) {
        FTP_SERVER_PORT_NUMBER = fTP_SERVER_PORT_NUMBER;
    }

    public String getFTP_USERNAME() {
        return FTP_USERNAME;
    }

    public void setFTP_USERNAME(String fTP_USERNAME) {
        FTP_USERNAME = fTP_USERNAME;
    }

    public String getFTP_PASSWORD() {
        return FTP_PASSWORD;
    }

    public void setFTP_PASSWORD(String fTP_PASSWORD) {
        FTP_PASSWORD = fTP_PASSWORD;
    }

    public void uploadFTPFile(String ftpFilePath, String filePath) {
        LOG.info(String.format("Starting to upload %s to %s on %s with %s and %s", filePath, ftpFilePath,
                FTP_SERVER_ADDRESS, FTP_USERNAME, FTP_PASSWORD));
        try {
            connectFTPServer();
            FileInputStream fis = new FileInputStream(filePath);

            boolean ftpStatus = ftpClient.deleteFile(ftpFilePath);
            LOG.info(String.format("Delete file %s on server status %s", filePath, ftpClient.getReplyCode()));
            if (ftpStatus) {
                LOG.info(String.format("It does look like that file %s has been uploaded to server before", filePath));
            } else {
                LOG.info(String.format("File %s has not been uploaded to server before", filePath));
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpStatus = ftpClient.storeFile(ftpFilePath, fis);
            LOG.info(String.format("File %s uploaded to folder %s on %s return code %s", filePath, ftpFilePath,
                    FTP_SERVER_ADDRESS, ftpClient.getReplyCode()));
            if (ftpStatus) {
                LOG.info(String.format("File %s uploaded to folder %s on %s successfully!", filePath, ftpFilePath,
                        FTP_SERVER_ADDRESS));
            } else {
                LOG.info(String.format("File %s uploaded to folder %s on %s UNsuccessfully!", filePath, ftpFilePath,
                        FTP_SERVER_ADDRESS));
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnectFTPServer();
        }

    }

    /**
     * @param remoteFilePath Source FTP File Path
     * @param localFilePath  Destination Path
     * @return true if file is downloaded or else
     */
    public boolean downloadFTPFile(String remoteFilePath, String localFilePath) {
        LOG.info("File " + remoteFilePath + " is being downloaded...");
        boolean success = false;
        connectFTPServer();
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(localFilePath));
        ) {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            LOG.info("REPLY CODE: " + ftpClient.getReplyCode());
            ftpClient.setBufferSize(BUFFER_SIZE);
            LOG.info("REPLY CODE: " + ftpClient.getReplyCode());
            LOG.info(String.format("Starting download file %s", remoteFilePath));
            success = ftpClient.retrieveFile(remoteFilePath, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (success) {
            LOG.info("File " + remoteFilePath + " has been downloaded successfully.");
        }
        disconnectFTPServer();
        return success;
    }

    public List<FTPFile> getListFileFromFTPServer(String path, final String ext) {
        List<FTPFile> listFiles = new ArrayList<FTPFile>();
        // connect ftp server
        connectFTPServer();
        try {
            // list file ends with "jar"
            FTPFile[] ftpFiles = ftpClient.listFiles(path, new FTPFileFilter() {
                public boolean accept(FTPFile file) {
                    return file.getName().endsWith(ext);
                }
            });
            if (ftpFiles.length > 0) {
                for (FTPFile ftpFile : ftpFiles) {
                    // add file to listFiles
                    if (ftpFile.isFile()) {
                        listFiles.add(ftpFile);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listFiles;
    }

    private void connectFTPServer() {
        ftpClient = new FTPClient();
        try {
            LOG.info(String.format("Connecting to ftp server: IP: %s, Port: %s", FTP_SERVER_ADDRESS, FTP_SERVER_PORT_NUMBER));
            // connect to ftp server
//            ftpClient.setDefaultTimeout(FTP_TIMEOUT);
            ftpClient.connect(FTP_SERVER_ADDRESS, FTP_SERVER_PORT_NUMBER);
            // run the passive mode command
            // check reply code
            ftpClient.setKeepAlive(true);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                disconnectFTPServer();
                throw new IOException("FTP server not respond!");
            } else {
                ftpClient.setSoTimeout(FTP_TIMEOUT);
                // login ftp server
                if (!ftpClient.login(FTP_USERNAME, FTP_PASSWORD)) {
                    throw new IOException("Username or password is incorrect!");
                }
                ftpClient.setDataTimeout(FTP_TIMEOUT);
                ftpClient.enterLocalPassiveMode();
                LOG.info("FTP Reply Code: " + ftpClient.getReplyCode());
                LOG.info(String.format("Connected to server %s", FTP_SERVER_ADDRESS));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void disconnectFTPServer() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}