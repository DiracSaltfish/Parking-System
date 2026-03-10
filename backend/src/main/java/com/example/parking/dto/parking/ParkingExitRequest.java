package com.example.parking.dto.parking;

import jakarta.validation.constraints.NotBlank;

public record ParkingExitRequest(
        @NotBlank(message = "停车记录不能为空") String recordId
) {
}
