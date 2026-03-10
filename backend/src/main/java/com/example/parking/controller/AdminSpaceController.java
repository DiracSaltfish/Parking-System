package com.example.parking.controller;

import com.example.parking.common.ApiResponse;
import com.example.parking.common.PageResult;
import com.example.parking.dto.space.FeeRuleUpdateRequest;
import com.example.parking.dto.space.ParkingSpaceSaveRequest;
import com.example.parking.service.AdminSpaceService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminSpaceController {

    private final AdminSpaceService adminSpaceService;

    public AdminSpaceController(AdminSpaceService adminSpaceService) {
        this.adminSpaceService = adminSpaceService;
    }

    @GetMapping("/spaces")
    public ApiResponse<PageResult<Map<String, Object>>> spaces(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(adminSpaceService.spaces(status, type, pageNum, pageSize));
    }

    @PostMapping("/spaces")
    public ApiResponse<Map<String, Object>> createSpace(@Valid @RequestBody ParkingSpaceSaveRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", adminSpaceService.createSpace(request));
    }

    @PutMapping("/spaces/{spaceId}")
    public ApiResponse<Map<String, Object>> updateSpace(@PathVariable String spaceId,
                                                        @Valid @RequestBody ParkingSpaceSaveRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", adminSpaceService.updateSpace(spaceId, request));
    }

    @DeleteMapping("/spaces/{spaceId}")
    public ApiResponse<Map<String, Object>> deleteSpace(@PathVariable String spaceId) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", adminSpaceService.deleteSpace(spaceId));
    }

    @GetMapping("/fee-rule")
    public ApiResponse<Map<String, Object>> feeRule() {
        return ApiResponse.success(adminSpaceService.feeRule());
    }

    @PutMapping("/fee-rule")
    public ApiResponse<Map<String, Object>> updateFeeRule(@Valid @RequestBody FeeRuleUpdateRequest request) {
        return ApiResponse.todo("已创建接口骨架，等待接入 Redis", adminSpaceService.updateFeeRule(request));
    }
}
