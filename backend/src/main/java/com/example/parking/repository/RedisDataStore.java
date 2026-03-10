package com.example.parking.repository;

import com.example.parking.util.DateTimeUtils;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDataStore {

    private static final String SEQ_USER = "seq:user";
    private static final String SEQ_VEHICLE = "seq:vehicle";
    private static final String SEQ_RECORD = "seq:record";
    private static final String SEQ_PAYMENT = "seq:payment";

    private static final String ADMIN_ID = "A1001";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin123";
    private static final String USER_USERNAME = "zhangsan";
    private static final String USER_PASSWORD = "User1234";

    private final StringRedisTemplate redisTemplate;

    public RedisDataStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        seedBaseDataIfAbsent();
    }

    public Map<String, Object> findAdminByUsername(String username) {
        String adminId = redisTemplate.opsForValue().get(adminUsernameKey(username));
        return adminId == null ? null : getAdmin(adminId);
    }

    public Map<String, Object> findUserByUsername(String username) {
        String userId = redisTemplate.opsForValue().get(userUsernameKey(username));
        return userId == null ? null : getUser(userId);
    }

    public Map<String, Object> findUserById(String userId) {
        return getUser(userId);
    }

    public Map<String, Object> registerUser(String username, String password, String phone, String realName) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userUsernameKey(username)))
                || Boolean.TRUE.equals(redisTemplate.hasKey(adminUsernameKey(username)))) {
            throw new IllegalArgumentException("用户名已存在");
        }

        String userId = nextId("U", SEQ_USER);
        Map<String, String> user = new LinkedHashMap<>();
        user.put("userId", userId);
        user.put("username", username);
        user.put("password", password);
        user.put("phone", phone);
        user.put("realName", realName);
        user.put("status", "ACTIVE");
        user.put("createTime", DateTimeUtils.format(LocalDateTime.now()));
        redisTemplate.opsForHash().putAll(userKey(userId), user);
        redisTemplate.opsForValue().set(userUsernameKey(username), userId);
        return toUserMap(user);
    }

    public String createSession(String role, String userId) {
        String token = role.toLowerCase() + "-token-" + userId + "-" + System.currentTimeMillis();
        Map<String, String> session = new LinkedHashMap<>();
        session.put("token", token);
        session.put("role", role);
        session.put("userId", userId);
        session.put("issuedAt", DateTimeUtils.format(LocalDateTime.now()));
        redisTemplate.opsForHash().putAll(sessionKey(token), session);
        redisTemplate.expire(sessionKey(token), java.time.Duration.ofHours(12));
        return token;
    }

    public Map<String, Object> resolveSession(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null) {
            throw new IllegalArgumentException("登录状态无效，请重新登录");
        }

        Map<Object, Object> session = redisTemplate.opsForHash().entries(sessionKey(token));
        if (session.isEmpty()) {
            throw new IllegalArgumentException("登录状态无效，请重新登录");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("role", value(session, "role"));
        result.put("userId", value(session, "userId"));
        return result;
    }

    public List<Map<String, Object>> listVehiclesByUser(String userId) {
        Set<String> vehicleIds = redisTemplate.opsForSet().members(userVehiclesKey(userId));
        List<Map<String, Object>> vehicles = new ArrayList<>();
        if (vehicleIds != null) {
            for (String vehicleId : vehicleIds) {
                Map<String, Object> vehicle = getVehicle(vehicleId);
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        }
        vehicles.sort(Comparator.comparing((Map<String, Object> item) -> (LocalDateTime) item.get("bindTime")).reversed());
        return vehicles;
    }

    public Map<String, Object> bindVehicle(String userId, String plateNumber, boolean isPrimary) {
        String existingVehicleId = redisTemplate.opsForValue().get(vehiclePlateKey(plateNumber));
        if (existingVehicleId != null) {
            Map<String, Object> existing = getVehicle(existingVehicleId);
            if (!Objects.equals(existing.get("userId"), userId)) {
                throw new IllegalArgumentException("该车牌已被其他用户绑定");
            }
            return existing;
        }

        if (isPrimary) {
            clearPrimaryVehicle(userId);
        }

        String vehicleId = nextId("V", SEQ_VEHICLE);
        Map<String, String> vehicle = new LinkedHashMap<>();
        vehicle.put("vehicleId", vehicleId);
        vehicle.put("userId", userId);
        vehicle.put("plateNumber", plateNumber);
        vehicle.put("isPrimary", String.valueOf(isPrimary));
        vehicle.put("bindTime", DateTimeUtils.format(LocalDateTime.now()));
        redisTemplate.opsForHash().putAll(vehicleKey(vehicleId), vehicle);
        redisTemplate.opsForSet().add(userVehiclesKey(userId), vehicleId);
        redisTemplate.opsForValue().set(vehiclePlateKey(plateNumber), vehicleId);
        return toVehicleMap(vehicle);
    }

    public Map<String, Object> updateVehicle(String userId, String vehicleId, String plateNumber, boolean isPrimary) {
        Map<String, Object> vehicle = getVehicle(vehicleId);
        if (vehicle == null || !Objects.equals(vehicle.get("userId"), userId)) {
            throw new IllegalArgumentException("车辆不存在");
        }

        String existingVehicleId = redisTemplate.opsForValue().get(vehiclePlateKey(plateNumber));
        if (existingVehicleId != null && !existingVehicleId.equals(vehicleId)) {
            throw new IllegalArgumentException("该车牌已被其他用户绑定");
        }

        String oldPlate = (String) vehicle.get("plateNumber");
        if (!oldPlate.equals(plateNumber)) {
            redisTemplate.delete(vehiclePlateKey(oldPlate));
            redisTemplate.opsForValue().set(vehiclePlateKey(plateNumber), vehicleId);
        }

        if (isPrimary) {
            clearPrimaryVehicle(userId);
        }

        redisTemplate.opsForHash().put(vehicleKey(vehicleId), "plateNumber", plateNumber);
        redisTemplate.opsForHash().put(vehicleKey(vehicleId), "isPrimary", String.valueOf(isPrimary));
        return getVehicle(vehicleId);
    }

    public Map<String, Object> deleteVehicle(String userId, String vehicleId) {
        Map<String, Object> vehicle = getVehicle(vehicleId);
        if (vehicle == null || !Objects.equals(vehicle.get("userId"), userId)) {
            throw new IllegalArgumentException("车辆不存在");
        }

        String plateNumber = (String) vehicle.get("plateNumber");
        if (Boolean.TRUE.equals(redisTemplate.hasKey(currentRecordPlateKey(plateNumber)))) {
            throw new IllegalStateException("该车辆当前正在停车，不能删除绑定");
        }

        redisTemplate.delete(vehicleKey(vehicleId));
        redisTemplate.opsForSet().remove(userVehiclesKey(userId), vehicleId);
        redisTemplate.delete(vehiclePlateKey(plateNumber));
        return Map.of("vehicleId", vehicleId, "deleted", true);
    }

    public Map<String, Object> findCurrentParkingByUser(String userId) {
        for (Map<String, Object> vehicle : listVehiclesByUser(userId)) {
            String recordId = redisTemplate.opsForValue().get(currentRecordPlateKey((String) vehicle.get("plateNumber")));
            if (recordId != null) {
                return getParkingRecord(recordId);
            }
        }
        return null;
    }

    public List<Map<String, Object>> listParkingRecordsByUser(String userId) {
        List<String> recordIds = redisTemplate.opsForList().range(userRecordsKey(userId), 0, -1);
        List<Map<String, Object>> records = new ArrayList<>();
        if (recordIds != null) {
            for (String recordId : recordIds) {
                Map<String, Object> record = getParkingRecord(recordId);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        records.sort(Comparator.comparing((Map<String, Object> item) -> (LocalDateTime) item.get("entryTime")).reversed());
        return records;
    }

    public List<Map<String, Object>> listPaymentsByUser(String userId) {
        List<String> paymentIds = redisTemplate.opsForList().range(userPaymentsKey(userId), 0, -1);
        List<Map<String, Object>> payments = new ArrayList<>();
        if (paymentIds != null) {
            for (String paymentId : paymentIds) {
                Map<String, Object> payment = getPayment(paymentId);
                if (payment != null) {
                    payments.add(payment);
                }
            }
        }
        payments.sort(Comparator.comparing((Map<String, Object> item) -> (LocalDateTime) item.get("payTime")).reversed());
        return payments;
    }

    public Map<String, Object> payCurrentRecord(String userId, String recordId, BigDecimal payAmount, String payMethod) {
        Map<String, Object> record = getParkingRecord(recordId);
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
        redisTemplate.opsForHash().put(parkingRecordKey(recordId), "payStatus", "PAID");
        redisTemplate.opsForHash().put(parkingRecordKey(recordId), "payTime", DateTimeUtils.format(payTime));
        redisTemplate.opsForHash().put(parkingRecordKey(recordId), "finalAmount", payAmount.toPlainString());

        String paymentId = nextId("P", SEQ_PAYMENT);
        Map<String, String> payment = new LinkedHashMap<>();
        payment.put("paymentId", paymentId);
        payment.put("recordId", recordId);
        payment.put("userId", userId);
        payment.put("payMethod", payMethod);
        payment.put("payAmount", payAmount.toPlainString());
        payment.put("payStatus", "PAID");
        payment.put("payTime", DateTimeUtils.format(payTime));
        redisTemplate.opsForHash().putAll(paymentKey(paymentId), payment);
        redisTemplate.opsForList().leftPush(userPaymentsKey(userId), paymentId);
        return toPaymentMap(payment);
    }

    public List<Map<String, Object>> listCurrentParkingRecords() {
        Set<String> currentKeys = redisTemplate.keys("parking:current:plate:*");
        List<Map<String, Object>> records = new ArrayList<>();
        if (currentKeys != null) {
            for (String currentKey : currentKeys) {
                String recordId = redisTemplate.opsForValue().get(currentKey);
                if (recordId == null) {
                    continue;
                }
                Map<String, Object> record = getParkingRecord(recordId);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        records.sort(Comparator.comparing((Map<String, Object> item) -> (LocalDateTime) item.get("entryTime")).reversed());
        return records;
    }

    public List<Map<String, Object>> listAllParkingRecords() {
        Set<String> recordKeys = redisTemplate.keys("parking:record:*");
        List<Map<String, Object>> records = new ArrayList<>();
        if (recordKeys != null) {
            for (String recordKey : recordKeys) {
                if (!recordKey.matches("parking:record:R\\d+")) {
                    continue;
                }
                String recordId = recordKey.substring("parking:record:".length());
                Map<String, Object> record = getParkingRecord(recordId);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        records.sort(Comparator.comparing((Map<String, Object> item) -> (LocalDateTime) item.get("entryTime")).reversed());
        return records;
    }

    public Map<String, Object> createParkingEntry(String plateNumber, String spaceCode) {
        String normalizedPlate = plateNumber == null ? "" : plateNumber.trim().toUpperCase();
        if (normalizedPlate.isBlank()) {
            throw new IllegalArgumentException("车牌号不能为空");
        }
        if (Boolean.TRUE.equals(redisTemplate.hasKey(currentRecordPlateKey(normalizedPlate)))) {
            throw new IllegalStateException("该车辆已在场内");
        }

        String vehicleId = redisTemplate.opsForValue().get(vehiclePlateKey(normalizedPlate));
        String userId = null;
        if (vehicleId != null) {
            Map<String, Object> vehicle = getVehicle(vehicleId);
            if (vehicle != null) {
                userId = (String) vehicle.get("userId");
            }
        }

        String recordId = nextId("R", SEQ_RECORD);
        String normalizedSpaceCode = spaceCode == null ? "" : spaceCode.trim().toUpperCase();
        return createParkingRecord(
                recordId,
                userId,
                vehicleId,
                normalizedPlate,
                normalizedSpaceCode.isBlank() ? "AUTO-" + recordId.substring(1) : normalizedSpaceCode,
                LocalDateTime.now(),
                null,
                "PARKING",
                "UNPAID"
        );
    }

    private void seedBaseDataIfAbsent() {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(adminUsernameKey(ADMIN_USERNAME)))) {
            return;
        }

        Map<String, String> admin = new LinkedHashMap<>();
        admin.put("id", ADMIN_ID);
        admin.put("username", ADMIN_USERNAME);
        admin.put("password", ADMIN_PASSWORD);
        admin.put("displayName", "系统管理员");
        admin.put("role", "ADMIN");
        redisTemplate.opsForHash().putAll(adminKey(ADMIN_ID), admin);
        redisTemplate.opsForValue().set(adminUsernameKey(ADMIN_USERNAME), ADMIN_ID);

        Map<String, Object> user = registerUser(USER_USERNAME, USER_PASSWORD, "13800138000", "张三");
        String userId = (String) user.get("userId");
        Map<String, Object> vehicle = bindVehicle(userId, "粤B12345", true);
        String vehicleId = (String) vehicle.get("vehicleId");

        String finishedRecordId = nextId("R", SEQ_RECORD);
        createParkingRecord(finishedRecordId, userId, vehicleId, "粤B12345", "A-019",
                LocalDateTime.now().minusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                "COMPLETED", "UNPAID");
        payCurrentRecord(userId, finishedRecordId, new BigDecimal("15.00"), "WECHAT");

        String currentRecordId = nextId("R", SEQ_RECORD);
        createParkingRecord(currentRecordId, userId, vehicleId, "粤B12345", "A-021",
                LocalDateTime.now().minusHours(2).minusMinutes(25),
                null, "PARKING", "UNPAID");
    }

    private Map<String, Object> createParkingRecord(String recordId, String userId, String vehicleId, String plateNumber,
                                                    String spaceCode, LocalDateTime entryTime, LocalDateTime exitTime,
                                                    String recordStatus, String payStatus) {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("recordId", recordId);
        record.put("userId", userId == null ? "" : userId);
        record.put("vehicleId", vehicleId == null ? "" : vehicleId);
        record.put("plateNumber", plateNumber);
        record.put("spaceCode", spaceCode);
        record.put("entryTime", DateTimeUtils.format(entryTime));
        record.put("exitTime", DateTimeUtils.format(exitTime));
        record.put("recordStatus", recordStatus);
        record.put("payStatus", payStatus);
        record.put("originalAmount", "0.00");
        record.put("discountAmount", "0.00");
        record.put("finalAmount", "0.00");
        record.put("discountType", "");
        record.put("exemptReason", "");
        record.put("operatorId", "");
        record.put("payTime", "");
        redisTemplate.opsForHash().putAll(parkingRecordKey(recordId), record);
        if (userId != null && !userId.isBlank()) {
            redisTemplate.opsForList().leftPush(userRecordsKey(userId), recordId);
        }
        if ("PARKING".equals(recordStatus)) {
            redisTemplate.opsForValue().set(currentRecordPlateKey(plateNumber), recordId);
        }
        return toParkingRecordMap(record);
    }

    private void clearPrimaryVehicle(String userId) {
        for (Map<String, Object> vehicle : listVehiclesByUser(userId)) {
            redisTemplate.opsForHash().put(vehicleKey((String) vehicle.get("vehicleId")), "isPrimary", "false");
        }
    }

    private Map<String, Object> getAdmin(String adminId) {
        Map<Object, Object> admin = redisTemplate.opsForHash().entries(adminKey(adminId));
        if (admin.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", value(admin, "id"));
        result.put("username", value(admin, "username"));
        result.put("password", value(admin, "password"));
        result.put("displayName", value(admin, "displayName"));
        result.put("role", value(admin, "role"));
        return result;
    }

    private Map<String, Object> getUser(String userId) {
        Map<Object, Object> user = redisTemplate.opsForHash().entries(userKey(userId));
        if (user.isEmpty()) {
            return null;
        }
        return toUserMap(user);
    }

    private Map<String, Object> getVehicle(String vehicleId) {
        Map<Object, Object> vehicle = redisTemplate.opsForHash().entries(vehicleKey(vehicleId));
        if (vehicle.isEmpty()) {
            return null;
        }
        return toVehicleMap(vehicle);
    }

    private Map<String, Object> getParkingRecord(String recordId) {
        Map<Object, Object> record = redisTemplate.opsForHash().entries(parkingRecordKey(recordId));
        if (record.isEmpty()) {
            return null;
        }
        return toParkingRecordMap(record);
    }

    private Map<String, Object> getPayment(String paymentId) {
        Map<Object, Object> payment = redisTemplate.opsForHash().entries(paymentKey(paymentId));
        if (payment.isEmpty()) {
            return null;
        }
        return toPaymentMap(payment);
    }

    private Map<String, Object> toUserMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", value(source, "userId"));
        result.put("username", value(source, "username"));
        result.put("password", value(source, "password"));
        result.put("phone", value(source, "phone"));
        result.put("realName", value(source, "realName"));
        result.put("status", value(source, "status"));
        result.put("createTime", DateTimeUtils.parse(value(source, "createTime")));
        return result;
    }

    private Map<String, Object> toVehicleMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("vehicleId", value(source, "vehicleId"));
        result.put("userId", value(source, "userId"));
        result.put("plateNumber", value(source, "plateNumber"));
        result.put("isPrimary", Boolean.parseBoolean(value(source, "isPrimary")));
        result.put("bindTime", DateTimeUtils.parse(value(source, "bindTime")));
        return result;
    }

    private Map<String, Object> toParkingRecordMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", value(source, "recordId"));
        result.put("userId", emptyToNull(value(source, "userId")));
        result.put("vehicleId", emptyToNull(value(source, "vehicleId")));
        result.put("plateNumber", value(source, "plateNumber"));
        result.put("spaceCode", value(source, "spaceCode"));
        result.put("entryTime", DateTimeUtils.parse(value(source, "entryTime")));
        result.put("exitTime", DateTimeUtils.parse(value(source, "exitTime")));
        result.put("recordStatus", value(source, "recordStatus"));
        result.put("payStatus", value(source, "payStatus"));
        result.put("originalAmount", toDecimal(value(source, "originalAmount")));
        result.put("discountAmount", toDecimal(value(source, "discountAmount")));
        result.put("finalAmount", toDecimal(value(source, "finalAmount")));
        result.put("discountType", value(source, "discountType"));
        result.put("exemptReason", value(source, "exemptReason"));
        result.put("operatorId", value(source, "operatorId"));
        result.put("payTime", DateTimeUtils.parse(value(source, "payTime")));
        return result;
    }

    private Map<String, Object> toPaymentMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentId", value(source, "paymentId"));
        result.put("recordId", value(source, "recordId"));
        result.put("userId", value(source, "userId"));
        result.put("payMethod", value(source, "payMethod"));
        result.put("payAmount", toDecimal(value(source, "payAmount")));
        result.put("payStatus", value(source, "payStatus"));
        result.put("payTime", DateTimeUtils.parse(value(source, "payTime")));
        return result;
    }

    private String nextId(String prefix, String sequenceKey) {
        Long number = redisTemplate.opsForValue().increment(sequenceKey);
        if (number == null) {
            throw new IllegalStateException("生成主键失败");
        }
        return prefix + (1000 + number);
    }

    private BigDecimal toDecimal(String value) {
        return (value == null || value.isBlank()) ? BigDecimal.ZERO : new BigDecimal(value);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        return authorizationHeader.replace("Bearer", "").trim();
    }

    private String value(Map<?, ?> source, String key) {
        Object value = source.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private String adminKey(String adminId) {
        return "admin:" + adminId;
    }

    private String adminUsernameKey(String username) {
        return "admin:username:" + username;
    }

    private String userKey(String userId) {
        return "user:" + userId;
    }

    private String userUsernameKey(String username) {
        return "user:username:" + username;
    }

    private String sessionKey(String token) {
        return "auth:token:" + token;
    }

    private String vehicleKey(String vehicleId) {
        return "user:vehicle:" + vehicleId;
    }

    private String userVehiclesKey(String userId) {
        return "user:" + userId + ":vehicles";
    }

    private String vehiclePlateKey(String plateNumber) {
        return "vehicle:plate:" + plateNumber;
    }

    private String parkingRecordKey(String recordId) {
        return "parking:record:" + recordId;
    }

    private String currentRecordPlateKey(String plateNumber) {
        return "parking:current:plate:" + plateNumber;
    }

    private String userRecordsKey(String userId) {
        return "user:" + userId + ":records";
    }

    private String paymentKey(String paymentId) {
        return "payment:" + paymentId;
    }

    private String userPaymentsKey(String userId) {
        return "user:" + userId + ":payments";
    }
}
