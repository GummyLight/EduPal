package com.example.edupal.service;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;

import java.util.List;

public interface PostService {
    List<PostDTO> getPosts();
    PostDTO getPostDetail(String postId);
    PostDTO createPost(PostRequest form);
    void deletePost(String postId);
    ReplyDTO createReply(String postId, ReplyRequest form);
    void deleteReply(String replyId);
    List<PostDTO> getCollectedPosts(String userId);
    void toggleCollect(String postId, boolean collect,String userId);
}