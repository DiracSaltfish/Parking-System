package com.example.parking.dto.space;

import jakarta.validation.constraints.NotBlank;

public record ParkingSpaceSaveRequest(
        @NotBlank(message = "车位编号不能为空") String spaceCode,
        @NotBlank(message = "车位类型不能为空") String type,
        @NotBlank(message = "楼层不能为空") String floor,
        String remark
) {
}
