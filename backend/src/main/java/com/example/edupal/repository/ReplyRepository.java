package com.example.edupal.repository;

import com.example.edupal.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {

    // 根据帖子ID获取所有回复（匹配您的post_reply表结构）
    List<Reply> findByPostIdOrderByPublishTimeAsc(String postId);

    // 统计帖子回复数
    long countByPostId(String postId);
}