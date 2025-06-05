package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TeachingContentRequest {

    private String teaching_content_id;
    private String subject;
    private String teacher_id;
    private String teaching_content_content;
    private String class_id;

    private String name;
    private LocalDateTime upload_time;
    private String description;

    public TeachingContentRequest() {}

    public TeachingContentRequest(String teaching_content_id, String subject, String teacher_id, String teaching_content_content, String class_id,
                                  String name, LocalDateTime upload_time, String description) {
        this.teaching_content_id = teaching_content_id;
        this.subject = subject;
        this.teacher_id = teacher_id;
        this.teaching_content_content = teaching_content_content;
        this.class_id = class_id;
        this.name = name;
        this.upload_time = upload_time;
        this.description= description;
    }
}