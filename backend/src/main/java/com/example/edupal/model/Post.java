package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "post")
@Data
public class Post {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 统一使用蛇形命名
    @Column(name = "author_id", nullable = false, length = 255)
    private String authorId;

    @Column(name = "author_name", nullable = false, length = 255)
    private String authorName;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "attached_file_name", length = 255)
    private String attachedFileName;

    @Column(name = "attached_file_url", columnDefinition = "TEXT")
    private String attachedFileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}