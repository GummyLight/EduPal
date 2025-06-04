package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.FileService;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FileService fileService;
    private final UserRepository userRepository;

    // 获取当前登录用户ID的辅助方法
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<PostDTO> getPosts() {
        String userId = getCurrentUserId();
        List<Post> posts = postRepository.findAllByOrderByPublishTimeDesc();
        return posts.stream().map(post -> convertToPostDTO(post, userId)).collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostDetail(String postId) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Reply> replies = replyRepository.findByPostIdOrderByPublishTimeAsc(postId);
        return convertToPostDTO(post, userId, replies);
    }

    @Override
    @Transactional
    public PostDTO createPost(PostForm form) {
        String userId = getCurrentUserId();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthorId(userId);
        post.setAuthorName(author.getUserName());
        post.setPublishTime(LocalDateTime.now());


        return convertToPostDTO(postRepository.save(post), userId);
    }

    @Override
    @Transactional
    public void deletePost(String postId) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete the post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public ReplyDTO createReply(String postId, ReplyForm form) {
        String userId = getCurrentUserId();
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


        return convertToReplyDTO(replyRepository.save(reply));
    }

    @Override
    @Transactional
    public void deleteReply(String replyId) {
        String userId = getCurrentUserId();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete the reply");
        }

        replyRepository.delete(reply);
    }

    @Override
    @Transactional
    public void toggleCollect(String postId, boolean collect) {
        String userId = getCurrentUserId();
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
    public AttachedFileDTO uploadFile(MultipartFile file) {
        String uploaderId = getCurrentUserId();
        try {
            String fileName = file.getOriginalFilename();
            String dest = "posts/" + uploaderId + "/";
            Result uploadResult = fileService.upload(dest, "",fileName, file); //lcj：这个方法大概率废了

            if (!uploadResult.isSuccess()) {
                throw new RuntimeException("File upload failed: " + uploadResult.getMessage());
            }

            String fileUrl = "/api/file/download?filename=" + dest + fileName;
            return new AttachedFileDTO(fileName, fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    // 以下私有方法保持不变...
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