package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.dto.vehicle.VehicleBindRequest;
import com.example.parking.service.UserVehicleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/vehicles")
public class UserVehicleController {

    private final UserVehicleService userVehicleService;

    public UserVehicleController(UserVehicleService userVehicleService) {
        this.userVehicleService = userVehicleService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> vehicles(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return ApiResponse.success(userVehicleService.vehicles(authorizationHeader));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> bind(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody VehicleBindRequest request) {
        return ApiResponse.success("绑定成功", userVehicleService.bind(authorizationHeader, request));
    }

    @PutMapping("/{vehicleId}")
    public ApiResponse<Map<String, Object>> update(
                                                   @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                   @PathVariable String vehicleId,
                                                   @Valid @RequestBody VehicleBindRequest request) {
        return ApiResponse.success("修改成功", userVehicleService.update(authorizationHeader, vehicleId, request));
    }

    @DeleteMapping("/{vehicleId}")
    public ApiResponse<Map<String, Object>> delete(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String vehicleId) {
        return ApiResponse.success("删除成功", userVehicleService.delete(authorizationHeader, vehicleId));
    }
}
