package com.example.parking.dto.parking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ParkingExemptionRequest(
        @NotBlank(message = "停车记录不能为空") String recordId,
        @NotBlank(message = "减免类型不能为空") String exemptionType,
        @NotNull(message = "减免金额不能为空") BigDecimal exemptionAmount,
        @NotBlank(message = "减免原因不能为空") String reason
) {
}
