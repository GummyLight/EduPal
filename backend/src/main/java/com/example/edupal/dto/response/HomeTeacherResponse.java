package com.example.edupal.dto.response;

import lombok.Data;

import java.util.List;

@Data

public class HomeTeacherResponse {
    private String status;
    private String message;
    private String username;
    private String userType;
    private String userId;
    private int unfinishedQAs;
    private int uploadExercises;
    private int uploadResources;
    private List<String> classIds; // List to store class IDs
    private List<StudentDetails> topStudents; // List to store detailed top students
    private List<StudentDetails> bottomStudents; // List to store detailed bottom students

    public HomeTeacherResponse(String status, String message, String username, String userType, String userId,
                               int unfinishedQAs, int uploadExercises, int uploadResources,
                               List<String> classIds, List<StudentDetails> topStudents, List<StudentDetails> bottomStudents) {
        this.status = status;
        this.message = message;
        this.username = username;
        this.userType = userType;
        this.userId = userId;
        this.unfinishedQAs = unfinishedQAs;
        this.uploadExercises = uploadExercises;
        this.uploadResources = uploadResources;
        this.classIds = classIds;
        this.topStudents = topStudents;
        this.bottomStudents = bottomStudents;
    }

    @Data
    public static class StudentDetails {
        private String studentId;
        private String studentName;
        private double studentAverageScore;
        private double studentVariation;

        public StudentDetails(String studentId, String studentName, double studentAverageScore, double studentVariation) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentAverageScore = studentAverageScore;
            this.studentVariation = studentVariation;
        }
    }
}
