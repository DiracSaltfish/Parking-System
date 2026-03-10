package com.example.parking.service.impl;

import com.example.parking.dto.vehicle.VehicleBindRequest;
import com.example.parking.service.UserVehicleService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserVehicleServiceImpl implements UserVehicleService {

    @Override
    public List<Map<String, Object>> vehicles() {
        return List.of(
                Map.of(
                        "vehicleId", "V1001",
                        "plateNumber", "粤B12345",
                        "isPrimary", true,
                        "bindTime", "2026-03-10 20:00:00"
                )
        );
    }

    @Override
    public Map<String, Object> bind(VehicleBindRequest request) {
        return Map.of(
                "vehicleId", "V1002",
                "plateNumber", request.plateNumber(),
                "isPrimary", request.isPrimary()
        );
    }

    @Override
    public Map<String, Object> update(String vehicleId, VehicleBindRequest request) {
        return Map.of(
                "vehicleId", vehicleId,
                "plateNumber", request.plateNumber(),
                "isPrimary", request.isPrimary()
        );
    }

    @Override
    public Map<String, Object> delete(String vehicleId) {
        return Map.of(
                "vehicleId", vehicleId,
                "deleted", true
        );
    }
}
