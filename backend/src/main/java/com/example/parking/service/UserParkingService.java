package com.example.parking.service;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingPaymentRequest;
import java.util.Map;

public interface UserParkingService {

    Map<String, Object> currentParking(String authorizationHeader);

    PageResult<Map<String, Object>> records(String authorizationHeader, String plateNumber, int pageNum, int pageSize);

    PageResult<Map<String, Object>> payments(String authorizationHeader, int pageNum, int pageSize);

    Map<String, Object> pay(String authorizationHeader, ParkingPaymentRequest request);
}
