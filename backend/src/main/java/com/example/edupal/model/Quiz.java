package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "quiz")
public class Quiz {
    @Id
    @Column(name = "quiz_id", length = 8)
    private String quiz_id;

    @Column(name = "quiz_subject", length = 8, nullable = false)
    private String quiz_subject;

    @Lob
    @Column(name = "quiz_content")
    private String quiz_content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publish_time")
    private Date publish_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deadline")
    private Date deadline;
}