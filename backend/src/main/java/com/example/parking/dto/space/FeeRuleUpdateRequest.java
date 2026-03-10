package com.example.parking.dto.space;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FeeRuleUpdateRequest(
        @NotNull(message = "免费分钟数不能为空") @Min(value = 0, message = "免费分钟数不能小于0") Integer freeMinutes,
        @NotNull(message = "小时单价不能为空") BigDecimal pricePerHour,
        @NotNull(message = "日封顶金额不能为空") BigDecimal dailyMaxAmount
) {
}
