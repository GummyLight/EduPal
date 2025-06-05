package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class PostRequest {
    private String authorId;
    private String title;
    private String content;
    private String attachedFileUrl;

}