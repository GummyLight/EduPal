package com.example.edupal.repository;

import com.example.edupal.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    // 按 name 模糊查询
    List<Resource> findByNameContaining(String name);
}
