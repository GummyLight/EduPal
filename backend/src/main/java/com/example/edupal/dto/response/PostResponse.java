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
    public PostResponse() {}
    public PostResponse(String id, String title, String content, String authorId, String authorName,
                        LocalDateTime publishTime, Boolean isCollected, String attachedFileUrl,
                        List<ReplyResponse> replies) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.publishTime = publishTime;
        this.isCollected = isCollected;
        this.attachedFileUrl = attachedFileUrl;
        this.replies = replies;
    }
}