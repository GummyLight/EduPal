package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class TeachingContentRequest {
    private String title;
    private String description;
    private String subject;
    private String grade;
}