package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.dto.response.ListStudentResponse;
import com.example.edupal.dto.response.ListTeacherResponse;
import com.example.edupal.dto.response.ListUserResponse;
import jakarta.mail.MessagingException;

public interface AuthService {
    Result registerUser(String userId, String userName, String password, String phoneNum, Integer userType);
    Result registerStudent(String userId, String userName, String password, String email, Integer userType,
                             String studentClass, Integer studentGender);
    Result registerTeacher(String userId, String userName, String password, String email, Integer userType,
                           String teachingSubject, String[] teacherClass);
    Result loginUserByUserId(String userId, String password);
    Result loginUserByEmail(String email, String password);
    Result sendVerificationCode(String mail) throws MessagingException;
    Result resetPassword(String email, String code, String newPassword);
    Result deleteUser(String userId);
    Result updateUser(String userId, String newUserName, String newEmail, String newPassword);
    Result updateStudentClass(String studentId, String newClassId);

    Result updateTeacherClass(String teacherId, String newClass1, String newClass2);
    Result updateTeacherSubject(String teacherId, String newSubject);

    ListUserResponse listUsers();

    ListStudentResponse listStudents();

    ListTeacherResponse listTeachers();

}