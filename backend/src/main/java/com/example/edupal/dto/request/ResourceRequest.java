package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ResourceRequest {

    private String resource_id;
    private String subject;
    private String teacher_id;
    private String resource_content;
    private String class_id;

    private String name;
    private LocalDateTime upload_time;
    private String description;

    public ResourceRequest() {}

    public ResourceRequest(String resource_id, String subject, String teacher_id, String resource_content, String class_id,
                           String name, LocalDateTime upload_time, String description) {
        this.resource_id = resource_id;
        this.subject = subject;
        this.teacher_id = teacher_id;
        this.resource_content = resource_content;
        this.class_id = class_id;
        this.name = name;
        this.upload_time = upload_time;
        this.description = description;
    }
}
