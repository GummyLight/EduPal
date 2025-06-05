package com.example.edupal.repository;

import com.example.edupal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByStudentId(String studentId);

    List<Student> findByStudentClass(String studentClass);

    List<Student> findByStudentName(String studentName);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.studentClass =:class1 OR s.studentClass =:class2")
    Integer countByStudentClass(String class1, String class2);

    @Query("SELECT s FROM Student s WHERE s.studentClass =:class1 OR s.studentClass =:class2")
    List<Student> findByStudentClass(String class1, String class2);
}
