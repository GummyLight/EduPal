package com.example.edupal.common;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;

@Component
public class FtpUtil {

    private static final Logger log = LoggerFactory.getLogger(FtpUtil.class);

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.uploadFilePath}")
    private String uploadDir;

    @Value("${ftp.downloadFilePath}")
    private String downloadDir;

    public FTPClient connect() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        ftpClient.login(username, password);
        ftpClient.setControlKeepAliveTimeout(60); // 设置控制连接的保活超时时间
        ftpClient.enterLocalActiveMode(); // 设置主动模式
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    public void uploadFile(String remoteFilePath, File localFile) throws IOException {
        FTPClient ftpClient = null;
        FileInputStream input = null;
        try {
            ftpClient = connect();
            input = new FileInputStream(localFile);
            if (!ftpClient.storeFile(remoteFilePath, input)) {
                String reply = ftpClient.getReplyString();
                log.error("Failed to upload file. FTP server reply: {}", reply);
                throw new IOException("FTP error: " + reply);
            }
        } catch (IOException e) {
            log.error("An error occurred during file upload: {}", e.getMessage());
            throw e; // Rethrow the exception if needed
        } finally {
            if (input != null) {
                input.close();
            }
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }

    public void downloadFile(String remoteFilePath, File localFile) throws IOException {
        FTPClient ftpClient = null;
        FileOutputStream output = null;
        try {
            ftpClient = connect();
            output = new FileOutputStream(localFile);
            ftpClient.retrieveFile(remoteFilePath, output);
        } finally {
            if (output != null) {
                output.close();
            }
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }

    public void deleteFile(String remoteFilePath) throws IOException {
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (!ftpClient.deleteFile(remoteFilePath)) {
                String reply = ftpClient.getReplyString();
                log.error("Failed to delete file. FTP server reply: {}", reply);
                throw new IOException("FTP error: " + reply);
            }
        } catch (IOException e) {
            log.error("An error occurred during file deletion: {}", e.getMessage());
            throw e; // Rethrow the exception if needed
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }
}