package com.example.edupal.service;

import com.example.edupal.common.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FtpFileServiceImpl implements FileService {

    @Autowired
    private FtpUtil ftpUtil;

    @Value("${ftp.uploadFilePath}")
    private String uploadDir;

    @Override
    public String upload(String dest, String name, MultipartFile file) throws IOException {
        File uploadFile = new File(dest, file.getOriginalFilename());
        file.transferTo(uploadFile);
        ftpUtil.uploadFile(uploadDir + name, uploadFile);
//        uploadFile.delete();
        return "Upload successful: " + name;
    }

    @Override
    public void download(String dest, String fileName, File outFile) throws IOException {
        ftpUtil.downloadFile(uploadDir + fileName, outFile);
    }

    @Override
    public void delete(String dest,String fileName) throws IOException {
        ftpUtil.deleteFile(uploadDir + dest + fileName);
    }
}