package com.example.edupal.repository;

import com.example.edupal.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findByQuestionId(String questionId);
    List<Question> findQuestionByStudentId(String studentId);

    @Query("SELECT COUNT(q) FROM Question q")
    int countAllQuestion();
}