# Redis 设计说明

## 1. 当前策略

你已经明确 **Redis 不在本机运行**，所以当前项目阶段只做两件事：

- 把 Redis 的业务模型和配置入口设计好
- 在后端项目中预留依赖、配置项、Repository 位置

当前阶段**不接入实际 Redis 服务，不编写真实读写实现**。

---

## 2. 后续接入方式

后续可以接入：

- 云服务器 Redis
- Windows/Linux 虚拟机中的 Redis
- Docker 中的 Redis
- 远程开发环境中的 Redis

后端只需要修改配置即可：

```yaml
spring:
  data:
    redis:
      host: your-redis-host
      port: 6379
      password: your-password
      database: 0
```

---

## 3. Key 设计总览

### 3.1 用户与管理员

```text
admin:{adminId} -> Hash
user:{userId} -> Hash
user:username:{username} -> String
```

### 3.2 用户绑定车辆

```text
user:vehicle:{vehicleId} -> Hash
user:{userId}:vehicles -> Set
vehicle:plate:{plateNumber} -> String
```

### 3.3 车位

```text
parking:space:{spaceId} -> Hash
parking:space:all -> Set
parking:space:free -> Set
parking:space:occupied -> Set
```

### 3.4 当前停车

```text
parking:current:{plateNumber} -> Hash
parking:current:plates -> Set
```

### 3.5 历史记录

```text
parking:record:{recordId} -> Hash
parking:record:list -> ZSet
parking:record:plate:{plateNumber} -> List
```

### 3.6 支付记录

```text
payment:{paymentId} -> Hash
payment:user:{userId} -> List
payment:record:{recordId} -> String
```

### 3.7 费用豁免

```text
parking:exemption:{exemptionId} -> Hash
parking:record:exemptions:{recordId} -> List
```

### 3.8 统计

```text
parking:stats:day:{yyyyMMdd} -> Hash
```

---

## 4. 当前项目中的处理方式

当前阶段后端会：

- 保留 Redis 依赖
- 保留 Redis 配置项
- 保留 `repository` 包位置
- 在 `service` 中用占位数据代替真实 Redis 读写

这样可以保证：

1. 项目结构是完整的
2. 后期接 Redis 时不需要大改目录
3. 前端可以先联调页面结构

---

## 5. 后续实现建议

正式接入 Redis 时，优先实现下面几部分：

1. 登录用户与 Token 存储
2. 车牌绑定关系
3. 当前停车状态
4. 停车记录与支付记录
5. 统计数据

不要一开始就把所有 Key 一次性写完，先保证主流程：

- 用户登录
- 绑定车牌
- 车辆入场
- 当前停车查询
- 缴费
- 管理员查看与豁免
