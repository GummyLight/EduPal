package com.example.edupal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetMyTeacherResponse {
    private String status = "success";
    private String message = "获取教师列表成功";
    private Integer teacherNum;
    private List<TItem> teachers;

    public GetMyTeacherResponse(String status, String message,List<TItem> teachers) {
        this.status = status;
        this.message = message;
        this.teacherNum = teachers.size();
        this.teachers = teachers;
    }

    @Data
    public static class TItem {
        private String teacherId;
        private String teacherName;
        private String teacherSubject;

        public TItem(String teacherId, String teacherName, String teacherSubject) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
            this.teacherSubject = teacherSubject;
        }
    }
}