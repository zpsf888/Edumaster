package com.example.authplain.service;

import com.example.authplain.dto.LoginRequest;
import com.example.authplain.dto.RegisterRequest;
import com.example.authplain.entity.User;
import com.example.authplain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册：先检查用户名是否已存在，若不存在则保存新用户（明文密码）
     */
    public User register(RegisterRequest request) {
        User existing = userRepository.findByUsername(request.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已被占用");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword());  // 明文保存，仅示例
        newUser.setEmail(request.getEmail());
        newUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    /**
     * 用户登录：直接对比明文密码
     */
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }
}
