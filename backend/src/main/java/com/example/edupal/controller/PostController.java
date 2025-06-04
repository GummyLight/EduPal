package com.example.edupal.controller;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.AttachedFileDTO;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.dto.response.ReplyDTO;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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
    public ResponseEntity<?> deletePost(@PathVariable String postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "message", "帖子删除成功",
                    "postId", postId,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }

    // 5. 创建回复 POST /community/posts/{postId}/replies
    @PostMapping("/posts/{postId}/replies")
    public ReplyDTO createReply(@PathVariable String postId, @RequestBody ReplyForm form) {
        return postService.createReply(postId, form);
    }

    // 6. 删除回复 DELETE /community/posts/{postId}/replies/{replyId}
    @DeleteMapping("/posts/{postId}/replies/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable String replyId) {
        try {
            postService.deleteReply(replyId);
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "message", "回复删除成功",
                    "replyId", replyId,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "error", "回复不存在",
                            "timestamp", LocalDateTime.now()
                    ));
        }
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
}