// Post.java
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

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false, length = 36)
    private String authorId;

    @Column(name = "author_name", nullable = false, length = 100)
    private String authorName;

    @Column(name = "publish_time", nullable = false)
    private LocalDateTime publishTime;

    @Column(name = "attached_file_name", length = 255)
    private String attachedFileName;

    @Column(name = "attached_file_url", columnDefinition = "TEXT")
    private String attachedFileUrl;
}