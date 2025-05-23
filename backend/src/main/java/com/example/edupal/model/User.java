package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id", updatable = false, unique = true)
    private String userId;

    @Column(name = "user_phone_num", unique = true)
    private String userPhoneNum;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_type")
    private Integer userType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_time")
    private Date loginTime;
}
