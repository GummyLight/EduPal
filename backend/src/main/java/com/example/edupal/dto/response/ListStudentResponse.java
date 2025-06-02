package com.example.edupal.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ListStudentResponse {
    private String status;          // 响应状态
    private String message;         // 响应消息
    private Integer studentNum;       // 学生总数
    private List<StudentDetail> students; // 学生集合

    public ListStudentResponse(String status, String message,Integer studentNum, List<StudentDetail> students) {
        this.status = status;
        this.message = message;
        this.studentNum = studentNum;
        this.students = students;
    }

    @Data
    public static class StudentDetail {
        private String studentId;      // 学生ID
        private String studentName;    // 学生姓名
        private Integer studentGender;  // 学生性别
        private String studentClass;   // 学生班级

        public StudentDetail(String studentId, String studentName, Integer studentGender, String studentClass) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentGender = studentGender;
            this.studentClass = studentClass;
        }
    }
}