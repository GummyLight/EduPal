package com.example.edupal.service.Impl;

import com.example.edupal.common.FtpUtil;
import com.example.edupal.common.Result;
import com.example.edupal.service.FileService;
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
    public Result upload(String dest, String toPath,String name, MultipartFile file) throws IOException {
        File uploadFile = new File(dest, file.getOriginalFilename());
        file.transferTo(uploadFile);
        boolean success = ftpUtil.uploadFile(uploadDir +toPath+ name, uploadFile);
        if (success) {
            return new Result(true, "上传成功: " + name);
        } else {
            return new Result(false, "上传失败: " + name);
        }
    }

    @Override
    public Result download(String dest, String fileName, File outFile) throws IOException {
        boolean success = ftpUtil.downloadFile(uploadDir +dest+ fileName, outFile);
        if (success) {
            return new Result(true, "下载成功: " + fileName);
        } else {
            return new Result(false, "下载失败: " + fileName);
        }
    }

    @Override
    public Result delete(String dest, String fileName) throws IOException {
        boolean success = ftpUtil.deleteFile(uploadDir + dest + fileName);
        if (success) {
            return new Result(true, "删除成功: " + fileName);
        } else {
            return new Result(false, "删除失败: " + fileName);
        }
    }

    @Override
    public StreamingResponseBody previewFile(String filename) throws IOException {
        return outputStream -> {
            boolean success = ftpUtil.downloadFile(uploadDir + filename, outputStream);
            if (!success) {
                throw new IOException("预览失败: " + filename);
            }
        };
    }
}