package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingEntryRequest;
import com.example.parking.dto.parking.ParkingExemptionRequest;
import com.example.parking.dto.parking.ParkingExitRequest;
import com.example.parking.service.AdminParkingService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminParkingServiceImpl implements AdminParkingService {

    @Override
    public PageResult<Map<String, Object>> current(String plateNumber, String payStatus, int pageNum, int pageSize) {
        List<Map<String, Object>> records = List.of(
                Map.of(
                        "recordId", "R1001",
                        "plateNumber", "粤B12345",
                        "spaceCode", "A-021",
                        "entryTime", "2026-03-10 17:30:00",
                        "durationMinutes", 145,
                        "finalAmount", new BigDecimal("15.00"),
                        "payStatus", "UNPAID"
                )
        );
        return new PageResult<>(records.size(), pageNum, pageSize, records);
    }

    @Override
    public Map<String, Object> entry(ParkingEntryRequest request) {
        return Map.of(
                "recordId", "R1002",
                "plateNumber", request.plateNumber(),
                "spaceId", request.spaceId() == null || request.spaceId().isBlank() ? "AUTO-S001" : request.spaceId(),
                "message", "已创建入场记录，当前为骨架返回"
        );
    }

    @Override
    public Map<String, Object> exit(ParkingExitRequest request) {
        return Map.of(
                "recordId", request.recordId(),
                "recordStatus", "COMPLETED",
                "message", "已办理出场，当前为骨架返回"
        );
    }

    @Override
    public PageResult<Map<String, Object>> records(String plateNumber, String recordStatus, String payStatus, int pageNum, int pageSize) {
        List<Map<String, Object>> records = List.of(
                Map.of(
                        "recordId", "R0999",
                        "plateNumber", "粤B12345",
                        "entryTime", "2026-03-09 09:30:00",
                        "exitTime", "2026-03-09 12:00:00",
                        "durationMinutes", 150,
                        "originalAmount", new BigDecimal("15.00"),
                        "discountAmount", new BigDecimal("0.00"),
                        "finalAmount", new BigDecimal("15.00"),
                        "payStatus", "PAID",
                        "recordStatus", "COMPLETED"
                )
        );
        return new PageResult<>(records.size(), pageNum, pageSize, records);
    }

    @Override
    public Map<String, Object> exempt(ParkingExemptionRequest request) {
        return Map.of(
                "recordId", request.recordId(),
                "exemptionType", request.exemptionType(),
                "exemptionAmount", request.exemptionAmount(),
                "reason", request.reason(),
                "finalAmount", new BigDecimal("0.00")
        );
    }

    @Override
    public Map<String, Object> exemptionDetail(String recordId) {
        return Map.of(
                "recordId", recordId,
                "exemptionType", "FULL",
                "exemptionAmount", new BigDecimal("15.00"),
                "reason", "内部车辆免单",
                "operatorId", "A1001",
                "operateTime", "2026-03-10 20:20:00"
        );
    }
}
