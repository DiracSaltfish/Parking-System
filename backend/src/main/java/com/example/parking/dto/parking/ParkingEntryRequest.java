package com.example.parking.dto.parking;

import jakarta.validation.constraints.NotBlank;

public record ParkingEntryRequest(
        @NotBlank(message = "车牌号不能为空") String plateNumber,
        String spaceId,
        String spaceType
) {
}
