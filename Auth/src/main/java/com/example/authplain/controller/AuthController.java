package com.example.authplain.controller;

import com.example.authplain.dto.ErrorResponse;
import com.example.authplain.dto.LoginRequest;
import com.example.authplain.dto.LoginResponse;
import com.example.authplain.dto.RegisterRequest;
import com.example.authplain.dto.RegisterResponse;
import com.example.authplain.entity.User;
import com.example.authplain.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册与登录控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 注册接口
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(new RegisterResponse("success", "User registered successfully.", user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("error", e.getMessage()));
        }
    }

    /**
     * 登录接口
     *
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            return ResponseEntity.ok(new LoginResponse("success", user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("error", e.getMessage()));
        }
    }
}
