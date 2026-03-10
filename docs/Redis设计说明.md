# Redis 设计说明

## 1. 说明

Redis 没有传统关系型数据库里的“表结构”概念，这个项目采用的是：

- `Key 命名规范`
- `Value 数据类型`
- `Hash 字段约定`
- `Set/List` 关系索引
- `String` 自增序列

当前文档以仓库中的实际实现为准，对应代码：
[RedisDataStore.java](/Users/ellis/Desktop/停车场管理/backend/src/main/java/com/example/parking/repository/RedisDataStore.java)

---

## 2. 默认连接配置

默认本机 Redis 配置位于：
[application.yml](/Users/ellis/Desktop/停车场管理/backend/src/main/resources/application.yml)

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
```

如果要切换到远程 Redis，只需要修改 `host / port / password / database`。

---

## 3. Key 结构总览

### 3.1 序列号

```text
seq:user        -> String，自增，用户编号序列
seq:vehicle     -> String，自增，车辆编号序列
seq:record      -> String，自增，停车记录编号序列
seq:payment     -> String，自增，支付编号序列
```

用途：

- 通过 `INCR` 生成业务主键
- 生成格式如 `U1001`、`V1001`、`R1001`、`P1001`

### 3.2 管理员

```text
admin:{adminId}                 -> Hash
admin:username:{username}       -> String
```

`admin:{adminId}` 字段：

```text
id
username
password
displayName
role
```

示例：

```text
admin:A1001
admin:username:admin -> A1001
```

### 3.3 普通用户

```text
user:{userId}                   -> Hash
user:username:{username}        -> String
```

`user:{userId}` 字段：

```text
userId
username
password
phone
realName
status
createTime
```

示例：

```text
user:U1001
user:username:zhangsan -> U1001
```

### 3.4 登录会话

```text
auth:token:{token}              -> Hash
```

`auth:token:{token}` 字段：

```text
token
role
userId
issuedAt
```

规则：

- TTL 为 `12 小时`
- 当前项目使用 Bearer Token

### 3.5 用户绑定车辆

```text
user:vehicle:{vehicleId}        -> Hash
user:{userId}:vehicles          -> Set
vehicle:plate:{plateNumber}     -> String
```

`user:vehicle:{vehicleId}` 字段：

```text
vehicleId
userId
plateNumber
isPrimary
bindTime
```

关系说明：

- `user:{userId}:vehicles` 存某个用户绑定的车辆 ID 集合
- `vehicle:plate:{plateNumber}` 用于按车牌快速反查 `vehicleId`

### 3.6 停车记录

```text
parking:record:{recordId}           -> Hash
parking:current:plate:{plateNumber} -> String
user:{userId}:records               -> List
```

`parking:record:{recordId}` 字段：

```text
recordId
userId
vehicleId
plateNumber
spaceCode
entryTime
exitTime
recordStatus
payStatus
originalAmount
discountAmount
finalAmount
discountType
exemptReason
operatorId
payTime
```

关系说明：

- `parking:current:plate:{plateNumber}` 指向当前在场记录 ID
- `user:{userId}:records` 保存该用户的停车记录列表

### 3.7 支付记录

```text
payment:{paymentId}             -> Hash
user:{userId}:payments          -> List
```

`payment:{paymentId}` 字段：

```text
paymentId
recordId
userId
payMethod
payAmount
payStatus
payTime
```

关系说明：

- `user:{userId}:payments` 保存用户支付记录列表

---

## 4. 当前种子数据

Spring Boot 启动时会在 Redis 中自动补基础演示数据，前提是系统首次启动且不存在：

```text
admin:username:admin
```

当前默认演示账号：

- 管理员：`admin / Admin123`
- 普通用户：`zhangsan / User1234`

同时会自动生成：

- 1 个普通用户
- 1 台已绑定车辆 `粤B12345`
- 1 条已完成并已支付的历史停车记录
- 1 条正在停车中的当前记录

---

## 5. 数据类型约定

为了便于前后端展示，当前 Redis 内部统一按字符串存储，读取后在 Java 中转换：

- 时间字段：Redis 存字符串，Java 转 `LocalDateTime`
- 金额字段：Redis 存字符串，Java 转 `BigDecimal`
- 布尔字段：Redis 存 `true/false`，Java 转 `Boolean`

好处：

- 结构简单，便于直接用 `redis-cli` 排查
- 避免 JSON 序列化配置复杂化
- 和 `StringRedisTemplate` 搭配直接

---

## 6. 推荐的查看命令

查看当前 key 总数：

```bash
redis-cli DBSIZE
```

查看所有会话：

```bash
redis-cli KEYS 'auth:token:*'
```

查看某个用户：

```bash
redis-cli HGETALL user:U1001
```

查看某个车辆：

```bash
redis-cli HGETALL user:vehicle:V1001
```

查看某条停车记录：

```bash
redis-cli HGETALL parking:record:R1001
```

查看某用户的支付列表：

```bash
redis-cli LRANGE user:U1001:payments 0 -1
```

---

## 7. 当前已接入 Redis 的业务

当前已经接入 Redis 的业务包括：

- 管理员登录
- 普通用户注册 / 登录
- Token 会话存储
- 用户绑定车辆
- 用户当前停车查询
- 用户停车记录查询
- 用户支付记录查询
- 用户模拟缴费
- 管理员当前在场车辆查询
- 管理员手动录入车辆入场

当前仍未接入 Redis 的部分：

- 管理员办理出场
- 停车费豁免真实流程
- 车位管理
- 收费规则配置
- 仪表盘实时统计

---

## 8. 后续扩展建议

后续管理员模块继续落地时，建议新增这些 key：

```text
parking:space:{spaceId}             -> Hash
parking:space:all                   -> Set
parking:stats:day:{yyyyMMdd}        -> Hash
parking:exemption:{exemptionId}     -> Hash
parking:record:{recordId}:ops       -> List
```

建议用途：

- `parking:space:*`：车位状态管理
- `parking:stats:*`：仪表盘统计
- `parking:exemption:*`：费用豁免单据
- `parking:record:{recordId}:ops`：记录操作日志

---

## 9. 初始化方式

仓库已经提供 Redis 启动文件：
[docker-compose.redis.yml](/Users/ellis/Desktop/停车场管理/docker-compose.redis.yml)

如果本机没有安装 Redis，可以直接运行：

```bash
docker compose -f docker-compose.redis.yml up -d
```

如果已经使用 Homebrew 安装 Redis，也可以继续使用：

```bash
brew services start redis
```

---

## 10. 注意事项

- 当前密码仍是演示用明文存储，后续应切换到 `BCrypt`
- 当前 Redis 更偏向课程演示和快速开发，不适合长期审计留存
- 如果后续要做复杂报表，建议增加 MySQL 作为归档库
