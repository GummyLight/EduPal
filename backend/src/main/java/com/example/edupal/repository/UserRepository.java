package com.example.edupal.repository;

import com.example.edupal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {
    //exist方法是用来判断某个字段是否存在的，比如判断某个用户是否存在
    //需要在User类中定义好对应的字段，比如user_id, user_phone_num等，且字段类型要与方法参数类型一致
    //方法名中的属性名必须与实体类中的属性名一致
    boolean existsByUserId(String userId);
    boolean existsByUserEmail(String email);

    // 根据 userId 查找用户的方法
    User findByUserId(String userId);
    // 根据 userPhoneNum 查找用户的方法
    User findByUserEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 2 AND DATE(u.loginTime) = CURRENT_DATE")
    int countTeacherLoggedInToday();

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 1 AND DATE(u.loginTime) = CURRENT_DATE")
    int countStudentLoggedInToday();

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.loginTime) = CURRENT_DATE")
    int countLoggedInToday();

    @Query("SELECT COUNT(u) FROM User u")
    int countUser();

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 1")
    int countStudent();

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 2")
    int countTeacher();
}