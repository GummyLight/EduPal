package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class PostForm {
    private String student_id;
    private String title;
    private String content;
    private String attachedFileUrl;

}