package com.example.edupal.repository;

import com.example.edupal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {

    @Query("SELECT p FROM Post p ORDER BY p.publishTime DESC")
    List<Post> findAllByOrderByPublishTimeDesc();

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% ORDER BY p.publishTime DESC")
    List<Post> findByTitleContaining(String keyword);

    @Query("SELECT p FROM Post p WHERE p.authorId = :userId ORDER BY p.publishTime DESC")
    List<Post> findByAuthorId(String userId);

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds ORDER BY p.publishTime DESC")
    List<Post> findByIds(@Param("postIds") List<String> postIds);

    @Query("SELECT COUNT(p) FROM Post p")
    int countPost();
}