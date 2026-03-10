package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.dto.auth.AdminLoginRequest;
import com.example.parking.dto.auth.UserLoginRequest;
import com.example.parking.dto.auth.UserRegisterRequest;
import com.example.parking.service.AuthService;
import com.example.parking.vo.auth.LoginResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(authService.adminLogin(request));
    }

    @PostMapping("/user/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/user/login")
    public ApiResponse<LoginResponse> userLogin(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(authService.userLogin(request));
    }

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> profile(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return ApiResponse.success(authService.profile(authorizationHeader));
    }
}
