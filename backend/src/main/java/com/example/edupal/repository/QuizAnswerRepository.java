package com.example.edupal.repository;


import com.example.edupal.model.Quiz;
import com.example.edupal.model.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, String> {
    QuizAnswer findByStudentIdAndQuizId(String studentId, Integer quizId);
    Integer countByQuizId(Integer quizId);
    Integer countByQuizIdAndIsGraded(Integer quizId, Integer isGraded);
    void deleteByQuizId(Integer quizId);

    List<QuizAnswer> findAllByQuizId(Integer quizId);

    List<QuizAnswer> findAllByStudentId(String userId);

    QuizAnswer findByAnswerId(Integer answerId);
    @Query("SELECT MAX(r.answerId)+1 FROM QuizAnswer r")
    Integer findMaxResourceId();
}
