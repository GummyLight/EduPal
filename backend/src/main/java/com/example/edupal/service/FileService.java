package com.example.edupal.service;

import com.example.edupal.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;

public interface FileService {
    Result upload(String dest, String toPath,String name, MultipartFile file) throws IOException;
    Result download(String dest, String fileName, File outFile) throws IOException;
    Result delete(String dest,String fileName) throws IOException;
//    Result previewFile(String filename, HttpServletResponse response) throws IOException;
    StreamingResponseBody previewFile(String filename) throws IOException;
}