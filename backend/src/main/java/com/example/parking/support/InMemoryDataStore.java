package com.example.parking.support;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDataStore {

    private final Map<String, Map<String, Object>> adminsByUsername = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> userIdByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> sessionRoleByToken = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserIdByToken = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Object>> vehiclesById = new ConcurrentHashMap<>();
    private final Map<String, List<String>> vehicleIdsByUserId = new ConcurrentHashMap<>();
    private final Map<String, String> plateToVehicleId = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Object>> parkingRecordsById = new ConcurrentHashMap<>();
    private final Map<String, String> currentRecordIdByPlate = new ConcurrentHashMap<>();
    private final Map<String, List<String>> recordIdsByPlate = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Object>> paymentsById = new ConcurrentHashMap<>();
    private final Map<String, List<String>> paymentIdsByUserId = new ConcurrentHashMap<>();

    private final AtomicLong userSeq = new AtomicLong(1001);
    private final AtomicLong vehicleSeq = new AtomicLong(1001);
    private final AtomicLong recordSeq = new AtomicLong(1001);
    private final AtomicLong paymentSeq = new AtomicLong(1001);

    @PostConstruct
    public void init() {
        seedAdmin();
        seedUserAndParking();
    }

    private void seedAdmin() {
        Map<String, Object> admin = new LinkedHashMap<>();
        admin.put("id", "A1001");
        admin.put("username", "admin");
        admin.put("password", "Admin123");
        admin.put("displayName", "系统管理员");
        admin.put("role", "ADMIN");
        adminsByUsername.put("admin", admin);
    }

    private void seedUserAndParking() {
        String userId = nextId("U", userSeq);
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("userId", userId);
        user.put("username", "zhangsan");
        user.put("password", "User1234");
        user.put("phone", "13800138000");
        user.put("realName", "张三");
        user.put("status", "ACTIVE");
        user.put("createTime", LocalDateTime.now().minusDays(2));
        usersById.put(userId, user);
        userIdByUsername.put("zhangsan", userId);

        Map<String, Object> vehicle = createVehicleInternal(userId, "粤B12345", true, LocalDateTime.now().minusDays(1));

        String finishedRecordId = nextId("R", recordSeq);
        Map<String, Object> finished = createParkingRecordInternal(
                finishedRecordId,
                userId,
                (String) vehicle.get("vehicleId"),
                "粤B12345",
                "A-019",
                LocalDateTime.now().minusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                "COMPLETED",
                "PAID"
        );
        finished.put("payTime", LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
        createPaymentInternal(userId, finishedRecordId, new BigDecimal("15.00"), "WECHAT",
                LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));

        String currentRecordId = nextId("R", recordSeq);
        createParkingRecordInternal(
                currentRecordId,
                userId,
                (String) vehicle.get("vehicleId"),
                "粤B12345",
                "A-021",
                LocalDateTime.now().minusHours(2).minusMinutes(25),
                null,
                "PARKING",
                "UNPAID"
        );
    }

    public synchronized Map<String, Object> findAdminByUsername(String username) {
        return cloneMap(adminsByUsername.get(username));
    }

    public synchronized Map<String, Object> findUserByUsername(String username) {
        String userId = userIdByUsername.get(username);
        return userId == null ? null : cloneMap(usersById.get(userId));
    }

    public synchronized Map<String, Object> findUserById(String userId) {
        return cloneMap(usersById.get(userId));
    }

    public synchronized Map<String, Object> registerUser(String username, String password, String phone, String realName) {
        if (userIdByUsername.containsKey(username) || adminsByUsername.containsKey(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        String userId = nextId("U", userSeq);
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("userId", userId);
        user.put("username", username);
        user.put("password", password);
        user.put("phone", phone);
        user.put("realName", realName);
        user.put("status", "ACTIVE");
        user.put("createTime", LocalDateTime.now());
        usersById.put(userId, user);
        userIdByUsername.put(username, userId);
        return cloneMap(user);
    }

    public synchronized String createSession(String role, String userId) {
        String token = role.toLowerCase() + "-token-" + userId + "-" + System.currentTimeMillis();
        sessionRoleByToken.put(token, role);
        sessionUserIdByToken.put(token, userId);
        return token;
    }

    public synchronized Map<String, Object> resolveSession(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null || !sessionRoleByToken.containsKey(token)) {
            throw new IllegalArgumentException("登录状态无效，请重新登录");
        }

        Map<String, Object> session = new LinkedHashMap<>();
        session.put("token", token);
        session.put("role", sessionRoleByToken.get(token));
        session.put("userId", sessionUserIdByToken.get(token));
        return session;
    }

    public synchronized List<Map<String, Object>> listVehiclesByUser(String userId) {
        List<String> vehicleIds = vehicleIdsByUserId.getOrDefault(userId, List.of());
        List<Map<String, Object>> result = new ArrayList<>();
        for (String vehicleId : vehicleIds) {
            result.add(cloneMap(vehiclesById.get(vehicleId)));
        }
        result.sort(Comparator.comparing(item -> (String) item.get("vehicleId")).reversed());
        return result;
    }

    public synchronized Map<String, Object> bindVehicle(String userId, String plateNumber, boolean isPrimary) {
        String existingVehicleId = plateToVehicleId.get(plateNumber);
        if (existingVehicleId != null) {
            Map<String, Object> existing = vehiclesById.get(existingVehicleId);
            if (!Objects.equals(existing.get("userId"), userId)) {
                throw new IllegalArgumentException("该车牌已被其他用户绑定");
            }
            return cloneMap(existing);
        }

        if (isPrimary) {
            clearPrimaryVehicle(userId);
        }
        return createVehicleInternal(userId, plateNumber, isPrimary, LocalDateTime.now());
    }

    public synchronized Map<String, Object> updateVehicle(String userId, String vehicleId, String plateNumber, boolean isPrimary) {
        Map<String, Object> vehicle = vehiclesById.get(vehicleId);
        if (vehicle == null || !Objects.equals(vehicle.get("userId"), userId)) {
            throw new IllegalArgumentException("车辆不存在");
        }

        String existingVehicleId = plateToVehicleId.get(plateNumber);
        if (existingVehicleId != null && !existingVehicleId.equals(vehicleId)) {
            throw new IllegalArgumentException("该车牌已被其他用户绑定");
        }

        plateToVehicleId.remove(vehicle.get("plateNumber"));
        vehicle.put("plateNumber", plateNumber);
        plateToVehicleId.put(plateNumber, vehicleId);

        if (isPrimary) {
            clearPrimaryVehicle(userId);
        }
        vehicle.put("isPrimary", isPrimary);
        return cloneMap(vehicle);
    }

    public synchronized Map<String, Object> deleteVehicle(String userId, String vehicleId) {
        Map<String, Object> vehicle = vehiclesById.get(vehicleId);
        if (vehicle == null || !Objects.equals(vehicle.get("userId"), userId)) {
            throw new IllegalArgumentException("车辆不存在");
        }

        String plateNumber = (String) vehicle.get("plateNumber");
        if (currentRecordIdByPlate.containsKey(plateNumber)) {
            throw new IllegalStateException("该车辆当前正在停车，不能删除绑定");
        }

        vehiclesById.remove(vehicleId);
        plateToVehicleId.remove(plateNumber);
        vehicleIdsByUserId.computeIfAbsent(userId, key -> new ArrayList<>()).remove(vehicleId);
        return Map.of("vehicleId", vehicleId, "deleted", true);
    }

    public synchronized Map<String, Object> findCurrentParkingByUser(String userId) {
        for (Map<String, Object> vehicle : listVehiclesByUser(userId)) {
            String plateNumber = (String) vehicle.get("plateNumber");
            String recordId = currentRecordIdByPlate.get(plateNumber);
            if (recordId != null) {
                return cloneMap(parkingRecordsById.get(recordId));
            }
        }
        return null;
    }

    public synchronized List<Map<String, Object>> listParkingRecordsByUser(String userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> vehicle : listVehiclesByUser(userId)) {
            String plateNumber = (String) vehicle.get("plateNumber");
            for (String recordId : recordIdsByPlate.getOrDefault(plateNumber, List.of())) {
                result.add(cloneMap(parkingRecordsById.get(recordId)));
            }
        }
        result.sort(Comparator.comparing(item -> (LocalDateTime) item.get("entryTime")).reversed());
        return result;
    }

    public synchronized List<Map<String, Object>> listPaymentsByUser(String userId) {
        List<String> paymentIds = paymentIdsByUserId.getOrDefault(userId, List.of());
        List<Map<String, Object>> result = new ArrayList<>();
        for (String paymentId : paymentIds) {
            result.add(cloneMap(paymentsById.get(paymentId)));
        }
        result.sort(Comparator.comparing(item -> (LocalDateTime) item.get("payTime")).reversed());
        return result;
    }

    public synchronized Map<String, Object> payCurrentRecord(String userId, String recordId, BigDecimal payAmount, String payMethod) {
        Map<String, Object> record = parkingRecordsById.get(recordId);
        if (record == null || !Objects.equals(record.get("userId"), userId)) {
            throw new IllegalArgumentException("停车记录不存在");
        }
        if ("PAID".equals(record.get("payStatus"))) {
            throw new IllegalStateException("该订单已支付");
        }
        if ("EXEMPTED".equals(record.get("payStatus"))) {
            throw new IllegalStateException("该订单已豁免，无需支付");
        }

        LocalDateTime payTime = LocalDateTime.now();
        record.put("payStatus", "PAID");
        record.put("payTime", payTime);
        record.put("finalAmount", payAmount);
        return createPaymentInternal(userId, recordId, payAmount, payMethod, payTime);
    }

    public synchronized List<Map<String, Object>> listCurrentParkingRecords() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String recordId : currentRecordIdByPlate.values()) {
            result.add(cloneMap(parkingRecordsById.get(recordId)));
        }
        result.sort(Comparator.comparing(item -> (LocalDateTime) item.get("entryTime")).reversed());
        return result;
    }

    public synchronized List<Map<String, Object>> listAllParkingRecords() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> record : parkingRecordsById.values()) {
            result.add(cloneMap(record));
        }
        result.sort(Comparator.comparing(item -> (LocalDateTime) item.get("entryTime")).reversed());
        return result;
    }

    public synchronized Map<String, Object> createParkingEntry(String plateNumber, String spaceCode) {
        String vehicleId = plateToVehicleId.get(plateNumber);
        if (currentRecordIdByPlate.containsKey(plateNumber)) {
            throw new IllegalStateException("该车辆已在场内");
        }

        String userId = null;
        if (vehicleId != null) {
            userId = (String) vehiclesById.get(vehicleId).get("userId");
        }

        String recordId = nextId("R", recordSeq);
        Map<String, Object> record = createParkingRecordInternal(
                recordId,
                userId,
                vehicleId,
                plateNumber,
                spaceCode == null || spaceCode.isBlank() ? "AUTO-" + recordId.substring(1) : spaceCode,
                LocalDateTime.now(),
                null,
                "PARKING",
                "UNPAID"
        );
        return cloneMap(record);
    }

    public synchronized Map<String, Object> completeParkingExit(String recordId) {
        Map<String, Object> record = parkingRecordsById.get(recordId);
        if (record == null) {
            throw new IllegalArgumentException("停车记录不存在");
        }
        if ("COMPLETED".equals(record.get("recordStatus"))) {
            return cloneMap(record);
        }

        record.put("exitTime", LocalDateTime.now());
        record.put("recordStatus", "COMPLETED");
        currentRecordIdByPlate.remove(record.get("plateNumber"));
        return cloneMap(record);
    }

    public synchronized Map<String, Object> applyExemption(String recordId, String exemptionType,
                                                           BigDecimal exemptionAmount, String reason) {
        Map<String, Object> record = parkingRecordsById.get(recordId);
        if (record == null) {
            throw new IllegalArgumentException("停车记录不存在");
        }

        record.put("discountType", exemptionType);
        record.put("discountAmount", exemptionAmount);
        BigDecimal originalAmount = (BigDecimal) record.get("originalAmount");
        BigDecimal finalAmount = "FULL".equals(exemptionType) ? BigDecimal.ZERO : originalAmount.subtract(exemptionAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        record.put("finalAmount", finalAmount);
        record.put("exemptReason", reason);
        record.put("payStatus", "EXEMPTED");
        record.put("operatorId", "A1001");
        record.put("operateTime", LocalDateTime.now());
        return cloneMap(record);
    }

    private Map<String, Object> createVehicleInternal(String userId, String plateNumber, boolean isPrimary, LocalDateTime bindTime) {
        String vehicleId = nextId("V", vehicleSeq);
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("vehicleId", vehicleId);
        vehicle.put("userId", userId);
        vehicle.put("plateNumber", plateNumber);
        vehicle.put("isPrimary", isPrimary);
        vehicle.put("bindTime", bindTime);
        vehiclesById.put(vehicleId, vehicle);
        vehicleIdsByUserId.computeIfAbsent(userId, key -> new ArrayList<>()).add(vehicleId);
        plateToVehicleId.put(plateNumber, vehicleId);
        return cloneMap(vehicle);
    }

    private Map<String, Object> createParkingRecordInternal(String recordId, String userId, String vehicleId,
                                                            String plateNumber, String spaceCode,
                                                            LocalDateTime entryTime, LocalDateTime exitTime,
                                                            String recordStatus, String payStatus) {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("recordId", recordId);
        record.put("userId", userId);
        record.put("vehicleId", vehicleId);
        record.put("plateNumber", plateNumber);
        record.put("spaceCode", spaceCode);
        record.put("entryTime", entryTime);
        record.put("exitTime", exitTime);
        record.put("recordStatus", recordStatus);
        record.put("payStatus", payStatus);
        record.put("originalAmount", BigDecimal.ZERO);
        record.put("discountAmount", BigDecimal.ZERO);
        record.put("finalAmount", BigDecimal.ZERO);
        record.put("discountType", "");
        record.put("exemptReason", "");
        record.put("operatorId", "");
        parkingRecordsById.put(recordId, record);
        recordIdsByPlate.computeIfAbsent(plateNumber, key -> new ArrayList<>()).add(recordId);
        if ("PARKING".equals(recordStatus)) {
            currentRecordIdByPlate.put(plateNumber, recordId);
        }
        return cloneMap(record);
    }

    private Map<String, Object> createPaymentInternal(String userId, String recordId, BigDecimal payAmount,
                                                      String payMethod, LocalDateTime payTime) {
        String paymentId = nextId("P", paymentSeq);
        Map<String, Object> payment = new LinkedHashMap<>();
        payment.put("paymentId", paymentId);
        payment.put("recordId", recordId);
        payment.put("userId", userId);
        payment.put("payMethod", payMethod);
        payment.put("payAmount", payAmount);
        payment.put("payStatus", "PAID");
        payment.put("payTime", payTime);
        paymentsById.put(paymentId, payment);
        paymentIdsByUserId.computeIfAbsent(userId, key -> new ArrayList<>()).add(paymentId);
        return cloneMap(payment);
    }

    private void clearPrimaryVehicle(String userId) {
        for (String vehicleId : vehicleIdsByUserId.getOrDefault(userId, List.of())) {
            Map<String, Object> vehicle = vehiclesById.get(vehicleId);
            if (vehicle != null) {
                vehicle.put("isPrimary", false);
            }
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        return authorizationHeader.replace("Bearer", "").trim();
    }

    private Map<String, Object> cloneMap(Map<String, Object> source) {
        return source == null ? null : new LinkedHashMap<>(source);
    }

    private String nextId(String prefix, AtomicLong sequence) {
        return prefix + sequence.getAndIncrement();
    }
}
