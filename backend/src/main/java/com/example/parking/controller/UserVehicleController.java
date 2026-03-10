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
    public ApiResponse<List<Map<String, Object>>> vehicles() {
        return ApiResponse.success(userVehicleService.vehicles());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> bind(@Valid @RequestBody VehicleBindRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", userVehicleService.bind(request));
    }

    @PutMapping("/{vehicleId}")
    public ApiResponse<Map<String, Object>> update(@PathVariable String vehicleId,
                                                   @Valid @RequestBody VehicleBindRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", userVehicleService.update(vehicleId, request));
    }

    @DeleteMapping("/{vehicleId}")
    public ApiResponse<Map<String, Object>> delete(@PathVariable String vehicleId) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", userVehicleService.delete(vehicleId));
    }
}
