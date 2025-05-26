package com.example.edupal.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 结果类，用于封装操作结果和相关信息。
 */
@Setter
@Getter
public class Result {
    // Getter 和 Setter 方法
    private boolean success; // 操作是否成功
    private String message; // 操作结果的消息
    private Object data; // 操作结果的数据，可以是任意类型

    // 构造函数，仅包含成功标志和消息
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 构造函数，包含成功标志、消息和数据
    public Result(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

}