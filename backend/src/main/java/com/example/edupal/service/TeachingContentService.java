package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.model.TeachingContent;

import java.util.List;

public interface TeachingContentService {
    Result saveTeachingContent(TeachingContent teachingContent);
    List<TeachingContent> getAllTeachingContents();

    void deleteTeachingContent(String teachingContentId);

    // 新增：按名称模糊查找
    List<TeachingContent> findTeachingContentsByName(String name);
}