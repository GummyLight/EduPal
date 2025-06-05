package com.example.edupal.repository;

import com.example.edupal.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, String> {
    // 根据用户ID查询测验列表

    @Query("SELECT q FROM Quiz q WHERE q.class1 = :classId OR q.class2 = :classId")
    List<Quiz> findByClassId(String classId);

    List<Quiz> findByTeacherId(String teacherId);

    @Query("SELECT MAX(q.quiz_id)+1 FROM Quiz q")
    Integer findMaxQuizId();

    @Query("SELECT q FROM Quiz q WHERE q.quiz_id = :quizId")
    Quiz findByQuizId(Integer quizId);
}
