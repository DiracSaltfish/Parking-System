package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.service.AdminDashboardService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        return ApiResponse.success(adminDashboardService.summary());
    }
}
