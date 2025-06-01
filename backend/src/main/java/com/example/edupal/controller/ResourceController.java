package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.ResourceRequest;
import com.example.edupal.model.Resource;
import com.example.edupal.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/resource")
@CrossOrigin
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // 1. 创建资源
    @PostMapping("/create")
    public ResponseEntity<?> createResource(@RequestBody ResourceRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "无效的请求"));
        }

        Resource resource = new Resource();
        resource.setResource_id(request.getResource_id());
        resource.setSubject(request.getSubject());
        resource.setTeacher_id(request.getTeacher_id());
        resource.setResource_content(request.getResource_content());
        resource.setClass_id(request.getClass_id());
        resource.setName(request.getName());
        resource.setUpload_time(request.getUpload_time() != null ? request.getUpload_time() : LocalDateTime.now());
        resource.setDescription(request.getDescription());

        Result result = resourceService.saveResource(resource);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "系统内部错误"));
        }

        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "资源保存成功", result.getData()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    // 2. 查询所有资源
    @GetMapping("/find")
    public ResponseEntity<?> getAllResources() {
        List<Resource> resources = resourceService.getAllResources();

        if (resources == null || resources.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "暂无资源", resources));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(200, "资源列表获取成功", resources));
        }
    }

    // 3. 删除资源
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable("id") String resourceId) {
        try {
            resourceService.deleteResource(resourceId);
            return ResponseEntity.ok(new ApiResponse<>(200, "资源删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "删除失败：" + e.getMessage()));
        }
    }

    // 4. 按name模糊查找资源
    @GetMapping("/search")
    public ResponseEntity<?> searchResourcesByName(@RequestParam("name") String name) {
        List<Resource> resources = resourceService.findResourcesByName(name);

        if (resources == null || resources.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "未找到相关资源", resources));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(200, "资源列表获取成功", resources));
        }
    }
}
