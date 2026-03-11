# 停车场管理系统

基于 `HTML + CSS + JavaScript + Spring Boot + Redis` 的前后端分离课程实训项目。

说明：

- 当前课程汇报主版本前端为原生 `HTML + CSS + JavaScript`
- 仓库中保留了一套 `Vue 3 + Element Plus` 版本，不作为当前课程要求的主交付版本

## 仓库结构

- `frontend-native/`：原生前端版本
- `frontend/`：保留的 Vue 版本
- `backend/`：Spring Boot 后端
- `docs/`：设计文档、接口文档、汇报提纲
- `docker-compose.redis.yml`：Redis 容器启动文件

## 当前实现状态

### 已完成

- 原生前端版本落地
- 统一登录页
  - 账号为 `admin` 时进入管理员端
  - 其他账号进入普通用户端
- 管理员登录
- 普通用户注册 / 登录
- Token 会话校验
- 用户绑定车牌、删除车牌
- 用户查询当前停车信息
- 用户查询停车记录
- 用户查询缴费记录
- 用户模拟缴费
- 管理员查看当前在场车辆
- 管理员查看历史停车记录
- 管理员手动录入车辆入场
- 管理员办理车辆出场
- 管理员车位管理与占用状态同步
- Redis 本机运行与仓储接入
- Redis 种子数据初始化
- 前端输入合法性校验

### 当前为骨架 / 演示数据

- 管理员仪表盘统计
- 收费规则设置
- 停车费用豁免详情与管理

### 下一步建议

- 完成费用豁免真实流程
- 将管理员仪表盘接入 Redis 实时统计
- 将收费规则改为真实持久化
- 将密码存储改为 `BCrypt`

## 当前技术栈

- 前端主版本：`HTML + CSS + JavaScript`
- 前端保留版本：`Vue 3 + Vite + Element Plus + Pinia + Vue Router`
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

### 3. 启动原生前端

```bash
cd frontend-native
python3 -m http.server 5501
```

原生前端地址：

- [http://localhost:5501/index.html](http://localhost:5501/index.html)

### 4. 启动 Vue 前端（保留版本）

```bash
cd frontend
npm install
npm run dev
```

Vue 前端默认地址：

- [http://localhost:5173](http://localhost:5173)

## 文档索引

- [停车场管理系统总体设计.md](/Users/ellis/Desktop/停车场管理/docs/停车场管理系统总体设计.md)
- [停车场管理系统详细功能设计.md](/Users/ellis/Desktop/停车场管理/docs/停车场管理系统详细功能设计.md)
- [API接口文档.md](/Users/ellis/Desktop/停车场管理/docs/API接口文档.md)
- [Redis设计说明.md](/Users/ellis/Desktop/停车场管理/docs/Redis设计说明.md)
- [加密存储流程设计.md](/Users/ellis/Desktop/停车场管理/docs/加密存储流程设计.md)
- [项目汇报重点.md](/Users/ellis/Desktop/停车场管理/docs/项目汇报重点.md)
