package com.example.edupal.repository;

import com.example.edupal.model.PostCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface PostCollectionRepository extends JpaRepository<PostCollection, PostCollection.PostCollectionId> {

    boolean existsByUserIdAndPostId(String userId, String postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostCollection pc WHERE pc.userId = ?1 AND pc.postId = ?2")
    void deleteByUserIdAndPostId(String userId, String postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostCollection pc WHERE pc.postId = ?1")
    void deleteByPostId(String postId);

    @Query("SELECT pc.postId FROM PostCollection pc WHERE pc.userId = :userId")
    List<String> findPostIdsByUserId(@Param("userId") String userId);
    List<PostCollection> findByUserId(String userId);
}