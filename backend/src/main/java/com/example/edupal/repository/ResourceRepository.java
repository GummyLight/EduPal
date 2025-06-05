package com.example.edupal.repository;

import com.example.edupal.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    // 按 name 模糊查询
    List<Resource> findByNameContaining(String name);

    // 查找 resource_id 的最大值
    @Query("SELECT MAX(r.resource_id)+1 FROM Resource r")
    Integer findMaxResourceId();

    @Query("SELECT COUNT(r) FROM Resource r")
    int countResource();
}
