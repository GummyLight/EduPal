package com.example.edupal.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeachingContentResponse {
    private Long id;
    private String title;
    private String description;
    private String subject;
    private String grade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}