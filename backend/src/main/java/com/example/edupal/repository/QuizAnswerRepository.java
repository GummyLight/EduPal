package com.example.edupal.repository;


import com.example.edupal.model.Quiz;
import com.example.edupal.model.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, String> {
    QuizAnswer findByStudentIdAndQuizId(String studentId, String quizId);
    Integer countByQuizId(String quizId);
    Integer countByQuizIdAndIsGraded(String quizId, Integer isGraded);
    void deleteByQuizId(String quizId);

    List<QuizAnswer> findAllByQuizId(String quizId);

    List<QuizAnswer> findAllByStudentId(String userId);
}
