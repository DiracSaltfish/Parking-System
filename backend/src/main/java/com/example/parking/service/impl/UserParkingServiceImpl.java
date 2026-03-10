package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingPaymentRequest;
import com.example.parking.service.UserParkingService;
import com.example.parking.support.InMemoryDataStore;
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
public class UserParkingServiceImpl implements UserParkingService {

    private final InMemoryDataStore dataStore;

    public UserParkingServiceImpl(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Map<String, Object> currentParking(String authorizationHeader) {
        String userId = currentUserId(authorizationHeader);
        Map<String, Object> record = dataStore.findCurrentParkingByUser(userId);
        if (record == null) {
            return Map.of(
                    "active", false,
                    "message", "当前没有在场车辆"
            );
        }
        return toParkingView(record, true);
    }

    @Override
    public PageResult<Map<String, Object>> records(String authorizationHeader, String plateNumber, int pageNum, int pageSize) {
        String userId = currentUserId(authorizationHeader);
        List<Map<String, Object>> records = dataStore.listParkingRecordsByUser(userId).stream()
                .filter(record -> plateNumber == null || plateNumber.isBlank()
                        || ((String) record.get("plateNumber")).contains(plateNumber))
                .map(record -> toParkingView(record, false))
                .collect(Collectors.toList());
        return PageUtils.page(records, pageNum, pageSize);
    }

    @Override
    public PageResult<Map<String, Object>> payments(String authorizationHeader, int pageNum, int pageSize) {
        String userId = currentUserId(authorizationHeader);
        List<Map<String, Object>> records = dataStore.listPaymentsByUser(userId).stream()
                .map(this::toPaymentView)
                .collect(Collectors.toList());
        return PageUtils.page(records, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> pay(String authorizationHeader, ParkingPaymentRequest request) {
        String userId = currentUserId(authorizationHeader);
        List<Map<String, Object>> records = dataStore.listParkingRecordsByUser(userId);
        Map<String, Object> record = records.stream()
                .filter(item -> request.recordId().equals(item.get("recordId")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("停车记录不存在"));
        BigDecimal finalAmount = (BigDecimal) toParkingView(record, true).get("finalAmount");
        return toPaymentView(dataStore.payCurrentRecord(userId, request.recordId(), finalAmount, request.payMethod()));
    }

    private String currentUserId(String authorizationHeader) {
        return (String) dataStore.resolveSession(authorizationHeader).get("userId");
    }

    private Map<String, Object> toParkingView(Map<String, Object> record, boolean includeMessage) {
        LocalDateTime entryTime = (LocalDateTime) record.get("entryTime");
        LocalDateTime exitTime = (LocalDateTime) record.get("exitTime");
        int durationMinutes = (int) Duration.between(entryTime, exitTime == null ? LocalDateTime.now() : exitTime).toMinutes();
        BigDecimal originalAmount = FeeCalculator.calculate(durationMinutes);
        BigDecimal discountAmount = (BigDecimal) record.getOrDefault("discountAmount", BigDecimal.ZERO);
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        if ("EXEMPTED".equals(record.get("payStatus"))) {
            finalAmount = BigDecimal.ZERO;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.get("recordId"));
        result.put("plateNumber", record.get("plateNumber"));
        result.put("spaceCode", record.get("spaceCode"));
        result.put("entryTime", DateTimeUtils.format(entryTime));
        result.put("exitTime", DateTimeUtils.format(exitTime));
        result.put("durationMinutes", durationMinutes);
        result.put("originalAmount", originalAmount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", finalAmount);
        result.put("payStatus", record.get("payStatus"));
        result.put("recordStatus", record.get("recordStatus"));
        if (includeMessage) {
            result.put("active", true);
        }
        return result;
    }

    private Map<String, Object> toPaymentView(Map<String, Object> payment) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentId", payment.get("paymentId"));
        result.put("recordId", payment.get("recordId"));
        result.put("payAmount", payment.get("payAmount"));
        result.put("payMethod", payment.get("payMethod"));
        result.put("payStatus", payment.get("payStatus"));
        result.put("payTime", DateTimeUtils.format((LocalDateTime) payment.get("payTime")));
        return result;
    }
}
