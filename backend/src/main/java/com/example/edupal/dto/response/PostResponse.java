package com.example.edupal.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private LocalDateTime publishTime;
    private Boolean isCollected;
    private String attachedFileUrl;
    private List<ReplyResponse> replies;
}