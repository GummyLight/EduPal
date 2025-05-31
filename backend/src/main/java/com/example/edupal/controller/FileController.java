package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
//    @PostMapping("/upload")
//    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
//        return fileService.upload(path, file.getOriginalFilename(), file);
//    }
//
//    @GetMapping("/download")
//    public void download(@RequestParam("fileName") String fileName, @RequestParam("path") String path,
//                         @RequestParam("outFile") String outFile) throws IOException {
//        File downloadFile = new File(outFile);
//        fileService.download(path, fileName, downloadFile);
//    }
//
//
//    @GetMapping("/previewFile")
//    public void previewFile(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
//        fileService.previewFile(filename, response);
//    }
//
//    @PostMapping("/delete")
//    public String delete(@RequestParam("fileName") String fileName, @RequestParam("path") String path) throws IOException {
//        fileService.delete(path,fileName);
//        File file = new File(path, fileName);
//        if (file.exists() && file.delete()) {
//            return "File deleted successfully: " + fileName;
//        } else {
//            return "Failed to delete file: " + fileName;
//        }
//    }
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
        Result result = fileService.upload(path, file.getOriginalFilename(), file);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("fileName") String fileName, @RequestParam("path") String path,
                                      @RequestParam("outFile") String outFile) throws IOException {
        File downloadFile = new File(outFile);
        Result result = fileService.download(path, fileName, downloadFile);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @GetMapping("/previewFile")
    public ResponseEntity<?> previewFile(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
        Result result = fileService.previewFile(filename, response);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("fileName") String fileName, @RequestParam("path") String path) throws IOException {
        Result result = fileService.delete(path, fileName);
        if (result.isSuccess()) {
            File file = new File(path, fileName);
            if (!file.exists() || file.delete()) {
                return ResponseEntity.ok(new ApiResponse<>(200, "File deleted successfully: " + fileName));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Failed to delete file: " + fileName));
            }
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }
}