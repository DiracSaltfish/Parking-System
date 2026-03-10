package com.example.parking.service.impl;

import com.example.parking.dto.auth.AdminLoginRequest;
import com.example.parking.dto.auth.UserLoginRequest;
import com.example.parking.dto.auth.UserRegisterRequest;
import com.example.parking.service.AuthService;
import com.example.parking.vo.auth.LoginResponse;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse adminLogin(AdminLoginRequest request) {
        return new LoginResponse("admin-token-demo", "ADMIN", "A1001", request.username(), "系统管理员");
    }

    @Override
    public LoginResponse userLogin(UserLoginRequest request) {
        return new LoginResponse("user-token-demo", "USER", "U1001", request.username(), "普通用户");
    }

    @Override
    public Map<String, Object> register(UserRegisterRequest request) {
        return Map.of(
                "userId", "U1001",
                "username", request.username(),
                "realName", request.realName(),
                "phone", request.phone()
        );
    }

    @Override
    public Map<String, Object> profile(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.contains("admin")) {
            return Map.of(
                    "userId", "A1001",
                    "role", "ADMIN",
                    "username", "admin",
                    "displayName", "系统管理员"
            );
        }

        return Map.of(
                "userId", "U1001",
                "role", "USER",
                "username", "zhangsan",
                "displayName", "张三"
        );
    }
}
