package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class PostForm {
    private String title;
    private String content;
    private String attachedFileUrl; // 完全匹配前端PostForm接口
}