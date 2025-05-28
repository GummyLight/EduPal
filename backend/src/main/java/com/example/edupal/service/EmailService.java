package com.example.edupal.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationCodeToEmail(String email, String code) throws MessagingException;
}