package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.model.User;
import com.example.edupal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceImplIntegrationTest {

    @Autowired
    private AuthServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // 清空数据库中的所有用户，确保测试环境的数据一致性
    }

    @Test
    public void testRegisterUserSuccess() {
        String userId = "s20250101";
        String password = "password123";
        String phoneNum = "12345678901";
        Integer userType = 1;

        Result result = userService.registerUser(userId, password, phoneNum, userType);
        assertTrue(result.isSuccess());
        assertEquals("注册成功", result.getMessage());

        User user = userRepository.findByUserId(userId);
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals(phoneNum, user.getUserPhoneNum());
        assertEquals(userType, user.getUserType());
        assertTrue(passwordEncoder.matches(password, user.getUserPassword())); // 验证密码是否正确编码
    }

    @Test
    public void testRegisterUserFailure_UserIdExists() {
        String userId = "s20250102";
        String password = "password123";
        String phoneNum = "12345678902";
        Integer userType = 1;

        // 先插入一个用户
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUserPhoneNum(phoneNum);
        existingUser.setUserType(userType);
        existingUser.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(existingUser);

        Result result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("用户名已存在", result.getMessage());

        // 确保只有一个用户
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testRegisterUserFailure_PhoneNumExists() {
        String userId = "s20250103";
        String password = "password123";
        String phoneNum = "12345678903";
        Integer userType = 1;

        // 先插入一个用户
        User existingUser = new User();
        existingUser.setUserId("anotherUser");
        existingUser.setUserPhoneNum(phoneNum);
        existingUser.setUserType(userType);
        existingUser.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(existingUser);

        Result result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("手机号已存在", result.getMessage());

        // 确保只有一个用户
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testRegisterUserFailure_IncompleteInformation() {
        String userId = null;
        String password = "password123";
        String phoneNum = "12345678904";
        Integer userType = 1;

        Result result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());

        userId = "s20250105";
        password = null;
        result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());

        password = "password123";
        phoneNum = null;
        result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());
    }

    @Test
    public void testRegisterUserFailure_PasswordTooShort() {
        String userId = "s20250106";
        String password = "123";
        String phoneNum = "12345678905";
        Integer userType = 1;

        Result result = userService.registerUser(userId, password, phoneNum, userType);
        assertFalse(result.isSuccess());
        assertEquals("密码长度不能小于6", result.getMessage());
    }

    @Test
    public void testLoginUserByUserIdSuccess() {
        String userId = "s20250107";
        String password = "password123";
        String phoneNum = "12345678907";
        Integer userType = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserPhoneNum(phoneNum);
        user.setUserType(userType);
        user.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Result result = userService.loginUserByUserId(userId, password);
        assertTrue(result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    public void testLoginUserByUserIdFailure_UserNotFound() {
        String userId = "s20250108";
        String password = "password123";

        Result result = userService.loginUserByUserId(userId, password);
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    public void testLoginUserByUserIdFailure_InvalidPassword() {
        String userId = "s20250109";
        String password = "password123";
        String phoneNum = "12345678909";
        Integer userType = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserPhoneNum(phoneNum);
        user.setUserType(userType);
        user.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Result result = userService.loginUserByUserId(userId, "wrongPassword");
        assertFalse(result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNumSuccess() {
        String userId = "s20250110";
        String password = "password123";
        String phoneNum = "12345678910";
        Integer userType = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserPhoneNum(phoneNum);
        user.setUserType(userType);
        user.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Result result = userService.loginUserByPhoneNum(phoneNum, password);
        assertTrue(result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNumFailure_UserNotFound() {
        String phoneNum = "12345678911";
        String password = "password123";

        Result result = userService.loginUserByPhoneNum(phoneNum, password);
        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNumFailure_InvalidPassword() {
        String userId = "s20250112";
        String password = "password123";
        String phoneNum = "12345678912";
        Integer userType = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserPhoneNum(phoneNum);
        user.setUserType(userType);
        user.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Result result = userService.loginUserByPhoneNum(phoneNum, "wrongPassword");
        assertFalse(result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }
}
