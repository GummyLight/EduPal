package com.example.edupal.service.Impl;

import cn.hutool.core.util.RandomUtil;
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
import com.example.edupal.service.AuthService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.edupal.common.Result;
import com.example.edupal.service.EmailService;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public Result registerUser(String userId, String userName, String password, String email, Integer userType) {
        if (userRepository.existsByUserId(userId)) {
            return new Result(false, "用户名已存在");
        }
        if (userRepository.existsByUserEmail(email)) {
            return new Result(false, "邮箱已存在");
        }
        //任何一项为空都要报错
        if (userId == null || password == null || email == null) {
            return new Result(false, "未填写完整信息");
        }
        if(password.length() < 6){
            return new Result(false, "密码长度不能小于6");
        }
        User user = new User();
        user.setUserId(userId);
        user.setUserPassword(passwordEncoder.encode(password));
        user.setUserEmail(email);
        user.setUserName(userName);
        user.setUserType(userType);
        user.setCreateTime(new Date());
        userRepository.save(user);
        return new Result(true, "注册成功");
    }

    @Override
    public Result registerStudent(String userId, String userName, String password, String email, Integer userType,
                                  String studentClass, Integer studentGender) {
        Result result = registerUser(userId, userName, password, email, userType);
        if (!result.isSuccess()) {
            return result; // 如果注册用户失败，直接返回错误信息
        }
        // 创建学生对象
        Student student = new Student();
        student.setStudentId(userId);
        student.setStudentName(userName);
        student.setStudentClass(studentClass);
        student.setStudentGender(studentGender);
        studentRepository.save(student);
        //创建user
        User user=new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setUserEmail(email);
        user.setUserPassword(passwordEncoder.encode(password));
        user.setUserType(userType);
        user.setCreateTime(new Date());
        userRepository.save(user);


        return new Result(true, "学生注册成功");
    }

    @Override
    public Result registerTeacher(String userId, String userName, String password, String email, Integer userType,
                                  String teachingSubject, String[] teacherClass) {
        Result result = registerUser(userId, userName, password, email, userType);
        if (!result.isSuccess()) {
            return result; // 如果注册用户失败，直接返回错误信息
        }
        // 创建教师对象
        Teacher teacher = new Teacher();
        teacher.setTeacherId(userId);
        teacher.setTeacherName(userName);
        teacher.setTeachingSubject(teachingSubject);
        teacher.setClass1(teacherClass[0]);
        teacher.setClass2(teacherClass[1]);
        teacherRepository.save(teacher);
        //创建user
        User user=new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setUserEmail(email);
        user.setUserPassword(passwordEncoder.encode(password));
        user.setUserType(userType);
        user.setCreateTime(new Date());
        userRepository.save(user);

        return new Result(true, "教师注册成功");
    }

    @Override
    public Result loginUserByUserId(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            return new Result(false, "密码错误");
        }
        // 更新登录时间
        user.setLoginTime(new Date());
        userRepository.save(user);
        // 返回登录成功的结果
        return new Result(true, "登录成功", user);
    }

    @Override
    public Result loginUserByEmail(String email, String password) {
        User user = userRepository.findByUserEmail(email);

        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            return new Result(false, "密码错误");
        }
        // 更新登录时间
        user.setLoginTime(new Date());
        userRepository.save(user);
        // 返回登录成功的结果
        return new Result(true, "登录成功", user);
    }

    @Override
    public Result sendVerificationCode(String email) throws MessagingException {
        if (email == null || email.isEmpty()) {
            return new Result(false, "邮箱不能为空");
        }
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            return new Result(false, "用户不存在，请先注册");
        }
        //用当前的email在ValidationRepository中查找，查看有没有没有过期的验证码
        //如果有，直接返回验证码已发送
        //有可能查到很多记录
        Validation existingValidation = validationRepository.findByEmail(email);
        if (existingValidation != null) {
            if (!isExpired(existingValidation.getTime())) {
                return new Result(false, "验证码已发送，请检查您的邮箱");
            } else {
                // 如果查到的验证码过期了，删除它
                validationRepository.delete(existingValidation);
            }
        }

// 生成验证码
        String code = RandomUtil.randomNumbers(6); // 随机一个 6位长度的验证码

// 发送验证码到邮箱
        emailService.sendVerificationCodeToEmail(email, code);

// 保存验证码到数据库
        saveVerificationCode(email, code);

        return new Result(true, "验证码已发送，请检查您的邮箱");
    }

    @Override
    public Result resetPassword(String email, String code, String newPassword) {
        if (email == null || code == null || newPassword == null) {
            return new Result(false, "未填写完整信息");
        }
        if (newPassword.length() < 6) {
            return new Result(false, "密码长度不能小于6");
        }

        String verificationResult = verifyCode(email, code);
        if (!verificationResult.equals("验证码验证成功")) {
            return new Result(false, verificationResult);
        }

        // 验证成功，更新密码
        updateUserPassword(email, passwordEncoder.encode(newPassword));
        return new Result(true, "密码重置成功，请重新登录");
    }

    private String verifyCode(String email, String code) {
        // 从数据库验证邮箱和验证码是否匹配
        //用email在ValidationRepository中查找
        Validation validation = validationRepository.findByEmail(email);
        if (validation == null) {
            return "验证码不存在，请先获取验证码";
        }
        else if (isExpired(validation.getTime())) {
            //在Validation中删除过期的项目
            validationRepository.delete(validation);
            return "验证码已过期,请重新获取";
        }
        else if (!validation.getCode().equals(code)) {
            return "验证码错误，请重新输入";
        }

        return "验证码验证成功"; // 假设验证成功
    }

    private boolean isExpired(Date time) {
        // 检查Validation的time有没有早于现在的时间（Date类）
        Date now = new Date();
        return time.before(now); // 如果时间早于现在，视为过期
    }

    private void saveVerificationCode(String email, String code) {
        Validation validation = new Validation();
        validation.setEmail(email);
        validation.setCode(code);
        //将Validation的时间设置为当前时间+5分钟
        validation.setTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5分钟后过期
        validationRepository.save(validation);
    }

    private void updateUserPassword(String email, String newPassword) {
        User user = userRepository.findByUserEmail(email);
        if (user != null) {
            user.setUserPassword(newPassword);
            userRepository.save(user);
        } else {
            throw new RuntimeException("用户不存在");
        }
    }

    @Override
    public Result deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            return new Result(false, "用户不存在");
        }
        userRepository.deleteById(userId);
        return new Result(true, "用户删除成功");
    }

    @Override
    public Result updateUser(String userId, String newUserName, String newEmail, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (newUserName != null && !newUserName.isEmpty()) {
            user.setUserName(newUserName);
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            user.setUserEmail(newEmail);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setUserPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
        return new Result(true, "用户信息更新成功");
    }

    @Override
    public Result updateStudentClass(String studentId, String newClassId) {
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            return new Result(false, "学生不存在");
        }
        student.setStudentClass(newClassId);
        studentRepository.save(student);
        return new Result(true, "学生班级更新成功");
    }

    @Override
    public Result updateTeacherClass(String teacherId, String newClass1, String newClass2) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return new Result(false, "教师不存在");
        }
        teacher.setClass1(newClass1);
        teacher.setClass2(newClass2);
        teacherRepository.save(teacher);
        return new Result(true, "教师班级更新成功");
    }

    @Override
    public Result updateTeacherSubject(String teacherId, String newSubject){
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return new Result(false, "教师不存在");
        }
        teacher.setTeachingSubject(newSubject);
        teacherRepository.save(teacher);
        return new Result(true, "教师科目更新成功");
    }

    @Override
    public ListUserResponse listUsers() {
        List<User> users = userRepository.findAll();

        // 构建用户集合
        List<ListUserResponse.UserDetail> userDetails = users.stream().map(user ->
                new ListUserResponse.UserDetail(
                        user.getUserId(),
                        user.getUserEmail(),
                        user.getUserType(),
                        user.getUserName(),
                        user.getCreateTime(),
                        user.getLoginTime()

                )
        ).collect(Collectors.toList());

        return new ListUserResponse("success", "用户列表获取成功", userDetails.size(), userDetails);
    }

    @Override
    public ListStudentResponse listStudents() {
        List<Student> students = studentRepository.findAll();

        // 构建学生集合
        List<ListStudentResponse.StudentDetail> studentDetails = students.stream().map(student ->
                new ListStudentResponse.StudentDetail(
                        student.getStudentId(),
                        student.getStudentName(),
                        student.getStudentGender(),
                        student.getStudentClass()
                )
        ).collect(Collectors.toList());

        return new ListStudentResponse("success", "学生列表获取成功", studentDetails.size(), studentDetails);
    }

    @Override
    public ListTeacherResponse listTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();

        // 构建教师集合
        List<ListTeacherResponse.TeacherDetail> teacherDetails = teachers.stream().map(teacher ->
                new ListTeacherResponse.TeacherDetail(
                        teacher.getTeacherId(),
                        teacher.getTeacherName(),
                        teacher.getTeachingSubject(),
                        new String[]{teacher.getClass1(), teacher.getClass2()}
                )
        ).collect(Collectors.toList());

        return new ListTeacherResponse("success", "教师列表获取成功", teacherDetails.size(), teacherDetails);
    }







}