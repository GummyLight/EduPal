package com.example.edupal.dto.response;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class HomeStudentResponse {
    private String status;
    private String message;
    private String username;
    private String userType;
    private String userId;
    private int finishedLectures;
    private int finishedExercises;
    private String gradeLevel;
    private int answerNum;
    private List<String> todayTasks;
    private List<String> notifications;

    public HomeStudentResponse(String status, String message, String username, String userType, String userId, int finishedLectures, int finishedExercises,
                               String gradeLevel, int answerNum, List<String> todayTasks, List<String> notifications) {
        this.status= status;
        this.message= message;
        this.username = username;
        this.userType = userType;
        this.userId = userId;
        this.finishedLectures = finishedLectures;
        this.finishedExercises = finishedExercises;
        this.gradeLevel = gradeLevel;
        this.answerNum = answerNum;
        this.todayTasks = todayTasks;
        this.notifications = notifications;
    }
}