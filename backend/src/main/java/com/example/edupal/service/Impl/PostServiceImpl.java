package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.FileService;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final PostCollectionRepository postCollectionRepository;
    private final FileService fileService; // 使用FileService接口
    private final UserRepository userRepository;

    @Override
    public List<PostDTO> getPosts(String userId) {
        List<Post> posts = postRepository.findAllByOrderByPublishTimeDesc();
        return posts.stream().map(post -> convertToPostDTO(post, userId)).collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostDetail(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Reply> replies = replyRepository.findByPostIdOrderByPublishTimeAsc(postId);
        boolean isCollected = postCollectionRepository.existsByUserIdAndPostId(userId, postId);

        return convertToPostDTO(post, userId, replies);
    }

    @Override
    @Transactional
    public PostDTO createPost(String userId, PostForm form) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthorId(userId);
        post.setAuthorName(author.getUserName());
        post.setPublishTime(LocalDateTime.now());

        if (form.getAttachedFileUrl() != null) {
            post.setAttachedFileName(extractFileName(form.getAttachedFileUrl()));
            post.setAttachedFileUrl(form.getAttachedFileUrl());
        }

        Post savedPost = postRepository.save(post);
        return convertToPostDTO(savedPost, userId);
    }

    @Override
    @Transactional
    public void deletePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete the post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public ReplyDTO createReply(String postId, String userId, ReplyForm form) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reply reply = new Reply();
        reply.setId(UUID.randomUUID().toString());
        reply.setPostId(postId);
        reply.setAuthorId(userId);
        reply.setAuthorName(author.getUserName());
        reply.setContent(form.getContent());
        reply.setPublishTime(LocalDateTime.now());

        if (form.getAttachedFileUrl() != null) {
            reply.setAttachedFileName(extractFileName(form.getAttachedFileUrl()));
            reply.setAttachedFileUrl(form.getAttachedFileUrl());
        }

        Reply savedReply = replyRepository.save(reply);
        return convertToReplyDTO(savedReply);
    }

    @Override
    @Transactional
    public void deleteReply(String postId, String replyId, String userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete the reply");
        }

        replyRepository.delete(reply);
    }

    @Override
    @Transactional
    public void toggleCollect(String postId, String userId, boolean collect) {
        if (collect) {
            if (!postCollectionRepository.existsByUserIdAndPostId(userId, postId)) {
                PostCollection collection = new PostCollection();
                collection.setUserId(userId);
                collection.setPostId(postId);
                postCollectionRepository.save(collection);
            }
        } else {
            postCollectionRepository.deleteByUserIdAndPostId(userId, postId);
        }
    }

    @Override
    public AttachedFileDTO uploadFile(MultipartFile file, String uploaderId) {
        try {
            // 使用FTP服务上传文件
            String fileName = file.getOriginalFilename();
            String dest = "posts/" + uploaderId + "/"; // 按用户ID分目录存储

            // 调用FTP文件服务
            Result uploadResult = fileService.upload(dest, fileName, file);

            if (!uploadResult.isSuccess()) {
                throw new RuntimeException("File upload failed: " + uploadResult.getMessage());
            }

            // 构建文件访问URL (根据实际FTP配置调整)
            String fileUrl = "/api/file/download?filename=" + dest + fileName;

            return new AttachedFileDTO(fileName, fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    // 私有辅助方法
    private PostDTO convertToPostDTO(Post post, String userId) {
        return convertToPostDTO(post, userId, null);
    }

    private PostDTO convertToPostDTO(Post post, String userId, List<Reply> replies) {
        boolean isCollected = postCollectionRepository.existsByUserIdAndPostId(userId, post.getId());

        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorName(post.getAuthorName());
        dto.setPublishTime(post.getPublishTime());
        dto.setIsCollected(isCollected);

        if (post.getAttachedFileUrl() != null) {
            dto.setAttachedFile(new AttachedFileDTO(
                    post.getAttachedFileName(),
                    post.getAttachedFileUrl()
            ));
        }

        if (replies != null) {
            dto.setReplies(replies.stream()
                    .map(this::convertToReplyDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ReplyDTO convertToReplyDTO(Reply reply) {
        ReplyDTO dto = new ReplyDTO();
        dto.setId(reply.getId());
        dto.setAuthorId(reply.getAuthorId());
        dto.setAuthorName(reply.getAuthorName());
        dto.setContent(reply.getContent());
        dto.setPublishTime(reply.getPublishTime());

        if (reply.getAttachedFileUrl() != null) {
            dto.setAttachedFile(new AttachedFileDTO(
                    reply.getAttachedFileName(),
                    reply.getAttachedFileUrl()
            ));
        }

        return dto;
    }

    private String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}