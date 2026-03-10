package com.example.parking.dto.parking;

import jakarta.validation.constraints.NotBlank;

public record ParkingPaymentRequest(
        @NotBlank(message = "停车记录不能为空") String recordId,
        @NotBlank(message = "支付方式不能为空") String payMethod
) {
}
