package com.example.edupal.repository;

import com.example.edupal.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAnswersByQuestionId(String questionId);

    @Query("SELECT a FROM Answer a WHERE a.questionId = :questionId AND a.answerType = 0")
    Answer findAiAnswersByQuestionId(String questionId);

    @Query("SELECT a FROM Answer a WHERE a.questionId = :questionId AND a.answerType = 1")
    List<Answer> findTeacherAnswersByQuestionId(String questionId);
}