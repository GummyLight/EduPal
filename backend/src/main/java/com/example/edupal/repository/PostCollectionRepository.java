package com.example.edupal.repository;

import com.example.edupal.model.PostCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostCollectionRepository extends JpaRepository<PostCollection, Void> {

    // 检查收藏关系是否存在（匹配您的post_collection表结构）
    boolean existsByUserIdAndPostId(String userId, String postId);

    // 删除收藏关系（高性能实现）
    @Transactional
    @Modifying
    @Query("DELETE FROM PostCollection pc WHERE pc.userId = ?1 AND pc.postId = ?2")
    void deleteByUserIdAndPostId(String userId, String postId);

    // 获取用户收藏的所有帖子ID
    @Query("SELECT pc.postId FROM PostCollection pc WHERE pc.userId = ?1")
    List<String> findPostIdsByUserId(String userId);
}