package com.example.parking.service;

import com.example.parking.common.PageResult;
import com.example.parking.dto.parking.ParkingPaymentRequest;
import java.util.Map;

public interface UserParkingService {

    Map<String, Object> currentParking();

    PageResult<Map<String, Object>> records(String plateNumber, int pageNum, int pageSize);

    PageResult<Map<String, Object>> payments(int pageNum, int pageSize);

    Map<String, Object> pay(ParkingPaymentRequest request);
}
