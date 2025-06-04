package com.example.edupal.repository;

import com.example.edupal.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {
    List<Reply> findByPostIdOrderByPublishTimeAsc(String postId);
    List<Reply> findByAuthorId(String authorId);
    void deleteByPostId(String postId);
}