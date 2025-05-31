package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "postcollect")
@Data
@IdClass(PostCollection.PostCollectionId.class)
public class PostCollection {
    @Id
    @Column(name = "userId", length = 255)
    private String userId;

    @Id
    @Column(name = "postId", length = 36)
    private String postId;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", insertable = false, updatable = false)
    private Post post;

    @Data
    public static class PostCollectionId implements Serializable {
        private String userId;
        private String postId;
    }
}