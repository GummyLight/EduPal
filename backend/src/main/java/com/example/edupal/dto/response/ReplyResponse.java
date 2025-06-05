package com.example.edupal.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplyResponse {
    private String id;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime publishTime;
    private String attachedFileUrl;

    public ReplyResponse() {}

    public ReplyResponse(String id, String authorId, String authorName, String content, LocalDateTime publishTime, String attachedFileUrl) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.publishTime = publishTime;
        this.attachedFileUrl = attachedFileUrl;
    }
}