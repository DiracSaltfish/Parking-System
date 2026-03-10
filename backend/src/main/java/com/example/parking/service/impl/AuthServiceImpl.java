package com.example.parking.service.impl;

import com.example.parking.dto.auth.AdminLoginRequest;
import com.example.parking.dto.auth.UserLoginRequest;
import com.example.parking.dto.auth.UserRegisterRequest;
import com.example.parking.service.AuthService;
import com.example.parking.support.InMemoryDataStore;
import com.example.parking.util.DateTimeUtils;
import com.example.parking.vo.auth.LoginResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final InMemoryDataStore dataStore;

    public AuthServiceImpl(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public LoginResponse adminLogin(AdminLoginRequest request) {
        Map<String, Object> admin = dataStore.findAdminByUsername(request.username());
        if (admin == null || !request.password().equals(admin.get("password"))) {
            throw new IllegalArgumentException("管理员账号或密码错误");
        }
        String token = dataStore.createSession("ADMIN", (String) admin.get("id"));
        return new LoginResponse(token, "ADMIN", (String) admin.get("id"),
                (String) admin.get("username"), (String) admin.get("displayName"));
    }

    @Override
    public LoginResponse userLogin(UserLoginRequest request) {
        Map<String, Object> user = dataStore.findUserByUsername(request.username());
        if (user == null || !request.password().equals(user.get("password"))) {
            throw new IllegalArgumentException("用户账号或密码错误");
        }
        String token = dataStore.createSession("USER", (String) user.get("userId"));
        return new LoginResponse(token, "USER", (String) user.get("userId"),
                (String) user.get("username"), (String) user.get("realName"));
    }

    @Override
    public Map<String, Object> register(UserRegisterRequest request) {
        Map<String, Object> user = dataStore.registerUser(
                request.username(),
                request.password(),
                request.phone(),
                request.realName()
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.get("userId"));
        result.put("username", user.get("username"));
        result.put("realName", user.get("realName"));
        result.put("phone", user.get("phone"));
        result.put("createTime", DateTimeUtils.format((java.time.LocalDateTime) user.get("createTime")));
        return result;
    }

    @Override
    public Map<String, Object> profile(String authorizationHeader) {
        Map<String, Object> session = dataStore.resolveSession(authorizationHeader);
        String role = (String) session.get("role");
        String userId = (String) session.get("userId");

        if ("ADMIN".equals(role)) {
            Map<String, Object> admin = dataStore.findAdminByUsername("admin");
            return Map.of(
                    "userId", admin.get("id"),
                    "role", "ADMIN",
                    "username", admin.get("username"),
                    "displayName", admin.get("displayName")
            );
        }

        Map<String, Object> user = dataStore.findUserById(userId);
        return Map.of(
                "userId", user.get("userId"),
                "role", "USER",
                "username", user.get("username"),
                "displayName", user.get("realName")
        );
    }
}
