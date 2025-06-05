package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class ReplyRequest {
    private String content;
    private String authorId;
    private String attachedFileUrl;
}