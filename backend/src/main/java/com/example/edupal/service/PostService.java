package com.example.edupal.service;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.dto.response.ReplyDTO;
import java.util.List;

public interface PostService {
    PostDTO createPost(String userId, PostForm form);
    void replyToPost(String userId, ReplyForm form);
    List<PostDTO> listAllPosts();
    void collectPost(String userId, String postId);
    void uncollectPost(String userId, String postId);
}