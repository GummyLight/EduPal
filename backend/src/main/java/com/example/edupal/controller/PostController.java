package com.example.edupal.controller;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    private String getCurrentUserId() {
        return "user123"; // 实际项目应从安全上下文获取
    }

    @PostMapping("/post")
    public PostDTO createPost(@RequestBody PostForm form) {
        return postService.createPost(getCurrentUserId(), form);
    }

    @PostMapping("/reply")
    public void reply(@RequestBody ReplyForm form) {
        postService.replyToPost(getCurrentUserId(), form);
    }

    @GetMapping("/posts")
    public List<PostDTO> listPosts() {
        return postService.listAllPosts();
    }

    @PostMapping("/collect/{postId}")
    public void collect(@PathVariable String postId) {
        postService.collectPost(getCurrentUserId(), postId);
    }

    @DeleteMapping("/collect/{postId}")
    public void uncollect(@PathVariable String postId) {
        postService.uncollectPost(getCurrentUserId(), postId);
    }
}