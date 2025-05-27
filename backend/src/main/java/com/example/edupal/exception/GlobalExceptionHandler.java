package com.example.edupal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.edupal.common.ApiResponse;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

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
    //调错神器，活爹！
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();

        if (ex instanceof InvocationTargetException) {
            Throwable targetException = ex.getCause();
            if (targetException instanceof RuntimeException) {
                body.put("code", 400);
                body.put("message", "Runtime exception: " + targetException.getMessage());
                // 可以根据需要返回不同的状态码
                // return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            } else if (targetException instanceof Error) {
                body.put("code", 500);
                body.put("message", "System error: " + ((Error) targetException).getMessage());
                // return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (targetException instanceof Exception) {
                body.put("code", 500);
                body.put("message", "Internal Server Error: " + targetException.getMessage());
                // return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                body.put("code", 500);
                body.put("message", "Internal Server Error");
                // return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body.put("code", 500);
            body.put("message", "Internal Server Error");
            // return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}