package com.example.parking.common;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> todo(String message, T data) {
        return new ApiResponse<>(202, message, data);
    }
}
