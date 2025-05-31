package com.example.edupal.service;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PostService {
    List<PostDTO> getPosts(String userId);
    PostDTO getPostDetail(String postId, String userId);
    PostDTO createPost(String userId, PostForm form);
    void deletePost(String postId, String userId);
    ReplyDTO createReply(String postId, String userId, ReplyForm form);
    void deleteReply(String postId, String replyId, String userId);
    void toggleCollect(String postId, String userId, boolean collect);
    AttachedFileDTO uploadFile(MultipartFile file, String uploaderId);
}