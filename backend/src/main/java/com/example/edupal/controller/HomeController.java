package com.example.edupal.controller;

import com.example.edupal.dto.response.HomeAdminResponse;
import com.example.edupal.dto.response.HomeStudentResponse;
import com.example.edupal.dto.response.HomeTeacherResponse;
import com.example.edupal.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/student")
    public ResponseEntity<HomeStudentResponse> getHomeData(@RequestParam("userId") String userId, @RequestParam("userType") Integer userType) {
        try {
            HomeStudentResponse homeData = homeService.getStudentHomeData(userId,userType);
            return ResponseEntity.ok(homeData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/teacher")
    public ResponseEntity<HomeTeacherResponse> getTeacherHomeData(@RequestParam("userId") String userId, @RequestParam("userType") Integer userType) {
        try {
            HomeTeacherResponse homeData = homeService.getTeacherHomeData(userId,userType);
            return ResponseEntity.ok(homeData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping ("/admin")
    public ResponseEntity<HomeAdminResponse> getAdminHomeData(@RequestParam("userId") String userId, @RequestParam("userType") Integer userType) {
        try {
            HomeAdminResponse homeData = homeService.getAdminHomeData(userId,userType);
            return ResponseEntity.ok(homeData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}