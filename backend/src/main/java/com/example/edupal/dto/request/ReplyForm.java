package com.example.edupal.dto.request;

import lombok.Data;

/**
 * 用于创建帖子的回复表单对象
 */
@Data
public class ReplyForm {
    private String content;
    // 附件通过 MultipartFile 上传，不在这里声明
}
