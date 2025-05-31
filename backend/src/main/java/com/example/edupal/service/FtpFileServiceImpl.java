package com.example.edupal.service;

import com.example.edupal.common.FtpUtil;
import com.example.edupal.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FtpFileServiceImpl implements FileService {

    @Autowired
    private FtpUtil ftpUtil;

    @Value("${ftp.uploadFilePath}")
    private String uploadDir;

    @Override
    public Result upload(String dest, String name, MultipartFile file) throws IOException {
        File uploadFile = new File(dest, file.getOriginalFilename());
        file.transferTo(uploadFile);
        boolean success = ftpUtil.uploadFile(uploadDir + name, uploadFile);
        if (success) {
            return new Result(true, "Upload successful: " + name);
        } else {
            return new Result(false, "Upload failed: " + name);
        }
    }

    @Override
    public Result download(String dest, String fileName, File outFile) throws IOException {
        boolean success = ftpUtil.downloadFile(uploadDir + fileName, outFile);
        if (success) {
            return new Result(true, "Download successful: " + fileName);
        } else {
            return new Result(false, "Download failed: " + fileName);
        }
    }

    @Override
    public Result delete(String dest, String fileName) throws IOException {
        boolean success = ftpUtil.deleteFile(uploadDir + dest + fileName);
        if (success) {
            return new Result(true, "Delete successful: " + fileName);
        } else {
            return new Result(false, "Delete failed: " + fileName);
        }
    }

//    @Override
//    public void previewFile(String filename, HttpServletResponse response) throws IOException {
//        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
//        File file = new File("d:/Projects/practice/test-springboot/src/main/resources/file/" + filename);
//
//        if (!file.exists()) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//
//        try (ServletOutputStream ros = response.getOutputStream();
//             FileInputStream fis = new FileInputStream(file)) {
//            byte[] buffer = new byte[4 * 1024];
//            int bytesRead;
//            while ((bytesRead = fis.read(buffer)) != -1) {
//                ros.write(buffer, 0, bytesRead);
//            }
//            ros.flush();
//        }
//    }

//    @Override
//    public Result previewFile(String filename, HttpServletResponse response) throws IOException {
//        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
//        File file = new File(uploadDir+filename);
//
//        if (!file.exists()) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return new Result(false, "File not found: " + filename);
//        }
//
//        try (ServletOutputStream ros = response.getOutputStream();
//             FileInputStream fis = new FileInputStream(file)) {
//            byte[] buffer = new byte[4 * 1024];
//            int bytesRead;
//            while ((bytesRead = fis.read(buffer)) != -1) {
//                ros.write(buffer, 0, bytesRead);
//            }
//            ros.flush();
//            return new Result(true, "Preview successful: " + filename);
//        } catch (IOException e) {
//            return new Result(false, "Preview failed: " + filename);
//        }
//    }
//    @Override
//    public Result previewFile(String filename, HttpServletResponse response) throws IOException {
//        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
//
//        try (ServletOutputStream ros = response.getOutputStream()) {
//            boolean success = ftpUtil.downloadFile(uploadDir + filename, ros);
//            if (success) {
//                ros.flush();
//                return new Result(true, "Preview successful: " + filename);
//            } else {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                return new Result(false, "File not found or preview failed: " + filename);
//            }
//        } catch (IOException e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            return new Result(false, "Preview failed: " + filename);
//        }
//    }

    @Override
    public StreamingResponseBody previewFile(String filename) throws IOException {
        return outputStream -> {
            boolean success = ftpUtil.downloadFile(uploadDir + filename, outputStream);
            if (!success) {
                throw new IOException("Failed to preview file from remote server: " + filename);
            }
        };
    }
}