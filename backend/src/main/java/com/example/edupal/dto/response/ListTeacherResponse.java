package com.example.edupal.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ListTeacherResponse {
    private String status;          // 响应状态
    private String message;         // 响应消息
    private Integer teacherNum;       // 教师总数
    private List<TeacherDetail> teachers; // 教师集合

    public ListTeacherResponse(String status, String teacher,Integer teacherNum, List<TeacherDetail> teachers) {
        this.status = status;
        this.message = teacher;
        this.teacherNum = teacherNum;
        this.teachers = teachers;
    }

    @Data
    public static class TeacherDetail {
        private String teacherId;         // 教师ID
        private String teacherName;       // 教师姓名
        private String teachingSubject;   // 教授科目
        private String class1;            // 班级1
        private String class2;            // 班级2

        public TeacherDetail(String teacherId, String teacherName, String teachingSubject, String class1, String class2) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
            this.teachingSubject = teachingSubject;
            this.class1 = class1;
            this.class2 = class2;
        }
    }
}