package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingPaymentRequest;
import com.example.parking.service.UserParkingService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserParkingController {

    private final UserParkingService userParkingService;

    public UserParkingController(UserParkingService userParkingService) {
        this.userParkingService = userParkingService;
    }

    @GetMapping("/parking/current")
    public ApiResponse<Map<String, Object>> currentParking() {
        return ApiResponse.success(userParkingService.currentParking());
    }

    @GetMapping("/parking/records")
    public ApiResponse<PageResult<Map<String, Object>>> records(
            @RequestParam(defaultValue = "") String plateNumber,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userParkingService.records(plateNumber, pageNum, pageSize));
    }

    @GetMapping("/payments")
    public ApiResponse<PageResult<Map<String, Object>>> payments(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userParkingService.payments(pageNum, pageSize));
    }

    @PostMapping("/payments/pay")
    public ApiResponse<Map<String, Object>> pay(@Valid @RequestBody ParkingPaymentRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", userParkingService.pay(request));
    }
}
