package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.TeachingContentRequest;
import com.example.edupal.model.TeachingContent;
import com.example.edupal.service.TeachingContentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/teaching-content")
@CrossOrigin
public class TeachingContentController {

    @Autowired
    private TeachingContentService teachingContentService;

    // 1. 创建教学内容
    @PostMapping("/create")
    public ResponseEntity<?> createTeachingContent(@RequestBody TeachingContentRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "无效的请求"));
        }

        TeachingContent teachingContent = new TeachingContent();
        teachingContent.setTeaching_content_id(request.getTeaching_content_id());
        teachingContent.setSubject(request.getSubject());
        teachingContent.setTeacher_id(request.getTeacher_id());
        teachingContent.setTeaching_content_content(request.getTeaching_content_content());
        teachingContent.setClass_id(request.getClass_id());
        teachingContent.setName(request.getName());
        teachingContent.setUpload_time(request.getUpload_time() != null ? request.getUpload_time() : LocalDateTime.now());
        teachingContent.setDescription(request.getDescription());

        Result result = teachingContentService.saveTeachingContent(teachingContent);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "系统内部错误"));
        }

        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容保存成功", result.getData()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    // 2. 查询所有教学内容
    @GetMapping("/find")
    public ResponseEntity<?> getAllTeachingContents() {
        List<TeachingContent> teachingContents = teachingContentService.getAllTeachingContents();

        if (teachingContents == null || teachingContents.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "暂无教学内容", teachingContents));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容列表获取成功", teachingContents));
        }
    }

    // 3. 删除教学内容
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTeachingContent(@PathVariable("id") String teachingContentId) {
        try {
            teachingContentService.deleteTeachingContent(teachingContentId);
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "删除失败：" + e.getMessage()));
        }
    }

    // 4. 按name模糊查找教学内容
    @GetMapping("/search")
    public ResponseEntity<?> searchTeachingContentsByName(@RequestParam("name") String name) {
        List<TeachingContent> teachingContents = teachingContentService.findTeachingContentsByName(name);

        if (teachingContents == null || teachingContents.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "未找到相关教学内容", teachingContents));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(200, "教学内容列表获取成功", teachingContents));
        }
    }
}