package com.example.edupal.service;

import com.example.edupal.dto.response.HomeStudentResponse;

public interface HomeService {
    HomeStudentResponse getStudentHomeData(String userId, Integer userType);
}