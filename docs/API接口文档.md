# 停车场管理系统 API 接口文档

## 1. 文档说明

本文档基于当前仓库代码整理，反映的是**当前实现状态**，不是早期接口规划稿。

- 项目前缀：`/api`
- 数据格式：`JSON`
- 认证方式：`Bearer Token`
- 当前日期：`2026-03-10`

状态标记说明：

- `已实现`：接口已接后端业务，可直接联调
- `骨架`：接口存在，但当前返回演示数据或 `202` 提示

---

## 2. 统一请求头

除登录、注册接口外，其余接口建议携带：

```http
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 3. 统一返回格式

成功示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

骨架接口示例：

```json
{
  "code": 202,
  "message": "已创建接口骨架，等待接入 Redis",
  "data": {}
}
```

分页示例：

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

### 4.1 管理员登录 `已实现`

- 方法：`POST`
- 路径：`/api/auth/admin/login`

请求参数：

```json
{
  "username": "admin",
  "password": "Admin123"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "admin-token-A1001-1773149190080",
    "role": "ADMIN",
    "userId": "A1001",
    "username": "admin",
    "displayName": "系统管理员"
  }
}
```

### 4.2 普通用户注册 `已实现`

- 方法：`POST`
- 路径：`/api/auth/user/register`

请求参数：

```json
{
  "username": "zhangsan",
  "password": "User1234",
  "phone": "13800138000",
  "realName": "张三"
}
```

### 4.3 普通用户登录 `已实现`

- 方法：`POST`
- 路径：`/api/auth/user/login`

请求参数：

```json
{
  "username": "zhangsan",
  "password": "User1234"
}
```

### 4.4 获取当前登录用户信息 `已实现`

- 方法：`GET`
- 路径：`/api/auth/profile`

---

## 5. 普通用户模块

### 5.1 我的车辆

#### 查询我的车辆 `已实现`

- 方法：`GET`
- 路径：`/api/user/vehicles`

#### 绑定车辆 `已实现`

- 方法：`POST`
- 路径：`/api/user/vehicles`

请求参数：

```json
{
  "plateNumber": "粤B12345",
  "isPrimary": true
}
```

#### 修改车辆 `已实现`

- 方法：`PUT`
- 路径：`/api/user/vehicles/{vehicleId}`

#### 删除车辆 `已实现`

- 方法：`DELETE`
- 路径：`/api/user/vehicles/{vehicleId}`

### 5.2 当前停车

#### 查询当前停车信息 `已实现`

- 方法：`GET`
- 路径：`/api/user/parking/current`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recordId": "R1002",
    "plateNumber": "粤B12345",
    "spaceCode": "A-021",
    "entryTime": "2026-03-10 18:53:30",
    "exitTime": "",
    "durationMinutes": 153,
    "originalAmount": 15.0,
    "discountAmount": 0.0,
    "finalAmount": 15.0,
    "payStatus": "UNPAID",
    "recordStatus": "PARKING",
    "active": true
  }
}
```

### 5.3 停车记录

#### 查询我的停车记录 `已实现`

- 方法：`GET`
- 路径：`/api/user/parking/records`

查询参数：

- `plateNumber`
- `pageNum`
- `pageSize`

### 5.4 支付记录与缴费

#### 查询我的支付记录 `已实现`

- 方法：`GET`
- 路径：`/api/user/payments`

#### 模拟缴费 `已实现`

- 方法：`POST`
- 路径：`/api/user/payments/pay`

请求参数：

```json
{
  "recordId": "R1001",
  "payMethod": "WECHAT"
}
```

---

## 6. 管理员模块

### 6.1 仪表盘

#### 查询仪表盘汇总 `骨架`

- 方法：`GET`
- 路径：`/api/admin/dashboard/summary`

说明：

- 当前返回演示统计数据
- 还未接入 Redis 实时统计

### 6.2 当前停车管理

#### 查询当前在场车辆 `已实现`

- 方法：`GET`
- 路径：`/api/admin/parking/current`

查询参数：

- `plateNumber`
- `payStatus`
- `pageNum`
- `pageSize`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 2,
    "pageNum": 1,
    "pageSize": 20,
    "records": [
      {
        "recordId": "R1003",
        "plateNumber": "粤C88888",
        "ownerName": "临时车辆",
        "spaceCode": "A-101",
        "entryTime": "2026-03-10 21:26:39",
        "exitTime": "",
        "durationMinutes": 0,
        "originalAmount": 0.0,
        "discountAmount": 0.0,
        "finalAmount": 0.0,
        "payStatus": "UNPAID",
        "recordStatus": "PARKING",
        "active": true
      }
    ]
  }
}
```

#### 手动录入车辆入场 `已实现`

- 方法：`POST`
- 路径：`/api/admin/parking/entry`

请求参数：

```json
{
  "plateNumber": "粤C88888",
  "spaceId": "A-101"
}
```

说明：

- 如果车牌已绑定用户，会自动关联用户
- 如果车牌未绑定用户，会作为“临时车辆”入场
- 入场后从当前时刻开始计费

#### 办理车辆出场 `骨架`

- 方法：`POST`
- 路径：`/api/admin/parking/exit`

### 6.3 历史停车记录

#### 查询所有停车记录 `已实现`

- 方法：`GET`
- 路径：`/api/admin/parking/records`

查询参数：

- `plateNumber`
- `recordStatus`
- `payStatus`
- `pageNum`
- `pageSize`

### 6.4 费用豁免

#### 创建费用豁免 `骨架`

- 方法：`POST`
- 路径：`/api/admin/parking/exempt`

#### 查询豁免详情 `已实现`

- 方法：`GET`
- 路径：`/api/admin/parking/exempt/{recordId}`

说明：

- 当前详情接口返回演示数据

### 6.5 车位管理

#### 查询车位列表 `骨架`

- 方法：`GET`
- 路径：`/api/admin/spaces`

#### 新增车位 `骨架`

- 方法：`POST`
- 路径：`/api/admin/spaces`

#### 修改车位 `骨架`

- 方法：`PUT`
- 路径：`/api/admin/spaces/{spaceId}`

#### 删除车位 `骨架`

- 方法：`DELETE`
- 路径：`/api/admin/spaces/{spaceId}`

### 6.6 收费规则

#### 查询收费规则 `骨架`

- 方法：`GET`
- 路径：`/api/admin/fee-rule`

#### 修改收费规则 `骨架`

- 方法：`PUT`
- 路径：`/api/admin/fee-rule`

---

## 7. 前端登录说明

前端当前采用**统一登录页**：

- 用户输入账号和密码
- 当账号为 `admin` 时，前端调用管理员登录接口
- 其他账号默认调用普通用户登录接口
- 登录成功后根据返回的 `role` 进入对应页面

这部分逻辑位于：
[LoginView.vue](/Users/ellis/Desktop/停车场管理/frontend/src/views/auth/LoginView.vue)

---

## 8. 当前联调结论

目前已经实际验证通过的接口：

- 管理员登录
- 普通用户登录
- 用户车辆管理
- 用户当前停车查询
- 用户停车记录查询
- 用户支付记录查询
- 用户模拟缴费
- 管理员当前停车列表
- 管理员手动录入车辆入场

仍待继续开发的接口：

- 管理员出场
- 管理员费用豁免真实流程
- 车位管理真实持久化
- 收费规则真实持久化
- 仪表盘实时统计
