package com.example.edupal.service;

import com.example.edupal.dto.response.HomeAdminResponse;
import com.example.edupal.dto.response.HomeStudentResponse;
import com.example.edupal.dto.response.HomeTeacherResponse;

public interface HomeService {
    HomeStudentResponse getStudentHomeData(String userId, Integer userType);
    HomeTeacherResponse getTeacherHomeData(String userId, Integer userType);
    HomeAdminResponse getAdminHomeData(String userId, Integer userType);
}