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

    @Column(name = "post_Id", nullable = false, length = 36)
    private String postId;

    @Column(name = "author_Id", nullable = false, length = 255)
    private String authorId;

    @Column(name = "author_Name", nullable = false, length = 255)
    private String authorName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publish_Time", nullable = false)
    private LocalDateTime publishTime;

    @Column(name = "attached_File_Name", length = 255)
    private String attachedFileName;

    @Column(name = "attached_File_Url", columnDefinition = "TEXT")
    private String attachedFileUrl;

    @Column(name = "created_At", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_At", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}