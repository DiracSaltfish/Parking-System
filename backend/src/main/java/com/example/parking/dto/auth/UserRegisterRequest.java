package com.example.parking.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterRequest(
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") String password,
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确") String phone,
        @NotBlank(message = "姓名不能为空") String realName
) {
}
