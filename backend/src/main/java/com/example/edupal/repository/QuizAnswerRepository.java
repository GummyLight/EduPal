package com.example.edupal.repository;


import com.example.edupal.model.Quiz;
import com.example.edupal.model.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, String> {
    QuizAnswer findByStudentIdAndQuizId(String studentId, String quizId);
    Integer countByQuizId(String quizId);
    Integer countByQuizIdAndIsGraded(String quizId, Integer isGraded);
    void deleteByQuizId(String quizId);

    List<QuizAnswer> findAllByQuizId(String quizId);

    List<QuizAnswer> findAllByStudentId(String userId);

    QuizAnswer findByAnswerId(String answerId);
    @Query("SELECT MAX(r.answerId)+1 FROM QuizAnswer r")
    Integer findMaxResourceId();
}
