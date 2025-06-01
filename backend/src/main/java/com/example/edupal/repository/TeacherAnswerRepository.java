package com.example.edupal.repository;

import com.example.edupal.model.TeacherAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherAnswerRepository extends JpaRepository<TeacherAnswer, String> {
    List<TeacherAnswer> findByTeacherId(String teacherId);

    TeacherAnswer findByQuestionId(String questionId);
}
