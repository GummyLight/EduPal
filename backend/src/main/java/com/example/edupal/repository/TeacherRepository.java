package com.example.edupal.repository;

import com.example.edupal.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {

    Teacher findByTeacherId(String teacherId);

    List<Teacher> findByTeacherName(String teacherName);

    @Query("SELECT t FROM Teacher t WHERE t.class1 = :teacherClass OR t.class2 = :teacherClass")
    List<Teacher> findByClass(String teacherClass);

    @Query("SELECT t.class1, t.class2 FROM Teacher t WHERE t.teacherId = :userId")
    List<String> findClassesByTeacherId(String userId);
}
