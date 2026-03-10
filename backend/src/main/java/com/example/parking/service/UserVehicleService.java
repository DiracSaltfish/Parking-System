package com.example.parking.service;

import com.example.parking.dto.vehicle.VehicleBindRequest;
import java.util.List;
import java.util.Map;

public interface UserVehicleService {

    List<Map<String, Object>> vehicles();

    Map<String, Object> bind(VehicleBindRequest request);

    Map<String, Object> update(String vehicleId, VehicleBindRequest request);

    Map<String, Object> delete(String vehicleId);
}
