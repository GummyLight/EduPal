package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_file")
@Data
public class PostFile {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "originalName", nullable = false, length = 255)
    private String originalName;

    @Column(name = "storagePath", nullable = false, length = 512)
    private String storagePath;

    @Column(name = "fileUrl", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "uploaderId", length = 255)
    private String uploaderId;

    @Column(name = "fileSize", nullable = false)
    private Long fileSize;

    @Column(name = "fileType", length = 100)
    private String fileType;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}