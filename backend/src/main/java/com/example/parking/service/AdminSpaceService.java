package com.example.parking.service;

import com.example.parking.common.PageResult;
import com.example.parking.dto.space.FeeRuleUpdateRequest;
import com.example.parking.dto.space.ParkingSpaceSaveRequest;
import java.util.Map;

public interface AdminSpaceService {

    PageResult<Map<String, Object>> spaces(String status, String type, int pageNum, int pageSize);

    Map<String, Object> createSpace(ParkingSpaceSaveRequest request);

    Map<String, Object> updateSpace(String spaceId, ParkingSpaceSaveRequest request);

    Map<String, Object> deleteSpace(String spaceId);

    Map<String, Object> feeRule();

    Map<String, Object> updateFeeRule(FeeRuleUpdateRequest request);
}
