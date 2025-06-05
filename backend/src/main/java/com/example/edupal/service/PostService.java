package com.example.edupal.service;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;

import java.util.List;

public interface PostService {
    List<PostResponse> getPosts();
    PostResponse getPostDetail(String postId);
    PostResponse createPost(PostRequest form);
    void deletePost(String postId);
    ReplyResponse createReply(String postId, ReplyRequest form);
    void deleteReply(String replyId);
    List<PostResponse> getCollectedPosts(String userId);
    void toggleCollect(String postId, boolean collect,String userId);
}