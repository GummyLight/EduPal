package com.example.edupal.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 统一响应模块，用于封装所有后端返回的数据格式。
 * 包含状态码、消息和数据三个部分，确保前后端对响应数据的理解是一致的。
 */
@Setter
@Getter
public class ApiResponse<T> {
    // Getter 和 Setter 方法
    private int code; // 状态码，用于表示请求的处理结果
    private String message; // 消息，用于返回给前端的提示信息
    private T data; // 数据，用于返回给前端的具体数据内容

    /**
     * 构造函数，用于创建 ApiResponse 实例时初始化状态码、消息和数据。
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数，用于创建 ApiResponse 实例时只初始化状态码和消息。
     * @param code 状态码
     * @param message 消息
     */
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

}