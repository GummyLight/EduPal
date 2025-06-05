package com.example.edupal.dto.response;

import lombok.Data;

@Data
public class HomeAdminResponse {
    private String status;
    private String message;
    private int totalUsers; // 现有用户数量
    private int totalTeachers; // 现有教师用户数量
    private int totalStudents; // 现有学生用户数量
    private int todayLoggedInTeachers; // 今日登录的教师用户数量
    private int todayLoggedInStudents; // 今日登录的学生用户数量
    private int todayLoggedInUsers; // 今日登录的用户数量
    private int totalTeachingMaterials; // 平台提供的教学资料数量
    private int totalExercises; // 平台现存的练习题数量
    private int totalCommunityTopics; // 平台社区中的话题数量
    private int totalStudentQuestions; // 学生通过平台提出问题的数量

    public HomeAdminResponse(String status, String message, int totalUsers, int totalTeachers, int totalStudents,
                             int todayLoggedInTeachers, int todayLoggedInStudents, int todayLoggedInUsers,
                             int totalTeachingMaterials, int totalExercises, int totalCommunityTopics,
                             int totalStudentQuestions) {
        this.status = status;
        this.message = message;
        this.totalUsers = totalUsers;
        this.totalTeachers = totalTeachers;
        this.totalStudents = totalStudents;
        this.todayLoggedInTeachers = todayLoggedInTeachers;
        this.todayLoggedInStudents = todayLoggedInStudents;
        this.todayLoggedInUsers = todayLoggedInUsers;
        this.totalTeachingMaterials = totalTeachingMaterials;
        this.totalExercises = totalExercises;
        this.totalCommunityTopics = totalCommunityTopics;
        this.totalStudentQuestions = totalStudentQuestions;
    }

}
