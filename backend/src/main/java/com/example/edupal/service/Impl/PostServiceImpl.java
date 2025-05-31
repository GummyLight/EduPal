package com.example.edupal.service.Impl;

import com.example.edupal.dto.request.PostForm;
import com.example.edupal.dto.request.ReplyForm;
import com.example.edupal.dto.response.PostDTO;
import com.example.edupal.dto.response.ReplyDTO;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final PostCollectionRepository collectionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PostDTO createPost(String userId, PostForm form) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthorId(userId);
        post.setAuthorName(author.getUserName());
        post.setPublishTime(LocalDateTime.now());
        post.setAttachedFileName(form.getAttachedFileName());
        post.setAttachedFileUrl(form.getAttachedFileUrl());

        Post savedPost = postRepository.save(post);
        return convertToPostDTO(savedPost, false); // 默认未收藏
    }

    @Override
    @Transactional
    public void replyToPost(String userId, ReplyForm form) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Reply reply = new Reply();
        reply.setId(UUID.randomUUID().toString());
        reply.setAuthorId(userId);
        reply.setAuthorName(author.getUserName());
        reply.setContent(form.getContent());
        reply.setPublishTime(LocalDateTime.now());
        // 需要补充postId设置（根据ReplyForm调整）
        replyRepository.save(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> listAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> convertToPostDTO(post, false)) // 需要实现收藏状态检查
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void collectPost(String userId, String postId) {
        if (!collectionRepository.existsByUserIdAndPostId(userId, postId)) {
            PostCollection collection = new PostCollection();
            collection.setUserId(userId);
            collection.setPostId(postId);
            collectionRepository.save(collection);
        }
    }

    @Override
    @Transactional
    public void uncollectPost(String userId, String postId) {
        collectionRepository.deleteByUserIdAndPostId(userId, postId);
    }

    // DTO转换方法
    private PostDTO convertToPostDTO(Post post, boolean isCollected) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorName(post.getAuthorName());
        dto.setPublishTime(post.getPublishTime());
        dto.setIsCollected(isCollected);
        // 需要补充replies的转换
        return dto;
    }

    private ReplyDTO convertToReplyDTO(Reply reply) {
        ReplyDTO dto = new ReplyDTO();
        dto.setId(reply.getId());
        dto.setAuthorId(reply.getAuthorId());
        dto.setAuthorName(reply.getAuthorName());
        dto.setContent(reply.getContent());
        dto.setPublishTime(reply.getPublishTime());
        return dto;
    }
}