package com.example.parking.service;

import com.example.parking.dto.auth.AdminLoginRequest;
import com.example.parking.dto.auth.UserLoginRequest;
import com.example.parking.dto.auth.UserRegisterRequest;
import com.example.parking.vo.auth.LoginResponse;
import java.util.Map;

public interface AuthService {

    LoginResponse adminLogin(AdminLoginRequest request);

    LoginResponse userLogin(UserLoginRequest request);

    Map<String, Object> register(UserRegisterRequest request);

    Map<String, Object> profile(String authorizationHeader);
}
