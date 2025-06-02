package com.example.edupal.service.impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.response.ListStudentResponse;
import com.example.edupal.dto.response.ListTeacherResponse;
import com.example.edupal.dto.response.ListUserResponse;
import com.example.edupal.model.Student;
import com.example.edupal.model.Teacher;
import com.example.edupal.model.User;
import com.example.edupal.model.Validation;
import com.example.edupal.repository.StudentRepository;
import com.example.edupal.repository.TeacherRepository;
import com.example.edupal.repository.UserRepository;
import com.example.edupal.repository.ValidationRepository;
import com.example.edupal.service.EmailService;
import com.example.edupal.service.AuthService;
import com.example.edupal.service.Impl.AuthServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ValidationRepository validationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Test
    void testRegisterUser_UsernameExists() {
        when(userRepository.existsByUserId("testUser")).thenReturn(true);
        Result result = authService.registerUser("testUser", "password123", "test@example.com", 1);
        assertFalse(result.isSuccess());
        assertEquals("用户名已存在", result.getMessage());
    }

    @Test
    void testRegisterUser_EmailExists() {
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(true);
        Result result = authService.registerUser("testUser", "password123", "test@example.com", 1);
        assertFalse(result.isSuccess());
        assertEquals("邮箱已存在", result.getMessage());
    }

    @Test
    void testRegisterUser_IncompleteInfo() {
        Result result = authService.registerUser(null, "password123", "test@example.com", 1);
        assertFalse(result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());
    }

    @Test
    void testRegisterUser_ShortPassword() {
        Result result = authService.registerUser("testUser", "pass", "test@example.com", 1);
        assertFalse(result.isSuccess());
        assertEquals("密码长度不能小于6", result.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUserId("testUser")).thenReturn(false);
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(false);
        Result result = authService.registerUser("testUser", "password123", "test@example.com", 1);
        assertTrue(result.isSuccess());
        assertEquals("注册成功", result.getMessage());
    }

    @Test
    void testLoginUserByUserId_UserNotFound() {
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());
        Result result = authService.loginUserByUserId("testUser", "password123");
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    void testLoginUserByUserId_IncorrectPassword() {
        User user = new User();
        user.setUserId("testUser");
        user.setUserPassword(passwordEncoder.encode("password123"));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        Result result = authService.loginUserByUserId("testUser", "wrongPassword");
        assertFalse(result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    void testLoginUserByUserId_Success() {
        User user = new User();
        user.setUserId("testUser");
        user.setUserPassword(passwordEncoder.encode("123456"));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        Result result = authService.loginUserByUserId("testUser", "123456");
        assertTrue(result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    void testLoginUserByEmail_UserNotFound() {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(null);
        Result result = authService.loginUserByEmail("test@example.com", "password123");
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    void testLoginUserByEmail_IncorrectPassword() {
        User user = new User();
        user.setUserEmail("test@example.com");
        user.setUserPassword(passwordEncoder.encode("password123"));
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(user);
        Result result = authService.loginUserByEmail("test@example.com", "wrongPassword");
        assertFalse(result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    void testLoginUserByEmail_Success() {
        User user = new User();
        user.setUserEmail("test@example.com");
        user.setUserPassword(passwordEncoder.encode("password123"));
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(user);
        Result result = authService.loginUserByEmail("test@example.com", "password123");
        assertTrue(result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    void testSendVerificationCode_EmailEmpty() throws MessagingException, MessagingException {
        Result result = authService.sendVerificationCode("");
        assertFalse(result.isSuccess());
        assertEquals("邮箱不能为空", result.getMessage());
    }

    @Test
    void testSendVerificationCode_UserNotFound() throws MessagingException {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(null);
        Result result = authService.sendVerificationCode("test@example.com");
        assertFalse(result.isSuccess());
        assertEquals("用户不存在，请先注册", result.getMessage());
    }

    @Test
    void testSendVerificationCode_CodeExistsNotExpired() throws MessagingException {
        Validation validation = new Validation();
        validation.setEmail("test@example.com");
        validation.setTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        when(validationRepository.findByEmail("test@example.com")).thenReturn(validation);
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(new User()); // 模拟用户存在
        Result result = authService.sendVerificationCode("test@example.com");
        assertFalse(result.isSuccess());
        assertEquals("验证码已发送，请检查您的邮箱", result.getMessage());
    }

    @Test
    void testSendVerificationCode_CodeExpired() throws MessagingException {
        Validation validation = new Validation();
        validation.setEmail("test@example.com");
        validation.setTime(new Date(System.currentTimeMillis() - 10 * 60 * 1000));
        when(validationRepository.findByEmail("test@example.com")).thenReturn(validation);
        doNothing().when(validationRepository).delete(validation);
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(new User());
        Result result = authService.sendVerificationCode("test@example.com");
        assertTrue(result.isSuccess());
        assertEquals("验证码已发送，请检查您的邮箱", result.getMessage());
    }

    @Test
    void testSendVerificationCode_Success() throws MessagingException {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(new User());
        when(validationRepository.findByEmail("test@example.com")).thenReturn(null);
        doNothing().when(emailService).sendVerificationCodeToEmail("test@example.com", "123456");
        when(validationRepository.save(any(Validation.class))).thenReturn(null);
        Result result = authService.sendVerificationCode("test@example.com");
        assertTrue(result.isSuccess());
        assertEquals("验证码已发送，请检查您的邮箱", result.getMessage());
    }

    @Test
    void testResetPassword_IncompleteInfo() {
        Result result = authService.resetPassword("", "", "");
        assertFalse(result.isSuccess());
        assertEquals("密码长度不能小于6", result.getMessage());
    }

    @Test
    void testResetPassword_ShortPassword() {
        Result result = authService.resetPassword("test@example.com", "123", "password123");
        assertFalse(result.isSuccess());
        assertEquals("验证码不存在，请先获取验证码", result.getMessage());
    }

    @Test
    void testResetPassword_CodeVerificationFailed() {
        when(validationRepository.findByEmail("test@example.com")).thenReturn(null);
        Result result = authService.resetPassword("test@example.com", "123456", "password123");
        assertFalse(result.isSuccess());
        assertEquals("验证码不存在，请先获取验证码", result.getMessage());
    }

    @Test
    void testResetPassword_CodeExpired() {
        Validation validation = new Validation();
        validation.setEmail("test@example.com");
        validation.setTime(new Date(System.currentTimeMillis() - 10 * 60 * 1000));
        when(validationRepository.findByEmail("test@example.com")).thenReturn(validation);
        doNothing().when(validationRepository).delete(validation);
        Result result = authService.resetPassword("test@example.com", "123456", "password123");
        assertFalse(result.isSuccess());
        assertEquals("验证码已过期,请重新获取", result.getMessage());
    }

    @Test
    void testResetPassword_CodeIncorrect() {
        Validation validation = new Validation();
        validation.setEmail("test@example.com");
        validation.setTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        validation.setCode("654321");
        when(validationRepository.findByEmail("test@example.com")).thenReturn(validation);
        Result result = authService.resetPassword("test@example.com", "123456", "password123");
        assertFalse(result.isSuccess());
        assertEquals("验证码错误，请重新输入", result.getMessage());
    }

    @Test
    void testResetPassword_Success() {
        Validation validation = new Validation();
        validation.setEmail("test@example.com");
        validation.setTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        validation.setCode("123456");
        when(validationRepository.findByEmail("test@example.com")).thenReturn(validation);
        doNothing().when(validationRepository).delete(validation);
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        Result result = authService.resetPassword("test@example.com", "123456", "password123");
        assertTrue(result.isSuccess());
        assertEquals("密码重置成功，请重新登录", result.getMessage());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById("testUser")).thenReturn(false);
        Result result = authService.deleteUser("testUser");
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById("testUser")).thenReturn(true);
        doNothing().when(userRepository).deleteById("testUser");
        Result result = authService.deleteUser("testUser");
        assertTrue(result.isSuccess());
        assertEquals("用户删除成功", result.getMessage());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());
        Result result = authService.updateUser("testUser", "newName", "newEmail@example.com", "newPassword");
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User();
        user.setUserId("testUser");
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        Result result = authService.updateUser("testUser", "newName", "newEmail@example.com", "newPassword");
        assertTrue(result.isSuccess());
        assertEquals("用户信息更新成功", result.getMessage());
    }

    @Test
    void testUpdateStudentClass_StudentNotFound() {
        when(studentRepository.findByStudentId("studentId")).thenReturn(null);
        Result result = authService.updateStudentClass("studentId", "newClassId");
        assertFalse(result.isSuccess());
        assertEquals("学生不存在", result.getMessage());
    }

    @Test
    void testUpdateStudentClass_Success() {
        Student student = new Student();
        student.setStudentId("studentId");
        student.setStudentClass("oldClassId");
        when(studentRepository.findByStudentId("studentId")).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(null);
        Result result = authService.updateStudentClass("studentId", "newClassId");
        assertTrue(result.isSuccess());
        assertEquals("学生班级更新成功", result.getMessage());
    }

    @Test
    void testUpdateTeacherClass_TeacherNotFound() {
        when(teacherRepository.findById("teacherId")).thenReturn(Optional.empty());
        Result result = authService.updateTeacherClass("teacherId", "newClass1", "newClass2");
        assertFalse(result.isSuccess());
        assertEquals("教师不存在", result.getMessage());
    }

    @Test
    void testUpdateTeacherClass_Success() {
        Teacher teacher = new Teacher();
        teacher.setTeacherId("teacherId");
        when(teacherRepository.findById("teacherId")).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        Result result = authService.updateTeacherClass("teacherId", "newClass1", "newClass2");
        assertTrue(result.isSuccess());
        assertEquals("教师班级更新成功", result.getMessage());
    }

    @Test
    void testUpdateTeacherSubject_TeacherNotFound() {
        when(teacherRepository.findById("teacherId")).thenReturn(Optional.empty());
        Result result = authService.updateTeacherSubject("teacherId", "newSubject");
        assertFalse(result.isSuccess());
        assertEquals("教师不存在", result.getMessage());
    }

    @Test
    void testUpdateTeacherSubject_Success() {
        Teacher teacher = new Teacher();
        teacher.setTeacherId("teacherId");
        when(teacherRepository.findById("teacherId")).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        Result result = authService.updateTeacherSubject("teacherId", "newSubject");
        assertTrue(result.isSuccess());
        assertEquals("教师科目更新成功", result.getMessage());
    }

    @Test
    void testListUsers_Success() {
        User user = new User();
        user.setUserId("testUser");
        user.setUserEmail("test@example.com");
        user.setUserType(1);
        user.setUserName("Test User");
        user.setCreateTime(new Date());
        user.setLoginTime(new Date());
        when(userRepository.findAll()).thenReturn(List.of(user));
        ListUserResponse response = authService.listUsers();
        assertEquals("success", response.getStatus());
        assertEquals("用户列表获取成功", response.getMessage());
        assertEquals(1, response.getUsers().size());
    }

    @Test
    void testListStudents_Success() {
        Student student = new Student();
        student.setStudentId("studentId");
        student.setStudentName("Student Name");
        student.setStudentGender(1);
        student.setStudentClass("Class A");
        when(studentRepository.findAll()).thenReturn(List.of(student));
        ListStudentResponse response = authService.listStudents();
        assertEquals("success", response.getStatus());
        assertEquals("学生列表获取成功", response.getMessage());
        assertEquals(1, response.getStudents().size());
    }

    @Test
    void testListTeachers_Success() {
        Teacher teacher = new Teacher();
        teacher.setTeacherId("teacherId");
        teacher.setTeacherName("Teacher Name");
        teacher.setTeachingSubject("Math");
        teacher.setClass1("Class A");
        teacher.setClass2("Class B");
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));
        ListTeacherResponse response = authService.listTeachers();
        assertEquals("success", response.getStatus());
        assertEquals("教师列表获取成功", response.getMessage());
        assertEquals(1, response.getTeachers().size());
    }
}