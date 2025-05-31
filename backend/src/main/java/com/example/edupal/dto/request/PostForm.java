package com.example.edupal.dto.request;

import lombok.Data;

/**
 * 用于创建或更新帖子的表单对象
 */

@Data
public class PostForm {
    private String title;

    private String content;
    private String attachedFileName;
    private String attachedFileUrl;
}