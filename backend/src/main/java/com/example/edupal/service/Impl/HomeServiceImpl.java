package com.example.edupal.service.Impl;

import com.example.edupal.dto.response.HomeStudentResponse;
import com.example.edupal.model.LearningProgress;
import com.example.edupal.model.Question;
import com.example.edupal.model.Student;
import com.example.edupal.repository.LearningProgressRepository;
import com.example.edupal.repository.QuestionRepository;
import com.example.edupal.repository.StudentRepository;
import com.example.edupal.repository.TeacherRepository;
import com.example.edupal.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    LearningProgressRepository learningProgressRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Override
    public HomeStudentResponse getStudentHomeData(String userId, Integer userType) {
        if(userType==1)
        {
            //student
            Student student = studentRepository.findByStudentId(userId);
            LearningProgress learningProgress = learningProgressRepository.findByStudentId(userId);
            if (student == null || learningProgress == null) {
                return new HomeStudentResponse(
                        "error",
                        "未找到学生或学习进度信息",
                        null,
                        null,
                        null,
                        0,
                        0,
                        null,
                        0,
                        null,
                        null
                );
            }
            List<Question> questions=questionRepository.findQuestionByStudentId(userId);

            return new HomeStudentResponse(
                    "success",
                    "成功找到学生",
                    student.getStudentName(),
                    "1",
                    student.getStudentId(),
                    learningProgress.getFinishedLecture(),
                    learningProgress.getFinishedQuiz(),
                    learningProgress.getGrade(),
                    questions.size(),
                    Arrays.asList("Task 1", "Task 2"),
                    Arrays.asList("Notification 1", "Notification 2")
            );

        }
        return new HomeStudentResponse(
                "error",
                "用户类型错误",
                null,
                null,
                null,
                0,
                0,
                null,
                0,
                null,
                null
        );
    }
}