package com.example.edupal.common;

public class Result {


    private boolean success;
    private String message;

    // 构造函数
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // getter 和 setter 方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}