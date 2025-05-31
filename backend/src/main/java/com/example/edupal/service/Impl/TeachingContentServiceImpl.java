package com.example.edupal.service.Impl;

import com.example.edupal.dto.request.TeachingContentRequest;
import com.example.edupal.dto.response.TeachingContentResponse;
import com.example.edupal.model.TeachingContent;
import com.example.edupal.repository.TeachingContentRepository;
import com.example.edupal.service.TeachingContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachingContentServiceImpl implements TeachingContentService {

    private final TeachingContentRepository repository;

    @Override
    public TeachingContentResponse addContent(TeachingContentRequest request) {
        TeachingContent content = TeachingContent.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .grade(request.getGrade())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        TeachingContent saved = repository.save(content);
        return mapToResponse(saved);
    }

    @Override
    public void deleteContent(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<TeachingContentResponse> getAllContent() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TeachingContentResponse mapToResponse(TeachingContent content) {
        return TeachingContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .subject(content.getSubject())
                .grade(content.getGrade())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }
}
