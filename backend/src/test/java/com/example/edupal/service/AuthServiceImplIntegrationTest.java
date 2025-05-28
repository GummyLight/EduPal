//package com.example.edupal.service;
//
//import com.example.edupal.common.Result;
//import com.example.edupal.model.User;
//import com.example.edupal.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class AuthServiceImplIntegrationTest {
//
//    @Autowired
//    private AuthServiceImpl userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @BeforeEach
//    public void setUp() {
//        userRepository.deleteAll(); // 清空数据库中的所有用户，确保测试环境的数据一致性
//    }
//
//    @Test
//    public void testRegisterUserSuccess() {
//        String userId = "s20250101";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        Result result = userService.registerUser(userId, password, email, userType);
//        assertTrue(result.isSuccess());
//        assertEquals("注册成功", result.getMessage());
//
//        User user = userRepository.findByUserId(userId);
//        assertNotNull(user);
//        assertEquals(userId, user.getUserId());
//        assertEquals(email, user.getUserEmail());
//        assertEquals(userType, user.getUserType());
//        assertTrue(passwordEncoder.matches(password, user.getUserPassword())); // 验证密码是否正确编码
//    }
//
//    @Test
//    public void testRegisterUserFailure_UserIdExists() {
//        String userId = "s20250102";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        // 先插入一个用户
//        User existingUser = new User();
//        existingUser.setUserId(userId);
//        existingUser.setUserEmail(email);
//        existingUser.setUserType(userType);
//        existingUser.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(existingUser);
//
//        Result result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("用户名已存在", result.getMessage());
//
//        // 确保只有一个用户
//        assertEquals(1, userRepository.count());
//    }
//
//    @Test
//    public void testRegisterUserFailure_EmailExists() {
//        String userId = "s20250103";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        // 先插入一个用户
//        User existingUser = new User();
//        existingUser.setUserId("anotherUser");
//        existingUser.setUserEmail(email);
//        existingUser.setUserType(userType);
//        existingUser.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(existingUser);
//
//        Result result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("邮箱已存在", result.getMessage());
//
//        // 确保只有一个用户
//        assertEquals(1, userRepository.count());
//    }
//
//    @Test
//    public void testRegisterUserFailure_IncompleteInformation() {
//        String userId = null;
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        Result result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("未填写完整信息", result.getMessage());
//
//        userId = "s20250105";
//        password = null;
//        result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("未填写完整信息", result.getMessage());
//
//        password = "password123";
//        email = null;
//        result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("未填写完整信息", result.getMessage());
//    }
//
//    @Test
//    public void testRegisterUserFailure_PasswordTooShort() {
//        String userId = "s20250106";
//        String password = "123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        Result result = userService.registerUser(userId, password, email, userType);
//        assertFalse(result.isSuccess());
//        assertEquals("密码长度不能小于6", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByUserIdSuccess() {
//        String userId = "s20250107";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setUserEmail(email);
//        user.setUserType(userType);
//        user.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(user);
//
//        Result result = userService.loginUserByUserId(userId, password);
//        assertTrue(result.isSuccess());
//        assertEquals("登录成功", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByUserIdFailure_UserNotFound() {
//        String userId = "s20250108";
//        String password = "password123";
//
//        Result result = userService.loginUserByUserId(userId, password);
//        assertFalse(result.isSuccess());
//        assertEquals("用户不存在", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByUserIdFailure_InvalidPassword() {
//        String userId = "s20250109";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setUserEmail(email);
//        user.setUserType(userType);
//        user.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(user);
//
//        Result result = userService.loginUserByUserId(userId, "wrongPassword");
//        assertFalse(result.isSuccess());
//        assertEquals("密码错误", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByEmailSuccess() {
//        String userId = "s20250110";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setUserEmail(email);
//        user.setUserType(userType);
//        user.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(user);
//
//        Result result = userService.loginUserByEmail(email, password);
//        assertTrue(result.isSuccess());
//        assertEquals("登录成功", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByEmailFailure_UserNotFound() {
//        String Email = "12345678911";
//        String password = "password123";
//
//        Result result = userService.loginUserByEmail(Email, password);
//        assertFalse(result.isSuccess());
//        assertEquals("用户不存在", result.getMessage());
//    }
//
//    @Test
//    public void testLoginUserByEmailFailure_InvalidPassword() {
//        String userId = "s20250112";
//        String password = "password123";
//        String email = "1323858543@qq.com";
//        Integer userType = 1;
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setUserEmail(email);
//        user.setUserType(userType);
//        user.setUserPassword(passwordEncoder.encode(password));
//        userRepository.save(user);
//
//        Result result = userService.loginUserByEmail(email, "wrongPassword");
//        assertFalse(result.isSuccess());
//        assertEquals("密码错误", result.getMessage());
//    }
//}
