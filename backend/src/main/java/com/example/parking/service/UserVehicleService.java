package com.example.parking.service;

import com.example.parking.dto.vehicle.VehicleBindRequest;
import java.util.List;
import java.util.Map;

public interface UserVehicleService {

    List<Map<String, Object>> vehicles(String authorizationHeader);

    Map<String, Object> bind(String authorizationHeader, VehicleBindRequest request);

    Map<String, Object> update(String authorizationHeader, String vehicleId, VehicleBindRequest request);

    Map<String, Object> delete(String authorizationHeader, String vehicleId);
}
