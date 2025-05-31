package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Data
public class Reply {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "postId", nullable = false, length = 36)
    private String postId;

    @Column(name = "authorId", nullable = false, length = 255)
    private String authorId;

    @Column(name = "authorName", nullable = false, length = 255)
    private String authorName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publishTime", nullable = false)
    private LocalDateTime publishTime;

    @Column(name = "attachedFileName", length = 255)
    private String attachedFileName;

    @Column(name = "attachedFileUrl", columnDefinition = "TEXT")
    private String attachedFileUrl;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}