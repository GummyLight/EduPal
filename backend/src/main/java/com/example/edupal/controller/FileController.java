package com.example.edupal.controller;

import com.example.edupal.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    //file是文件，path是文件所在你机子上的目录
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
        return fileService.upload(path, file.getOriginalFilename(), file);
    }

    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName, @RequestParam("path") String path,
                         @RequestParam("outFile") String outFile) throws IOException {
        File downloadFile = new File(outFile);
        fileService.download(path, fileName, downloadFile);
    }


    @GetMapping("/previewFile")
    public void previewFile(@RequestParam("filename") String filename, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
        File file = new File("d:/Projects/practice/test-springboot/src/main/resources/file/" + filename);

        ServletOutputStream ros = response.getOutputStream();

        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[4 * 1024];
        int len = 0;
        while ((len = fis.read(bytes)) != -1) {
            ros.write(bytes, 0, len);
        }

        ros.flush();
        ros.close();
        fis.close();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("fileName") String fileName, @RequestParam("path") String path) throws IOException {
        fileService.delete(path,fileName);
        File file = new File(path, fileName);
        if (file.exists() && file.delete()) {
            return "File deleted successfully: " + fileName;
        } else {
            return "Failed to delete file: " + fileName;
        }
    }
}