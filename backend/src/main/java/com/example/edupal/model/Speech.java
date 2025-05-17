package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
public class Speech {
    @Id
    @Column(length = 12)
    private String speech_id;

    @Column(length = 10)
    private String user_id;

    @Column(length = 10, nullable = false)
    private String speaker;

    @Column(nullable = false)
    private Integer speech_type;

    @Lob
    private String speech_content;

    @Column(length = 12)
    private String target_speech;

    // Getters and Setters
    public String getSpeech_id() {
        return speech_id;
    }

    public void setSpeech_id(String speech_id) {
        this.speech_id = speech_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public Integer getSpeech_type() {
        return speech_type;
    }

    public void setSpeech_type(Integer speech_type) {
        this.speech_type = speech_type;
    }

    public String getSpeech_content() {
        return speech_content;
    }

    public void setSpeech_content(String speech_content) {
        this.speech_content = speech_content;
    }

    public String getTarget_speech() {
        return target_speech;
    }

    public void setTarget_speech(String target_speech) {
        this.target_speech = target_speech;
    }
}