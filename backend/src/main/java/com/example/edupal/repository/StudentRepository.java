package com.example.edupal.repository;

import com.example.edupal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByStudentId(String studentId);

    List<Student> findByStudentClass(String studentClass);

    List<Student> findByStudentName(String studentName);
}
