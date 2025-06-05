package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class ReplyForm {
    private String content;
    private String authorId;
    private String attachedFileUrl;
}