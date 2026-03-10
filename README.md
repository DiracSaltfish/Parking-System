# 停车场管理系统

当前仓库已经包含：

- `docs/`：总体设计、详细功能设计、接口文档、Redis 设计说明
- `backend/`：Spring Boot 项目骨架
- `frontend/`：Vue 3 + Element Plus 页面骨架
- `docker-compose.redis.yml`：Redis 容器启动文件

## 当前阶段说明

- 本机 Redis 已安装并可用，默认地址 `127.0.0.1:6379`
- 第一阶段主流程已经切换到 Redis 仓储
- 后端接口已经按文档完成骨架编排
- 前端页面已经完成角色划分和基础路由
- 后续可以在此基础上继续补 Service、Redis Repository 和联调逻辑

## 当前已打通的主流程

- 管理员登录
- 普通用户注册 / 登录
- 用户绑定车牌
- 用户查询当前停车信息
- 用户查询停车记录和缴费记录
- 用户模拟缴费

## 演示账号

- 管理员：`admin / Admin123`
- 普通用户：`zhangsan / User1234`

说明：

- 当前阶段后端默认连接本机 Redis
- 主流程数据会在 Spring Boot 启动时自动写入 Redis 种子数据
- 后续接入远程 Redis 时，只需要修改 `application.yml` 或环境变量
- Redis 的实际 Key 结构、字段定义、初始化方式见 [docs/Redis设计说明.md](/Users/ellis/Desktop/停车场管理/docs/Redis设计说明.md)

## 运行建议

### 后端

需要本机安装：

- JDK 17+
- Maven 3.9+

启动命令：

```bash
cd backend
mvn spring-boot:run
```

### Redis

方式一：本机已安装 Redis

```bash
brew services start redis
```

方式二：使用仓库自带 Compose

```bash
docker compose -f docker-compose.redis.yml up -d
```

### 前端

```bash
cd frontend
npm install
npm run dev
```
