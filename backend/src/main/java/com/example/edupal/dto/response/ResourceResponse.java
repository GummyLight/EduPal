package com.example.edupal.dto.response;

import lombok.Data;

@Data
public class ResourceResponse {
    private String resourceId;
    private String subject;
    private String teacherId;
    private String resourceContent;
    private String classId;

    public ResourceResponse() {}

    public ResourceResponse(String resourceId, String subject, String teacherId, String resourceContent, String classId) {
        this.resourceId = resourceId;
        this.subject = subject;
        this.teacherId = teacherId;
        this.resourceContent = resourceContent;
        this.classId = classId;
    }
}
