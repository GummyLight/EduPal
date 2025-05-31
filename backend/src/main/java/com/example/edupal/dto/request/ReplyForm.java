package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class ReplyForm {
    private String content;
    private String attachedFileUrl; // 完全匹配前端ReplyForm接口
}