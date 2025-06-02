package com.example.edupal.repository;

import com.example.edupal.model.TeachingContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachingContentRepository extends JpaRepository<TeachingContent, String> {
    // 按 name 模糊查询
    List<TeachingContent> findByNameContaining(String name);
}