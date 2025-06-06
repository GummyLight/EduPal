package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.*;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class QuizServiceImplTest {

    @Autowired
    private QuizServiceImpl quizService;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private QuizAnswerRepository quizAnswerRepository;

    @MockitoBean
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        when(userRepository.findByUserId("S1")).thenReturn(new User("S1","xxx@email.com","Student",0,new Date(), new Date(),"小明"));
        when(userRepository.findByUserId("S2")).thenReturn(new User("S2","xxx@email.com","Student",0,new Date(), new Date(),"小明"));
        when(userRepository.findByUserId("T1")).thenReturn(new User());
        when(userRepository.findByUserId("T2")).thenReturn(new User());
        when(studentRepository.findByStudentId("S1")).thenReturn(new Student("S1", "Class1"));
        when(studentRepository.findByStudentId("S2")).thenReturn(null);
        when(teacherRepository.findByTeacherId("T1")).thenReturn(new Teacher());
        when(teacherRepository.findByTeacherId("T2")).thenReturn(null);
    }

    @Test
    void testGetStudentQuiz_StudentNotFound() throws Exception {
        GetStudentQuizResponse response = quizService.getStudentQuiz("S2");
        assertEquals("error", response.getStatus());
        assertEquals("Student not found", response.getMessage());
    }

    @Test
    void testGetStudentQuiz_NoQuizzes() throws Exception {
        GetStudentQuizResponse response = quizService.getStudentQuiz("S1");
        assertEquals("success", response.getStatus());
        assertEquals("No Your Quiz", response.getMessage());
        assertEquals(0, response.getQuizNum());
    }

    @Test
    void testGetStudentQuiz_WithQuizzes() throws Exception {
        List<Quiz> quizzes = Arrays.asList(
                new Quiz("Quiz1", "Math", "Content", "Easy", "Points", "Description", "T1", new Date(), new Date()),
                new Quiz("Quiz2", "Science", "Content", "Medium", "Points", "Description", "T1", new Date(), new Date())
        );
        when(quizRepository.findByClassId("Class1")).thenReturn(quizzes);
        when(quizAnswerRepository.findByStudentIdAndQuizId("S1", 1)).thenReturn(new QuizAnswer());
        when(quizAnswerRepository.findByStudentIdAndQuizId("S1", 2)).thenReturn(null);

        GetStudentQuizResponse response = quizService.getStudentQuiz("S1");
        assertEquals("success", response.getStatus());
        assertEquals("Get Your Quiz", response.getMessage());
        assertEquals(2, response.getQuizDetails().size());
    }

    @Test
    void testCreateQuiz() {
        CreateQuizRequest request = new CreateQuizRequest(
                "Quiz1", "Math", "Content", "Easy", "Points", "Description", "T1", new Date(), new Date(), "0021","0022"
        );
        when(quizRepository.save(any(Quiz.class))).thenReturn(new Quiz());
        Result result = quizService.createQuiz(request);
        assertTrue(result.isSuccess());
        assertEquals("Quiz created successfully", result.getMessage());
    }

    @Test
    void testModifyQuiz() {
        ModifyQuizRequest request = new ModifyQuizRequest(1, "Quiz1", "Math", "Content", "Easy", "Points", "Description", "T1", new Date(), new Date(), "0021","0022");
        when(quizRepository.findByQuizId(1)).thenReturn(new Quiz());
        when(quizRepository.save(any(Quiz.class))).thenReturn(new Quiz());
        Result result = quizService.modifyQuiz(request);
        assertTrue(result.isSuccess());
        assertEquals("Quiz modified successfully", result.getMessage());
    }

    @Test
    void testDeleteQuiz() {
        when(quizRepository.findByQuizId(1)).thenReturn(new Quiz());
        Result result = quizService.deleteQuiz(1);
        assertTrue(result.isSuccess());
        assertEquals("Quiz deleted successfully", result.getMessage());
    }
}