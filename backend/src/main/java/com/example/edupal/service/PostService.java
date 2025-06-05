package com.example.edupal.service;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PostService {
    List<PostDTO> getPosts();
    PostDTO getPostDetail(String postId);
    PostDTO createPost(PostForm form);
    void deletePost(String postId);
    ReplyDTO createReply(String postId, ReplyForm form);
    void deleteReply(String replyId);
    List<PostDTO> getCollectedPosts(String userId);
    void toggleCollect(String postId, boolean collect,String userId);
}