package com.example.edupal.controller;

import com.example.edupal.dto.request.TeachingContentRequest;
import com.example.edupal.dto.response.TeachingContentResponse;
import com.example.edupal.model.TeachingContent;
import com.example.edupal.service.TeachingContentService;
import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teaching-content")
@CrossOrigin
@RequiredArgsConstructor
public class TeachingContentController {

    private final TeachingContentService teachingContentService;

    // ✅ 1. 创建教学内容
    @PostMapping("/create")
    public ResponseEntity<?> createContent(@RequestBody TeachingContentRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "无效的请求"));
        }

        try {
            TeachingContentResponse response = teachingContentService.addContent(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容保存成功", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "系统错误：" + e.getMessage()));
        }
    }

    // ✅ 2. 查询所有教学内容
    @GetMapping("/find")
    public ResponseEntity<?> getAllContents() {
        List<TeachingContentResponse> contentList = teachingContentService.getAllContent();

        if (contentList == null || contentList.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "暂无教学内容", contentList));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容列表获取成功", contentList));
        }
    }

    // ✅ 3. 删除教学内容
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteContent(@RequestParam("id") Long id) {
        try {
            teachingContentService.deleteContent(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "删除失败：" + e.getMessage()));
        }
    }
}
