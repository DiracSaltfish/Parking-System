package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingEntryRequest;
import com.example.parking.dto.parking.ParkingExemptionRequest;
import com.example.parking.dto.parking.ParkingExitRequest;
import com.example.parking.service.AdminParkingService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/parking")
public class AdminParkingController {

    private final AdminParkingService adminParkingService;

    public AdminParkingController(AdminParkingService adminParkingService) {
        this.adminParkingService = adminParkingService;
    }

    @GetMapping("/current")
    public ApiResponse<PageResult<Map<String, Object>>> current(
            @RequestParam(defaultValue = "") String plateNumber,
            @RequestParam(defaultValue = "") String payStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(adminParkingService.current(plateNumber, payStatus, pageNum, pageSize));
    }

    @PostMapping("/entry")
    public ApiResponse<Map<String, Object>> entry(@Valid @RequestBody ParkingEntryRequest request) {
        return ApiResponse.success("入场登记成功", adminParkingService.entry(request));
    }

    @PostMapping("/exit")
    public ApiResponse<Map<String, Object>> exit(@Valid @RequestBody ParkingExitRequest request) {
        return ApiResponse.success("出场办理成功", adminParkingService.exit(request));
    }

    @GetMapping("/records")
    public ApiResponse<PageResult<Map<String, Object>>> records(
            @RequestParam(defaultValue = "") String plateNumber,
            @RequestParam(defaultValue = "") String recordStatus,
            @RequestParam(defaultValue = "") String payStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(adminParkingService.records(plateNumber, recordStatus, payStatus, pageNum, pageSize));
    }

    @PostMapping("/exempt")
    public ApiResponse<Map<String, Object>> exempt(@Valid @RequestBody ParkingExemptionRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", adminParkingService.exempt(request));
    }

    @GetMapping("/exempt/{recordId}")
    public ApiResponse<Map<String, Object>> exemptionDetail(@PathVariable String recordId) {
        return ApiResponse.success(adminParkingService.exemptionDetail(recordId));
    }
}
