package com.example.edupal.service.Impl;

import com.example.edupal.dto.request.*;
import com.example.edupal.dto.response.*;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final PostCollectionRepository postCollectionRepository;
    private final UserRepository userRepository;

    // 获取当前登录用户ID的辅助方法
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<PostResponse> getPosts() {
        String userId = getCurrentUserId();
        List<Post> posts = postRepository.findAllByOrderByPublishTimeDesc();
        return posts.stream().map(post -> convertToPostDTO(post, userId)).collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostDetail(String postId) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Reply> replies = replyRepository.findByPostIdOrderByPublishTimeAsc(postId);
        return convertToPostDTO(post, userId, replies);
    }

    @Override
    @Transactional
    public PostResponse createPost(PostRequest form) {
        User author = userRepository.findById(form.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + form.getAuthorId()));
        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthorId(form.getAuthorId());
        post.setAuthorName(author.getUserName());
        post.setPublishTime(LocalDateTime.now());
        post.setAttachedFileUrl(form.getAttachedFileUrl()); // 直接设置URL

        return convertToPostDTO(postRepository.save(post), form.getAuthorId());
    }

    @Override
    @Transactional
    public void deletePost(String postId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        // 先删除所有相关的收藏记录
        postCollectionRepository.deleteByPostId(postId);
        
        // 删除所有相关的回复
        replyRepository.deleteByPostId(postId);
        
        // 最后删除帖子本身
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public ReplyResponse createReply(String postId, ReplyRequest form) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findById(form.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reply reply = new Reply();
        reply.setId(UUID.randomUUID().toString());
        reply.setPostId(postId);
        reply.setAuthorId(form.getAuthorId());
        reply.setAuthorName(author.getUserName());
        reply.setContent(form.getContent());
        reply.setPublishTime(LocalDateTime.now());
        reply.setAttachedFileUrl(form.getAttachedFileUrl()); // 直接设置URL

        return convertToReplyDTO(replyRepository.save(reply));
    }

    @Override
    @Transactional
    public void deleteReply(String replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        replyRepository.delete(reply);
    }

    @Override
    @Transactional
    public void toggleCollect(String postId, boolean collect,String userId) {
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
    public List<PostResponse> getCollectedPosts(String userId) {
        // 查询用户收藏的帖子ID列表
        List<String> collectedPostIds = postCollectionRepository.findPostIdsByUserId(userId);

        if (collectedPostIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据帖子ID列表查询帖子详情
        List<Post> posts = postRepository.findByIds(collectedPostIds);

        // 转换为DTO并标记为已收藏
        return posts.stream()
                .map(post -> convertToPostDTO(post, userId))
                .collect(Collectors.toList());
    }

    // 私有方法
    private PostResponse convertToPostDTO(Post post, String userId) {
        return convertToPostDTO(post, userId, null);
    }

    private PostResponse convertToPostDTO(Post post, String userId, List<Reply> replies) {
        boolean isCollected = postCollectionRepository.existsByUserIdAndPostId(userId, post.getId());

        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorName(post.getAuthorName());
        dto.setPublishTime(post.getPublishTime());
        dto.setIsCollected(isCollected);
        dto.setAttachedFileUrl(post.getAttachedFileUrl()); // 直接设置URL

        if (replies != null) {
            dto.setReplies(replies.stream()
                    .map(this::convertToReplyDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ReplyResponse convertToReplyDTO(Reply reply) {
        ReplyResponse dto = new ReplyResponse();
        dto.setId(reply.getId());
        dto.setAuthorId(reply.getAuthorId());
        dto.setAuthorName(reply.getAuthorName());
        dto.setContent(reply.getContent());
        dto.setPublishTime(reply.getPublishTime());
        dto.setAttachedFileUrl(reply.getAttachedFileUrl()); // 直接设置URL

        return dto;
    }
}
