package com.example.edupal.repository;

import com.example.edupal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {

    // 基本CRUD方法已由JpaRepository提供

    @Query("SELECT p FROM Post p ORDER BY p.publishTime DESC")
    List<Post> findAllByOrderByPublishTimeDesc();

    // 根据标题搜索（匹配您的数据库字段名）
    List<Post> findByTitleContainingIgnoreCase(String keyword);
}