package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.model.Resource;

import java.util.List;

public interface ResourceService {
    Result saveResource(Resource resource);
    List<Resource> getAllResources();
    void deleteResource(String resourceId);
}
