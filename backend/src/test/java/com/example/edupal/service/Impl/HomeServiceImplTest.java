package com.example.edupal.service.Impl;

import com.example.edupal.dto.response.*;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HomeServiceImplTest {

    @Autowired
    private HomeServiceImpl homeService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private TeacherRepository teacherRepository;

    @MockitoBean
    private LearningProgressRepository learningProgressRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private ResourceRepository resourceRepository;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private TeacherAnswerRepository teacherAnswerRepository;

    @BeforeEach
    void setUp() {
        // 初始化Mock数据
        when(userRepository.countUser()).thenReturn(100);
        when(userRepository.countTeacher()).thenReturn(20);
        when(userRepository.countStudent()).thenReturn(80);
        when(userRepository.countTeacherLoggedInToday()).thenReturn(10);
        when(userRepository.countStudentLoggedInToday()).thenReturn(50);
        when(userRepository.countLoggedInToday()).thenReturn(60);
        when(resourceRepository.countResource()).thenReturn(50);
        when(quizRepository.countAllQuiz()).thenReturn(30);
        when(postRepository.countPost()).thenReturn(20);
        when(questionRepository.countAllQuestion()).thenReturn(100);
    }

    @Test
    void testGetStudentHomeData() {
        // Mock数据
        String userId = "S1";
        Integer userType = 1;
        Student student = new Student();
        student.setStudentId(userId);
        student.setStudentName("Student 1");

        LearningProgress learningProgress = new LearningProgress();
        learningProgress.setStudentId(userId);
        learningProgress.setFinishedLecture(5);
        learningProgress.setFinishedQuiz(3);
        learningProgress.setGrade("A");

        List<Question> questions = Arrays.asList(
                new Question(),
                new Question(),
                new Question()
        );

        // 设置Mock行为
        when(studentRepository.findByStudentId(userId)).thenReturn(student);
        when(learningProgressRepository.findByStudentId(userId)).thenReturn(learningProgress);
        when(questionRepository.findQuestionByStudentId(userId)).thenReturn(questions);

        // 调用方法
        HomeStudentResponse response = homeService.getStudentHomeData(userId, userType);

        // 验证结果
        assertEquals("success", response.getStatus());
        assertEquals("成功找到学生", response.getMessage());
        assertEquals("Student 1", response.getUsername());
        assertEquals("1", response.getUserType());
        assertEquals(userId, response.getUserId());
        assertEquals(5, response.getFinishedLectures());
        assertEquals(3, response.getFinishedExercises());
        assertEquals("A", response.getGradeLevel());
        assertEquals(3, response.getAnswerNum());
        assertNotNull(response.getTodayTasks());
        assertNotNull(response.getNotifications());
    }

    @Test
    void testGetTeacherHomeData() {
        // Mock数据
        String userId = "T1";
        Integer userType = 2;
        com.example.edupal.model.Teacher teacher = new com.example.edupal.model.Teacher();
        teacher.setTeacherId(userId);
        teacher.setTeacherName("Teacher 1");

        // 设置Mock行为
        when(teacherRepository.findByTeacherId(userId)).thenReturn(teacher);
        when(teacherAnswerRepository.findByTeacherId(userId)).thenReturn(Arrays.asList(
                new TeacherAnswer(),
                new TeacherAnswer()
        ));
        when(quizRepository.countByTeacherId(userId)).thenReturn(10);
        when(resourceRepository.countByTeacherId(userId)).thenReturn(5);
        when(teacherRepository.findClassesByTeacherId(userId)).thenReturn(Arrays.asList("Class 1", "Class 2"));

        // 调用方法
        HomeTeacherResponse response = homeService.getTeacherHomeData(userId, userType);

        // 验证结果
        assertEquals("success", response.getStatus());
        assertEquals("成功找到教师", response.getMessage());
        assertEquals("Teacher 1", response.getUsername());
        assertEquals("2", response.getUserType());
        assertEquals(userId, response.getUserId());
        assertEquals(2, response.getUnfinishedQAs());
        assertEquals(10, response.getUploadExercises());
        assertEquals(5, response.getUploadResources());
        assertNotNull(response.getClass());
        assertNotNull(response.getTopStudents());
        assertNotNull(response.getBottomStudents());
    }

    @Test
    void testGetAdminHomeData() {
        // Mock数据
        String userId = "A1";
        Integer userType = 0;

        // 调用方法
        HomeAdminResponse response = homeService.getAdminHomeData(userId, userType);

        // 验证结果
        assertEquals("success", response.getStatus());
        assertEquals("统计数据成功", response.getMessage());
        assertEquals(100, response.getTotalUsers());
        assertEquals(20, response.getTotalTeachers());
        assertEquals(80, response.getTotalStudents());
        assertEquals(10, response.getTodayLoggedInTeachers());
        assertEquals(50, response.getTodayLoggedInStudents());
        assertEquals(60, response.getTodayLoggedInUsers());
        assertEquals(50, response.getTotalTeachingMaterials());
        assertEquals(30, response.getTotalExercises());
        assertEquals(20, response.getTotalCommunityTopics());
        assertEquals(100, response.getTotalStudentQuestions());
    }

    @Test
    void testGetStudentHomeData_InvalidUserType() {
        // Mock数据
        String userId = "S1";
        Integer userType = 2;

        // 调用方法
        HomeStudentResponse response = homeService.getStudentHomeData(userId, userType);

        // 验证结果
        assertEquals("error", response.getStatus());
        assertEquals("用户类型错误", response.getMessage());
    }

    @Test
    void testGetTeacherHomeData_InvalidUserType() {
        // Mock数据
        String userId = "T1";
        Integer userType = 1;

        // 调用方法
        HomeTeacherResponse response = homeService.getTeacherHomeData(userId, userType);

        // 验证结果
        assertEquals("error", response.getStatus());
        assertEquals("用户类型错误", response.getMessage());
    }

    @Test
    void testGetAdminHomeData_InvalidUserType() {
        // Mock数据
        String userId = "A1";
        Integer userType = 1;

        // 调用方法
        HomeAdminResponse response = homeService.getAdminHomeData(userId, userType);

        // 验证结果
        assertEquals("error", response.getStatus());
        assertEquals("用户类型错误", response.getMessage());
    }
}