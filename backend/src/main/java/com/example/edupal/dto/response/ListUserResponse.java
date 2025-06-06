package com.example.edupal.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ListUserResponse {
    private String status;          // 响应状态
    private String message;         // 响应消息
    private Integer userNum;       // 用户总数
    private List<UserDetail> users; // 用户集合

    public ListUserResponse(String status, String message,Integer userNum, List<UserDetail> users) {
        this.status = status;
        this.message = message;
        this.userNum = userNum;
        this.users = users;
    }


    @Data
    public static class UserDetail {
        private String userId;         // 用户ID
        private String userEmail;      // 用户邮箱
        private Integer userType;      // 用户类型
        private String userName;       // 用户名
        private Date createTime;       // 创建时间
        private Date loginTime;        // 登录时间

        public UserDetail(String userId, String userEmail, Integer userType,
                          String userName, Date createTime, Date loginTime) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.userType = userType;
            this.userName = userName;
            this.createTime = createTime;
            this.loginTime = loginTime;
        }
    }
}