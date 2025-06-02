package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.ListStudentResponse;
import com.example.edupal.dto.response.ListTeacherResponse;
import com.example.edupal.dto.response.ListUserResponse;
import com.example.edupal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AuthService authService;
    //注册新账户
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        Result result=new Result(false,"注册失败");
        if (request.getUserType()==0)
        {
            // 管理员注册
            result= authService.registerUser(request.getUserId(), request.getUserName(), request.getPassword(), request.getEmail(), request.getUserType());
        }
        else if (request.getUserType()==1){
            // 学生注册
            result = authService.registerStudent(request.getUserId(), request.getUserName(), request.getPassword(), request.getEmail(), request.getUserType(),
                    request.getStudentClass(), request.getStudentGender());
        }
        else if(request.getUserType()==2){
            // 老师注册
            result = authService.registerTeacher(request.getUserId(), request.getUserName(), request.getPassword(), request.getEmail(), request.getUserType(),
                    request.getTeachingSubject(), request.getTeacherClass());
        }
        else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "无效的用户类型"));
        }
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }
    //注销账户
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam("userType") Integer userType,
                                        @RequestParam("userId") String userId) {
        if (userType!=0)
        {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "非管理员不可注销用户"));
        }
        Result result = authService.deleteUser(userId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }


    // 修改账户信息(user信息）

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam("userId") String userId,
                                        @RequestParam("newUserName") String newUserName,
                                        @RequestParam("newEmail") String newEmail,
                                        @RequestParam("newPassword") String newPassword) {
        Result result = authService.updateUser(userId, newUserName, newEmail, newPassword);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    //修改学生班级
    @PostMapping("/updateStudentClass")
    public ResponseEntity<?> updateStudentClass(@RequestParam("studentId") String studentId,
                                                @RequestParam("newClassId") String newClassId) {
        Result result = authService.updateStudentClass(studentId, newClassId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    //修改老师班级
    @PostMapping("/updateTeacherClass")
    public ResponseEntity<?> updateTeacherClass(@RequestParam("teacherId") String teacherId,
                                                @RequestParam("newClass1") String newClass1,
                                                @RequestParam("newClass2") String newClass2) {
        Result result = authService.updateTeacherClass(teacherId, newClass1, newClass2);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    //修改老师科目
    @PostMapping("/updateTeacherSubject")
    public ResponseEntity<?> updateTeacherSubject(@RequestParam("teacherId") String teacherId,
                                                  @RequestParam("newSubject") String newSubject) {
        Result result = authService.updateTeacherSubject(teacherId, newSubject);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    // 列出所有用户
    @GetMapping("/listUsers")
    public ListUserResponse listUsers() {
       try{
           return authService.listUsers();
       }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    // 列出所有学生
    @GetMapping("/listStudents")
    public ListStudentResponse listStudents() {
        try {
            return authService.listStudents();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
    // 列出所有老师
    @GetMapping("/listTeachers")
    public ListTeacherResponse listTeachers() {
    try {
            return authService.listTeachers();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
}
