package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.model.Resource;
import com.example.edupal.repository.ResourceRepository;
import com.example.edupal.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Result saveResource(Resource resource) {
        try {
            Resource saved = resourceRepository.save(resource);
            return new Result(true, "保存成功", saved);
        } catch (Exception e) {
            return new Result(false, "保存失败：" + e.getMessage());
        }
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public void deleteResource(String resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}
