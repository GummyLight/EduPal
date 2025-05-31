package com.example.edupal.service.Impl;

import com.example.edupal.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerificationCodeToEmail(String email, String code) throws MessagingException {
        Date now = new Date();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject("【Edupal】忘记密码验证");
        helper.setFrom("18215137801@163.com");  // 发送人
        helper.setTo(email);
        helper.setSentDate(now);  // 富文本
        String context = "<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，您本次忘记密码的验证码是："+
                "<b color=\"'red'\">"  + code + "</b><br>"
                +"，有效期5分钟。请妥善保管，切勿泄露";
        helper.setText(context, true);
        javaMailSender.send(message);

    }
}
