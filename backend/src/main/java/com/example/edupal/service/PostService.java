package com.example.edupal.service;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PostService {
    List<PostDTO> getPosts(); // 移除userId参数
    PostDTO getPostDetail(String postId); // 移除userId参数
    PostDTO createPost(PostForm form); // 移除userId参数
    void deletePost(String postId); // 移除userId参数
    ReplyDTO createReply(String postId, ReplyForm form); // 移除userId参数
    void deleteReply(String replyId); // 移除postId和userId参数
    void toggleCollect(String postId, boolean collect); // 移除userId参数
    AttachedFileDTO uploadFile(MultipartFile file); // 移除uploaderId参数
}