package com.example.edupal.controller;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.model.User;
import com.example.edupal.repository.UserRepository;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    private UserRepository userRepository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 直接调用返回User的方法
        User user = userRepository.findByUserEmail(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getUserId();
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