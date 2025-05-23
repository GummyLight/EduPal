package com.example.edupal.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("密码错误");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
