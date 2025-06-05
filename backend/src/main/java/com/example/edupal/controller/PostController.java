package com.example.edupal.controller;

import com.example.edupal.dto.request.PostRequest;
import com.example.edupal.dto.request.ReplyRequest;
import com.example.edupal.dto.response.PostResponse;
import com.example.edupal.dto.response.ReplyResponse;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }

    // 2. 获取帖子详情 GET /community/posts/{postId}
    @GetMapping("/posts/{postId}")
    public PostResponse getPostDetail(@PathVariable String postId) {
        return postService.getPostDetail(postId);
    }

    // 3. 创建帖子 POST /community/posts
    @PostMapping("/posts")
    public PostResponse createPost(@RequestBody PostRequest form) {
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
    public ReplyResponse createReply(@PathVariable String postId, @RequestBody ReplyRequest form) {
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
    @PutMapping("/posts/{postId}/collect/{userId}")
    public ResponseEntity<?> collectPost(
            @PathVariable String postId,
            @PathVariable String userId  // 必须添加@PathVariable注解
    ) {
        postService.toggleCollect(postId, true, userId);
        return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "帖子收藏成功"
        ));
    }

    @DeleteMapping("/posts/{postId}/collect/{userId}")
    public ResponseEntity<?> uncollectPost(
            @PathVariable String postId,
            @PathVariable String userId  // 添加注解
    ) {
        postService.toggleCollect(postId, false, userId);
        return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "取消收藏成功"
        ));
    }

    // 9. 获取用户收藏的帖子列表 GET /community/users/{userId}/collected-posts
    @GetMapping("/users/{userId}/collected-posts")
    public ResponseEntity<List<PostResponse>> getCollectedPosts(@PathVariable String userId) {
        List<PostResponse> posts = postService.getCollectedPosts(userId);
        return ResponseEntity.ok(posts);
    }


}