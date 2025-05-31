package com.example.edupal.service.Impl;

import com.example.edupal.dto.response.QuizResponse;
import com.example.edupal.model.Quiz;
import com.example.edupal.repository.QuizRepository;
import com.example.edupal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().map(quiz -> {
            QuizResponse res = new QuizResponse();
            res.setQuizId(quiz.getQuiz_id());
            res.setQuizSubject(quiz.getQuiz_subject());
            res.setQuizContent(quiz.getQuiz_content());
            res.setPublishTime(quiz.getPublish_time());
            res.setDeadline(quiz.getDeadline());
            return res;
        }).toList();
    }
}
