package com.example.edupal.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeachingContentResponse {
    private String teachingContentId;
    private String subject;
    private String teacherId;
    private String teachingContentContent;
    private String classId;

    private String name;
    private LocalDateTime uploadTime;
    private String description;

    public TeachingContentResponse() {}

    public TeachingContentResponse(String teachingContentId, String subject, String teacherId, String teachingContentContent,
                                   String classId, String name, LocalDateTime uploadTime, String description) {
        this.teachingContentId = teachingContentId;
        this.subject = subject;
        this.teacherId = teacherId;
        this.teachingContentContent = teachingContentContent;
        this.classId = classId;
        this.name = name;
        this.uploadTime = uploadTime;
        this.description = description;
    }
}