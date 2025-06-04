package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_collection")
@Data
@IdClass(PostCollection.PostCollectionId.class)
public class PostCollection {
    @Id
    @Column(name = "user_Id", length = 255)
    private String userId;

    @Id // 需要为复合主键的每个部分添加@Id注解
    @Column(name = "post_id",length=255) // 修正列名与数据库一致
    private String postId; // 需要添加postId字段，与PostCollectionId匹配

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_Id", insertable = false, updatable = false)
    private Post post;

    @Data
    public static class PostCollectionId implements Serializable {
        private String userId;
        private String postId;
    }
}