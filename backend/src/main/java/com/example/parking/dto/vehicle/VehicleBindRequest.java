package com.example.parking.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleBindRequest(
        @NotBlank(message = "车牌号不能为空") String plateNumber,
        @NotNull(message = "是否主车牌不能为空") Boolean isPrimary
) {
}
