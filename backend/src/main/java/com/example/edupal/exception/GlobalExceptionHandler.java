package com.example.edupal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.edupal.common.ApiResponse;

/**
 * 全局异常处理器，用于捕获和处理后端抛出的异常，并返回统一的 JSON 格式响应。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常，并返回统一的 JSON 格式响应。
     * @param ex 异常对象
     * @return ResponseEntity，包含状态码和统一响应模块
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse<>(500, "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}