package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.model.Resource;

import java.util.List;

public interface ResourceService {
    Result saveResource(Resource resource);
    List<Resource> getAllResources();
    void deleteResource(int resourceId);

    // 按名称模糊查找
    List<Resource> findResourcesByName(String name);

    // 新增：获取最大 resource_id
    Integer getMaxResourceId();
}
