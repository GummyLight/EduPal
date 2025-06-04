package com.example.edupal.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplyDTO {
    private String id;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime publishTime;
    private String attachedFileUrl;
}