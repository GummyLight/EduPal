package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.model.TeachingContent;
import com.example.edupal.repository.TeachingContentRepository;
import com.example.edupal.service.TeachingContentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingContentServiceImpl implements TeachingContentService {

    @Autowired
    private TeachingContentRepository teachingContentRepository;

    @Override
    public Result saveTeachingContent(TeachingContent teachingContent) {
        try {
            TeachingContent saved = teachingContentRepository.save(teachingContent);
            return new Result(true, "保存成功", saved);
        } catch (Exception e) {
            return new Result(false, "保存失败：" + e.getMessage());
        }
    }

    @Override
    public List<TeachingContent> getAllTeachingContents() {
        return teachingContentRepository.findAll();
    }

    @Override
    public void deleteTeachingContent(String teachingContentId) {
        teachingContentRepository.deleteById(teachingContentId);
    }

    @Override
    public List<TeachingContent> findTeachingContentsByName(String name) {
        return teachingContentRepository.findByNameContaining(name);
    }
}