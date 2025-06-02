package com.example.edupal.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceResponse {
    private String resourceId;
    private String subject;
    private String teacherId;
    private String resourceContent;
    private String classId;

    private String name;
    private LocalDateTime uploadTime;
    private String description;

    public ResourceResponse() {}

    public ResourceResponse(String resourceId, String subject, String teacherId, String resourceContent,
                            String classId, String name, LocalDateTime uploadTime, String description) {
        this.resourceId = resourceId;
        this.subject = subject;
        this.teacherId = teacherId;
        this.resourceContent = resourceContent;
        this.classId = classId;
        this.name = name;
        this.uploadTime = uploadTime;
        this.description = description;
    }
}
