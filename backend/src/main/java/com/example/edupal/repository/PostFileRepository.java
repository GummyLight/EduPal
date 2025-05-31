package com.example.edupal.repository;

import com.example.edupal.model.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, String> {

    List<PostFile> findByUploaderId(String uploaderId);

    List<PostFile> findByFileType(String fileType);
}