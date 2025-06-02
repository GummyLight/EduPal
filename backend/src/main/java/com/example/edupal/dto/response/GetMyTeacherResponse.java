package com.example.edupal.dto.response;

import lombok.Data;

@Data
public class GetMyTeacherResponse {
    private TItem[] teachers;

    public GetMyTeacherResponse(TItem[] teachers) {
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