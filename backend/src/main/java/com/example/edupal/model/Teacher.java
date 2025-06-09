package com.example.edupal.model;

import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teacher")
public class Teacher {
    @Id
    @Column(name="teacher_id",length = 10)
    private String teacherId;

    @Column(name="teacher_name",length = 10)
    private String teacherName;

    @Column(name="teaching_subject",length = 10)
    private String teachingSubject;

    @Column(name="class1",length = 4)
    private String class1;

    @Column(name="class2",length = 4)
    private String class2;
}