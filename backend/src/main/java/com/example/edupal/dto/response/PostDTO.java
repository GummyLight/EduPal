package com.example.edupal.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子返回数据结构
 */
@Data
public class PostDTO {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private LocalDateTime publishTime;

    private Boolean isCollected = false; // 当前用户是否收藏了此帖子



    private List<ReplyDTO> replies;
}
