package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceRequest {

    private String resource_id;
    private String subject;
    private String teacher_id;
    private String resource_content;
    private String class_id;

    public ResourceRequest() {}

    public ResourceRequest(String resource_id, String subject, String teacher_id, String resource_content, String class_id) {
        this.resource_id = resource_id;
        this.subject = subject;
        this.teacher_id = teacher_id;
        this.resource_content = resource_content;
        this.class_id = class_id;
    }
}
