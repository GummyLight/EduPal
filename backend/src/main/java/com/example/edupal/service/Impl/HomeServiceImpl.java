package com.example.edupal.service.Impl;

import com.example.edupal.dto.response.HomeAdminResponse;
import com.example.edupal.dto.response.HomeStudentResponse;
import com.example.edupal.dto.response.HomeTeacherResponse;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    LearningProgressRepository learningProgressRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    TeacherAnswerRepository teacherAnswerRepository;

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

    @Override
    public HomeTeacherResponse getTeacherHomeData(String userId, Integer userType) {
        if (userType == 2) {
            // teacher
            com.example.edupal.model.Teacher teacher = teacherRepository.findByTeacherId(userId);
            if (teacher == null) {
                return new HomeTeacherResponse(
                        "error",
                        "未找到教师信息",
                        null,
                        null,
                        null,
                        0,
                        0,
                        0,
                        null,
                        null,
                        null
                );
            }
            //List<Question> questions = questionRepository.findQuestionByTeacherId(userId);

            return new HomeTeacherResponse(
                    "success",
                    "成功找到教师",
                    teacher.getTeacherName(),
                    "2",
                    teacher.getTeacherId(),
                    teacherGetUnfinishedQA(userId), // Unfinished Q&A count
                    quizRepository.countByTeacherId(userId), // Placeholder for uploadExercises
                    resourceRepository.countByTeacherId(userId), // Placeholder for uploadResources
                    teacherRepository.findClassesByTeacherId(userId), // Example class IDs
                    Arrays.asList(
                            new HomeTeacherResponse.StudentDetails("S1", "Student 1", 85.5, 2.3),
                            new HomeTeacherResponse.StudentDetails("S2", "Student 2", 90.0, 1.8)
                    ), // Example top students
                    Arrays.asList(
                            new HomeTeacherResponse.StudentDetails("S3", "Student 3", 60.0, 5.0),
                            new HomeTeacherResponse.StudentDetails("S4", "Student 4", 55.0, 4.5)
                    ) // Example bottom students
            );
        }
        return new HomeTeacherResponse(
                "error",
                "用户类型错误",
                null,
                null,
                null,
                0,
                0,
                0,
                null,
                null,
                null
        );
    }

    @Override
    public HomeAdminResponse getAdminHomeData(String userId, Integer userType){
        if (userType == 0) { // Assuming userType 0 corresponds to admin
            int totalUsers = userRepository.countUser(); // Example method to count total users
            int totalTeachers = userRepository.countTeacher();
            int totalStudents = userRepository.countStudent();
            int todayLoggedInTeachers = userRepository.countTeacherLoggedInToday();
            int todayLoggedInStudents = userRepository.countStudentLoggedInToday();
            int todayLoggedInUsers = userRepository.countLoggedInToday();
            int totalTeachingMaterials = resourceRepository.countResource(); // Example method
            int totalExercises = quizRepository.countAllQuiz(); // Example method
            int totalCommunityTopics = postRepository.countPost(); // Example method
            int totalStudentQuestions = questionRepository.countAllQuestion(); // Example method

            return new HomeAdminResponse(
                    "success",
                    "统计数据成功",
                    totalUsers,
                    totalTeachers,
                    totalStudents,
                    todayLoggedInTeachers,
                    todayLoggedInStudents,
                    todayLoggedInUsers,
                    totalTeachingMaterials,
                    totalExercises,
                    totalCommunityTopics,
                    totalStudentQuestions
            );
        }
        return new HomeAdminResponse(
                "error",
                "用户类型错误",
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
    }

    private int teacherGetUnfinishedQA(String teacherId) {
        List<TeacherAnswer> teacherAnswer = teacherAnswerRepository.findByTeacherId(teacherId);
        int unfinishedCount = 0;
        for (TeacherAnswer answer : teacherAnswer) {
            if (answer.getAnswerId()==null) {
                unfinishedCount++;
            }
        }
        return unfinishedCount;
    }


}