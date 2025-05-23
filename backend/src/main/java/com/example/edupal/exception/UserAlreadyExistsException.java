package com.example.edupal.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("用户已存在");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
