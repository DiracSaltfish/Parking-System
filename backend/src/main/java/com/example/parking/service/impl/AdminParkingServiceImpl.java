package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingEntryRequest;
import com.example.parking.dto.parking.ParkingExemptionRequest;
import com.example.parking.dto.parking.ParkingExitRequest;
import com.example.parking.repository.RedisDataStore;
import com.example.parking.service.AdminParkingService;
import com.example.parking.util.DateTimeUtils;
import com.example.parking.util.FeeCalculator;
import com.example.parking.util.PageUtils;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminParkingServiceImpl implements AdminParkingService {

    private final RedisDataStore dataStore;

    public AdminParkingServiceImpl(RedisDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public PageResult<Map<String, Object>> current(String plateNumber, String payStatus, int pageNum, int pageSize) {
        List<Map<String, Object>> records = dataStore.listCurrentParkingRecords().stream()
                .filter(record -> plateNumber == null || plateNumber.isBlank()
                        || String.valueOf(record.get("plateNumber")).contains(plateNumber.trim().toUpperCase()))
                .filter(record -> payStatus == null || payStatus.isBlank()
                        || payStatus.equals(record.get("payStatus")))
                .map(record -> toParkingView(record, true))
                .collect(Collectors.toList());
        return PageUtils.page(records, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> entry(ParkingEntryRequest request) {
        return toParkingView(dataStore.createParkingEntry(request.plateNumber(), request.spaceId()), true);
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
        List<Map<String, Object>> records = dataStore.listAllParkingRecords().stream()
                .filter(record -> plateNumber == null || plateNumber.isBlank()
                        || String.valueOf(record.get("plateNumber")).contains(plateNumber.trim().toUpperCase()))
                .filter(record -> recordStatus == null || recordStatus.isBlank()
                        || recordStatus.equals(record.get("recordStatus")))
                .filter(record -> payStatus == null || payStatus.isBlank()
                        || payStatus.equals(record.get("payStatus")))
                .map(record -> toParkingView(record, false))
                .collect(Collectors.toList());
        return PageUtils.page(records, pageNum, pageSize);
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

    private Map<String, Object> toParkingView(Map<String, Object> record, boolean currentView) {
        LocalDateTime entryTime = (LocalDateTime) record.get("entryTime");
        LocalDateTime exitTime = (LocalDateTime) record.get("exitTime");
        int durationMinutes = (int) Duration.between(entryTime, exitTime == null ? LocalDateTime.now() : exitTime).toMinutes();
        BigDecimal originalAmount = FeeCalculator.calculate(durationMinutes);
        BigDecimal discountAmount = (BigDecimal) record.getOrDefault("discountAmount", BigDecimal.ZERO);
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0 || "EXEMPTED".equals(record.get("payStatus"))) {
            finalAmount = BigDecimal.ZERO;
        }

        String ownerName = "临时车辆";
        Object userId = record.get("userId");
        if (userId instanceof String userIdValue && !userIdValue.isBlank()) {
            Map<String, Object> user = dataStore.findUserById(userIdValue);
            if (user != null && user.get("realName") != null && !String.valueOf(user.get("realName")).isBlank()) {
                ownerName = String.valueOf(user.get("realName"));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.get("recordId"));
        result.put("plateNumber", record.get("plateNumber"));
        result.put("ownerName", ownerName);
        result.put("spaceCode", record.get("spaceCode"));
        result.put("entryTime", DateTimeUtils.format(entryTime));
        result.put("exitTime", DateTimeUtils.format(exitTime));
        result.put("durationMinutes", durationMinutes);
        result.put("originalAmount", originalAmount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", finalAmount);
        result.put("payStatus", record.get("payStatus"));
        result.put("recordStatus", record.get("recordStatus"));
        if (currentView) {
            result.put("active", true);
        }
        return result;
    }
}
