// PostCollection.java
package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "post_collection")
@Data
@IdClass(PostCollection.PostCollectionId.class)
public class PostCollection {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Id
    @Column(name = "post_id", length = 36)
    private String postId;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Data
    public static class PostCollectionId implements Serializable {
        private String userId;
        private String postId;
    }
}