package com.example.edupal.controller;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.AttachedFileDTO;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.dto.response.ReplyDTO;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 获取当前用户ID的辅助方法
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }
        return auth.getName();
    }

    // 1. 获取帖子列表 GET /community/posts
    @GetMapping("/posts")
    public List<PostDTO> getPosts() {
        return postService.getPosts();
    }

    // 2. 获取帖子详情 GET /community/posts/{postId}
    @GetMapping("/posts/{postId}")
    public PostDTO getPostDetail(@PathVariable String postId) {
        return postService.getPostDetail(postId);
    }

    // 3. 创建帖子 POST /community/posts
    @PostMapping("/posts")
    public PostDTO createPost(@RequestBody PostForm form) {
        return postService.createPost(form);
    }

    // 4. 删除帖子 DELETE /community/posts/{postId}
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
    }

    // 5. 创建回复 POST /community/posts/{postId}/replies
    @PostMapping("/posts/{postId}/replies")
    public ReplyDTO createReply(@PathVariable String postId, @RequestBody ReplyForm form) {
        return postService.createReply(postId, form);
    }

    // 6. 删除回复 DELETE /community/posts/{postId}/replies/{replyId}
    @DeleteMapping("/posts/{postId}/replies/{replyId}")
    public void deleteReply(@PathVariable String replyId) {
        postService.deleteReply(replyId);
    }

    // 7. 收藏帖子 PUT /community/posts/{postId}/collect
    @PutMapping("/posts/{postId}/collect")
    public void collectPost(@PathVariable String postId) {
        postService.toggleCollect(postId, true);
    }

    // 8. 取消收藏 DELETE /community/posts/{postId}/collect
    @DeleteMapping("/posts/{postId}/collect")
    public void uncollectPost(@PathVariable String postId) {
        postService.toggleCollect(postId, false);
    }

    // 9. 文件上传 POST /api/file/upload
    @PostMapping("/file/upload")
    public AttachedFileDTO uploadFile(@RequestParam("file") MultipartFile file) {
        return postService.uploadFile(file);
    }
}