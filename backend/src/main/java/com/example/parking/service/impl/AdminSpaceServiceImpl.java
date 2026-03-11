package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.space.FeeRuleUpdateRequest;
import com.example.parking.dto.space.ParkingSpaceSaveRequest;
import com.example.parking.repository.RedisDataStore;
import com.example.parking.service.AdminSpaceService;
import com.example.parking.util.PageUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminSpaceServiceImpl implements AdminSpaceService {

    private final RedisDataStore dataStore;

    public AdminSpaceServiceImpl(RedisDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public PageResult<Map<String, Object>> spaces(String status, String type, int pageNum, int pageSize) {
        List<Map<String, Object>> records = dataStore.listSpaces().stream()
                .filter(space -> status == null || status.isBlank() || status.equals(space.get("status")))
                .filter(space -> type == null || type.isBlank() || type.equals(space.get("type")))
                .map(this::toSpaceView)
                .collect(Collectors.toList());
        return PageUtils.page(records, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> createSpace(ParkingSpaceSaveRequest request) {
        return toSpaceView(dataStore.createSpace(request.spaceCode(), request.type(), request.floor(), request.remark()));
    }

    @Override
    public Map<String, Object> updateSpace(String spaceId, ParkingSpaceSaveRequest request) {
        return toSpaceView(dataStore.updateSpace(spaceId, request.spaceCode(), request.type(), request.floor(), request.remark()));
    }

    @Override
    public Map<String, Object> deleteSpace(String spaceId) {
        return dataStore.deleteSpace(spaceId);
    }

    @Override
    public Map<String, Object> feeRule() {
        return Map.of(
                "freeMinutes", 30,
                "pricePerHour", new BigDecimal("5.00"),
                "dailyMaxAmount", new BigDecimal("30.00")
        );
    }

    @Override
    public Map<String, Object> updateFeeRule(FeeRuleUpdateRequest request) {
        return Map.of(
                "freeMinutes", request.freeMinutes(),
                "pricePerHour", request.pricePerHour(),
                "dailyMaxAmount", request.dailyMaxAmount()
        );
    }

    private Map<String, Object> toSpaceView(Map<String, Object> space) {
        return Map.of(
                "spaceId", space.get("spaceId"),
                "spaceCode", space.get("spaceCode"),
                "type", space.get("type"),
                "status", space.get("status"),
                "floor", space.get("floor"),
                "remark", space.get("remark")
        );
    }
}
