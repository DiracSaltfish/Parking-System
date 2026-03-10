# 停车场管理系统

基于 `Vue 3 + Spring Boot + Redis` 的前后端分离课程实训项目。

当前仓库已经包含：

- `frontend/`：Vue 3 + Element Plus 前端
- `backend/`：Spring Boot 后端
- `docs/`：总体设计、功能设计、接口文档、Redis 设计、汇报提纲
- `docker-compose.redis.yml`：Redis 容器启动文件

## 当前实现状态

### 已完成

- 统一登录页
  - 账号为 `admin` 时进入管理员端
  - 其他账号进入普通用户端
- 管理员登录
- 普通用户注册 / 登录
- Token 会话校验
- 用户绑定车牌、修改车牌、删除车牌
- 用户查询当前停车信息
- 用户查询停车记录
- 用户查询缴费记录
- 用户模拟缴费
- 管理员查看当前在场车辆
- 管理员手动录入车辆入场
- Redis 本机运行与仓储接入
- Redis 种子数据初始化
- 前端输入合法性校验

### 当前为骨架 / 演示数据

- 管理员仪表盘统计
- 车位管理
- 收费规则设置
- 停车费用豁免详情与管理
- 管理员办理出场

### 下一步建议

- 完成管理员办理出场
- 完成费用豁免真实流程
- 将管理员仪表盘接入 Redis 实时统计
- 将车位管理与收费规则改为真实持久化
- 将密码存储改为 `BCrypt`

## 当前技术栈

- 前端：`Vue 3 + Vite + Element Plus + Pinia + Vue Router`
- 后端：`Spring Boot 3 + Java 17`
- 数据存储：`Redis`
- 接口风格：`RESTful API`

## 演示账号

- 管理员：`admin / Admin123`
- 普通用户：`zhangsan / User1234`

## Redis 说明

- 当前后端默认连接本机 Redis：`127.0.0.1:6379`
- Spring Boot 启动时会自动写入演示数据
- 实际 Key 结构、字段定义、初始化方式见 [Redis设计说明.md](/Users/ellis/Desktop/停车场管理/docs/Redis设计说明.md)

## 运行方式

### 1. 启动 Redis

方式一：本机安装 Redis

```bash
brew services start redis
```

方式二：使用仓库自带 Compose

```bash
docker compose -f docker-compose.redis.yml up -d
```

### 2. 启动后端

要求：

- `JDK 17+`
- `Maven 3.9+`

命令：

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：

- [http://localhost:8080](http://localhost:8080)

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

- [http://localhost:5173](http://localhost:5173)

## 文档索引

- [停车场管理系统总体设计.md](/Users/ellis/Desktop/停车场管理/docs/停车场管理系统总体设计.md)
- [停车场管理系统详细功能设计.md](/Users/ellis/Desktop/停车场管理/docs/停车场管理系统详细功能设计.md)
- [API接口文档.md](/Users/ellis/Desktop/停车场管理/docs/API接口文档.md)
- [Redis设计说明.md](/Users/ellis/Desktop/停车场管理/docs/Redis设计说明.md)
- [加密存储流程设计.md](/Users/ellis/Desktop/停车场管理/docs/加密存储流程设计.md)
- [项目汇报重点.md](/Users/ellis/Desktop/停车场管理/docs/项目汇报重点.md)
