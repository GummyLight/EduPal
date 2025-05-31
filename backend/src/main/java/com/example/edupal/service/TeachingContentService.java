package com.example.edupal.service;

import com.example.edupal.dto.request.TeachingContentRequest;
import com.example.edupal.dto.response.TeachingContentResponse;

import java.util.List;

public interface TeachingContentService {
    TeachingContentResponse addContent(TeachingContentRequest request);
    void deleteContent(Long id);
    List<TeachingContentResponse> getAllContent();
}