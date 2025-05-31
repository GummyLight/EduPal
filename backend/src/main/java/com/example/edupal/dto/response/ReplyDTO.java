package com.example.edupal.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子的回复返回数据结构
 */
@Data
public class ReplyDTO {
    private String id;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime publishTime;


}
