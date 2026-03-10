package com.example.parking.service.impl;

import com.example.parking.dto.vehicle.VehicleBindRequest;
import com.example.parking.repository.RedisDataStore;
import com.example.parking.service.UserVehicleService;
import com.example.parking.util.DateTimeUtils;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserVehicleServiceImpl implements UserVehicleService {

    private final RedisDataStore dataStore;

    public UserVehicleServiceImpl(RedisDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public List<Map<String, Object>> vehicles(String authorizationHeader) {
        String userId = currentUserId(authorizationHeader);
        return dataStore.listVehiclesByUser(userId).stream()
                .map(this::toVehicleView)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> bind(String authorizationHeader, VehicleBindRequest request) {
        String userId = currentUserId(authorizationHeader);
        return toVehicleView(dataStore.bindVehicle(userId, request.plateNumber(), request.isPrimary()));
    }

    @Override
    public Map<String, Object> update(String authorizationHeader, String vehicleId, VehicleBindRequest request) {
        String userId = currentUserId(authorizationHeader);
        return toVehicleView(dataStore.updateVehicle(userId, vehicleId, request.plateNumber(), request.isPrimary()));
    }

    @Override
    public Map<String, Object> delete(String authorizationHeader, String vehicleId) {
        String userId = currentUserId(authorizationHeader);
        return dataStore.deleteVehicle(userId, vehicleId);
    }

    private String currentUserId(String authorizationHeader) {
        return (String) dataStore.resolveSession(authorizationHeader).get("userId");
    }

    private Map<String, Object> toVehicleView(Map<String, Object> vehicle) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("vehicleId", vehicle.get("vehicleId"));
        result.put("plateNumber", vehicle.get("plateNumber"));
        result.put("isPrimary", vehicle.get("isPrimary"));
        result.put("bindTime", DateTimeUtils.format((LocalDateTime) vehicle.get("bindTime")));
        return result;
    }
}
