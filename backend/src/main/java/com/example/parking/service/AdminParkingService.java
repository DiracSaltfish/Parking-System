package com.example.parking.service;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingEntryRequest;
import com.example.parking.dto.parking.ParkingExemptionRequest;
import com.example.parking.dto.parking.ParkingExitRequest;
import java.util.Map;

public interface AdminParkingService {

    PageResult<Map<String, Object>> current(String plateNumber, String payStatus, int pageNum, int pageSize);

    Map<String, Object> entry(ParkingEntryRequest request);

    Map<String, Object> exit(ParkingExitRequest request);

    PageResult<Map<String, Object>> records(String plateNumber, String recordStatus, String payStatus, int pageNum, int pageSize);

    Map<String, Object> exempt(ParkingExemptionRequest request);

    Map<String, Object> exemptionDetail(String recordId);
}
