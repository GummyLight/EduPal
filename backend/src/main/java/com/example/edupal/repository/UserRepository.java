package com.example.edupal.repository;

import com.example.edupal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    //exist方法是用来判断某个字段是否存在的，比如判断某个用户是否存在
    //需要在User类中定义好对应的字段，比如user_id, user_phone_num等，且字段类型要与方法参数类型一致
    //方法名中的属性名必须与实体类中的属性名一致
    boolean existsByUserId(String userId);
    boolean existsByUserPhoneNum(String userPhoneNum);

    // 根据 userId 查找用户的方法
    User findByUserId(String userId);
    // 根据 userPhoneNum 查找用户的方法
    User findByUserPhoneNum(String userPhoneNum);
}