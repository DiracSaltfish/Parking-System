# 停车场管理系统

当前仓库已经包含：

- `docs/`：总体设计、详细功能设计、接口文档、Redis 设计说明
- `backend/`：Spring Boot 项目骨架
- `frontend/`：Vue 3 + Element Plus 页面骨架

## 当前阶段说明

- Redis 只完成设计，不接入本机实例
- 后端接口已经按文档完成骨架编排
- 前端页面已经完成角色划分和基础路由
- 后续可以在此基础上继续补 Service、Redis Repository 和联调逻辑

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

### 前端

```bash
cd frontend
npm install
npm run dev
```
