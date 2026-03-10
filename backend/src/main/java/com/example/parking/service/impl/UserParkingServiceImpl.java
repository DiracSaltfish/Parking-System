package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingPaymentRequest;
import com.example.parking.service.UserParkingService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserParkingServiceImpl implements UserParkingService {

    @Override
    public Map<String, Object> currentParking() {
        return Map.of(
                "recordId", "R1001",
                "plateNumber", "粤B12345",
                "spaceCode", "A-021",
                "entryTime", "2026-03-10 17:30:00",
                "durationMinutes", 145,
                "originalAmount", new BigDecimal("15.00"),
                "discountAmount", new BigDecimal("0.00"),
                "finalAmount", new BigDecimal("15.00"),
                "payStatus", "UNPAID",
                "note", "当前为骨架数据，后续改为 Redis 实时计算"
        );
    }

    @Override
    public PageResult<Map<String, Object>> records(String plateNumber, int pageNum, int pageSize) {
        List<Map<String, Object>> records = List.of(
                Map.of(
                        "recordId", "R0999",
                        "plateNumber", "粤B12345",
                        "entryTime", "2026-03-09 09:30:00",
                        "exitTime", "2026-03-09 12:00:00",
                        "durationMinutes", 150,
                        "finalAmount", new BigDecimal("15.00"),
                        "payStatus", "PAID"
                )
        );
        return new PageResult<>(records.size(), pageNum, pageSize, records);
    }

    @Override
    public PageResult<Map<String, Object>> payments(int pageNum, int pageSize) {
        List<Map<String, Object>> records = List.of(
                Map.of(
                        "paymentId", "P1001",
                        "recordId", "R0999",
                        "payAmount", new BigDecimal("15.00"),
                        "payMethod", "WECHAT",
                        "payStatus", "PAID",
                        "payTime", "2026-03-09 12:00:00"
                )
        );
        return new PageResult<>(records.size(), pageNum, pageSize, records);
    }

    @Override
    public Map<String, Object> pay(ParkingPaymentRequest request) {
        return Map.of(
                "paymentId", "P1002",
                "recordId", request.recordId(),
                "payMethod", request.payMethod(),
                "payAmount", new BigDecimal("15.00"),
                "payStatus", "PAID",
                "payTime", "2026-03-10 20:15:00"
        );
    }
}
