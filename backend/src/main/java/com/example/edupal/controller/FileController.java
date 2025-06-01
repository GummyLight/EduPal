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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    // 上传文件
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("path") String path,
                                    @RequestParam("toPath") String toPath,
                                    @RequestParam("fileId") String fileId) throws IOException {
        // 使用 fileId 重命名文件
        String newFileName = fileId + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Result result = fileService.upload(path, toPath,newFileName, file);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    // 下载文件
    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("fileName") String fileName,
                                      @RequestParam("path") String path,
                                      @RequestParam("fileId") String fileId,
                                      @RequestParam("outFile") String outFile) throws IOException {
        // 根据 fileId 找到文件
        String serverFileName = fileId + fileName.substring(fileName.lastIndexOf("."));
        Result result = fileService.download(path, serverFileName, new File(outFile+fileName));
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            File fileToDelete = new File(outFile + fileName);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @GetMapping("/previewFile")
    public StreamingResponseBody previewFile(@RequestParam("fileId") String fileId,
                                             @RequestParam("path") String path) throws IOException {
    return fileService.previewFile(path+fileId);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("fileId") String fileId,
                                    @RequestParam("path") String path) throws IOException {
        Result result = fileService.delete(path, fileId);
        if (result.isSuccess()) {
            File file = new File(path, fileId);
            if (!file.exists() || file.delete()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
            }
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }
}