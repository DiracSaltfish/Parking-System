package com.example.parking.service.impl;

import com.example.parking.common.PageResult;
import com.example.parking.dto.space.FeeRuleUpdateRequest;
import com.example.parking.dto.space.ParkingSpaceSaveRequest;
import com.example.parking.service.AdminSpaceService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminSpaceServiceImpl implements AdminSpaceService {

    @Override
    public PageResult<Map<String, Object>> spaces(String status, String type, int pageNum, int pageSize) {
        List<Map<String, Object>> records = List.of(
                Map.of(
                        "spaceId", "S1021",
                        "spaceCode", "A-021",
                        "type", "NORMAL",
                        "status", "FREE",
                        "floor", "B1",
                        "remark", "靠近电梯"
                )
        );
        return new PageResult<>(records.size(), pageNum, pageSize, records);
    }

    @Override
    public Map<String, Object> createSpace(ParkingSpaceSaveRequest request) {
        return Map.of(
                "spaceId", "S2001",
                "spaceCode", request.spaceCode(),
                "type", request.type(),
                "floor", request.floor()
        );
    }

    @Override
    public Map<String, Object> updateSpace(String spaceId, ParkingSpaceSaveRequest request) {
        return Map.of(
                "spaceId", spaceId,
                "spaceCode", request.spaceCode(),
                "type", request.type(),
                "floor", request.floor(),
                "remark", request.remark() == null ? "" : request.remark()
        );
    }

    @Override
    public Map<String, Object> deleteSpace(String spaceId) {
        return Map.of(
                "spaceId", spaceId,
                "deleted", true
        );
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
}
