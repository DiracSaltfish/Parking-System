package com.example.parking.service.impl;

import com.example.parking.service.AdminDashboardService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Override
    public Map<String, Object> summary() {
        return Map.of(
                "totalSpaces", 120,
                "freeSpaces", 48,
                "occupiedSpaces", 72,
                "todayEntryCount", 56,
                "todayExitCount", 43,
                "todayIncome", new BigDecimal("1260.00"),
                "note", "当前为骨架数据，后续接入 Redis 实时统计"
        );
    }
}
