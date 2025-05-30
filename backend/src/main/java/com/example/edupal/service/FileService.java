package com.example.edupal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileService {
    String upload(String dest, String name, MultipartFile file) throws IOException;
    void download(String dest, String fileName, File outFile) throws IOException;
    void delete(String dest,String fileName) throws IOException;
}