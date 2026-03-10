# 停车场管理系统 API 接口文档

## 1. 说明

本文档用于约定前后端联调接口，当前阶段为**接口编排版**，用于统一开发结构。

- 项目前缀：`/api`
- 接口风格：`RESTful`
- 数据格式：`JSON`
- 认证方式：`Token`
- 当前状态：已设计，后端骨架按本文档搭建，业务逻辑后续实现

---

## 2. 统一请求头

除登录、注册接口外，其余接口建议携带：

```http
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 3. 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

字段说明：

- `code`：业务状态码，`200` 表示成功
- `message`：响应说明
- `data`：业务数据

分页接口建议返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 0,
    "pageNum": 1,
    "pageSize": 10,
    "records": []
  }
}
```

---

## 4. 认证模块

### 4.1 管理员登录

- 方法：`POST`
- 路径：`/api/auth/admin/login`

请求参数：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "admin-token-demo",
    "role": "ADMIN",
    "userId": "A1001",
    "username": "admin",
    "displayName": "系统管理员"
  }
}
```

### 4.2 普通用户注册

- 方法：`POST`
- 路径：`/api/auth/user/register`

请求参数：

```json
{
  "username": "zhangsan",
  "password": "123456",
  "phone": "13800138000",
  "realName": "张三"
}
```

### 4.3 普通用户登录

- 方法：`POST`
- 路径：`/api/auth/user/login`

请求参数：

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "user-token-demo",
    "role": "USER",
    "userId": "U1001",
    "username": "zhangsan",
    "displayName": "张三"
  }
}
```

### 4.4 获取当前登录用户信息

- 方法：`GET`
- 路径：`/api/auth/profile`

---

## 5. 普通用户模块

## 5.1 用户绑定车牌

### 查询我的车辆

- 方法：`GET`
- 路径：`/api/user/vehicles`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "vehicleId": "V1001",
      "plateNumber": "粤B12345",
      "isPrimary": true,
      "bindTime": "2026-03-10 20:00:00"
    }
  ]
}
```

### 新增绑定车辆

- 方法：`POST`
- 路径：`/api/user/vehicles`

请求参数：

```json
{
  "plateNumber": "粤B12345",
  "isPrimary": true
}
```

### 修改绑定车辆

- 方法：`PUT`
- 路径：`/api/user/vehicles/{vehicleId}`

### 删除绑定车辆

- 方法：`DELETE`
- 路径：`/api/user/vehicles/{vehicleId}`

## 5.2 当前停车查询

### 查询我的当前停车信息

- 方法：`GET`
- 路径：`/api/user/parking/current`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recordId": "R1001",
    "plateNumber": "粤B12345",
    "spaceCode": "A-021",
    "entryTime": "2026-03-10 17:30:00",
    "durationMinutes": 145,
    "originalAmount": 15.0,
    "discountAmount": 0.0,
    "finalAmount": 15.0,
    "payStatus": "UNPAID"
  }
}
```

### 查询我的停车记录

- 方法：`GET`
- 路径：`/api/user/parking/records`
- 参数：`pageNum`、`pageSize`、`plateNumber`

### 查询我的缴费记录

- 方法：`GET`
- 路径：`/api/user/payments`
- 参数：`pageNum`、`pageSize`

### 提交停车缴费

- 方法：`POST`
- 路径：`/api/user/payments/pay`

请求参数：

```json
{
  "recordId": "R1001",
  "payMethod": "WECHAT"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "支付成功",
  "data": {
    "paymentId": "P1001",
    "recordId": "R1001",
    "payAmount": 15.0,
    "payMethod": "WECHAT",
    "payStatus": "PAID",
    "payTime": "2026-03-10 20:15:00"
  }
}
```

---

## 6. 管理员模块

## 6.1 仪表盘

### 获取首页汇总数据

- 方法：`GET`
- 路径：`/api/admin/dashboard/summary`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSpaces": 120,
    "freeSpaces": 48,
    "occupiedSpaces": 72,
    "todayEntryCount": 56,
    "todayExitCount": 43,
    "todayIncome": 1260.0
  }
}
```

## 6.2 车位管理

### 查询车位列表

- 方法：`GET`
- 路径：`/api/admin/spaces`
- 参数：`pageNum`、`pageSize`、`status`、`type`

### 新增车位

- 方法：`POST`
- 路径：`/api/admin/spaces`

请求参数：

```json
{
  "spaceCode": "A-021",
  "type": "NORMAL",
  "floor": "B1",
  "remark": "靠近电梯"
}
```

### 修改车位

- 方法：`PUT`
- 路径：`/api/admin/spaces/{spaceId}`

### 删除车位

- 方法：`DELETE`
- 路径：`/api/admin/spaces/{spaceId}`

## 6.3 停车管理

### 查询当前在场车辆

- 方法：`GET`
- 路径：`/api/admin/parking/current`
- 参数：`pageNum`、`pageSize`、`plateNumber`、`payStatus`

### 车辆入场

- 方法：`POST`
- 路径：`/api/admin/parking/entry`

请求参数：

```json
{
  "plateNumber": "粤B12345",
  "spaceId": "S1021"
}
```

### 车辆出场

- 方法：`POST`
- 路径：`/api/admin/parking/exit`

请求参数：

```json
{
  "recordId": "R1001"
}
```

### 查询历史停车记录

- 方法：`GET`
- 路径：`/api/admin/parking/records`
- 参数：`pageNum`、`pageSize`、`plateNumber`、`recordStatus`、`payStatus`

## 6.4 费用豁免

### 对单条停车记录执行减免

- 方法：`POST`
- 路径：`/api/admin/parking/exempt`

请求参数：

```json
{
  "recordId": "R1001",
  "exemptionType": "FULL",
  "exemptionAmount": 15.0,
  "reason": "内部车辆免单"
}
```

字段说明：

- `exemptionType`：`FULL` 全免，`PARTIAL` 部分减免
- `exemptionAmount`：部分减免时填写实际减免金额

### 查询某条记录的豁免信息

- 方法：`GET`
- 路径：`/api/admin/parking/exempt/{recordId}`

## 6.5 收费规则

### 查询收费规则

- 方法：`GET`
- 路径：`/api/admin/fee-rule`

### 修改收费规则

- 方法：`PUT`
- 路径：`/api/admin/fee-rule`

请求参数：

```json
{
  "freeMinutes": 30,
  "pricePerHour": 5.0,
  "dailyMaxAmount": 30.0
}
```

---

## 7. 状态字段建议

### 7.1 角色

- `ADMIN`
- `USER`

### 7.2 支付状态

- `UNPAID`
- `PAID`
- `EXEMPTED`

### 7.3 停车记录状态

- `PARKING`
- `COMPLETED`

### 7.4 车位状态

- `FREE`
- `OCCUPIED`
- `DISABLED`

### 7.5 车位类型

- `NORMAL`
- `NEW_ENERGY`
- `VIP`

---

## 8. 联调约定

- 当前阶段后端先返回占位数据或空数据结构
- 所有接口路径已经按模块固定，前端页面按此路径封装 API
- Redis 相关读写逻辑后续实现时，保持接口协议不变

---

## 9. 下一步实施建议

按这个接口文档继续往下做时，建议顺序是：

1. 先定义后端 DTO / VO / 统一响应对象
2. 再搭控制器和路由骨架
3. 再补 Redis Repository 和 Service 逻辑
4. 最后联调前端页面
