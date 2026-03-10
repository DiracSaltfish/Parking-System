package com.example.parking.vo.auth;

public record LoginResponse(
        String token,
        String role,
        String userId,
        String username,
        String displayName
) {
}
