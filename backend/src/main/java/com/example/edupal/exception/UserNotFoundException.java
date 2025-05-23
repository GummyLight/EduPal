package com.example.edupal.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("用户未找到");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
