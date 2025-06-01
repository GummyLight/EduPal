package com.example.edupal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //csrf() is deprecated since version 6.1 and marked for removal,changed to csrf(AbstractHttpConfigurer::disable)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/**").permitAll() // 允许对/auth路径下的所有请求无需认证
                        .requestMatchers("/ai/**").permitAll() // 允许对/ai路径下的所有请求无需认证
                        .requestMatchers("/file/**").permitAll() // 允许对/file路径下的所有请求无需认证
                        .requestMatchers("/quiz/**").permitAll() // ✅ 加这一行，放行 quiz 模块接口
                        .requestMatchers("/community/**").permitAll()
                        .requestMatchers("/resource/**").permitAll() // 允许对/resource路径下的所有请求无需认证

                        .anyRequest().authenticated() // 其他所有请求都需要认证
                );
        return http.build(); // 构建并返回HTTP安全配置对象

    }
}
