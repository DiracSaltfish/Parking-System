# 原生前端版本说明

这个目录是停车场管理系统的原生前端版本，使用：

- HTML
- CSS
- 原生 JavaScript

说明：

- 不依赖 Vue、React、Element Plus 等前端框架
- 直接调用当前 Spring Boot 后端接口
- 不会改动原本的 `frontend/` Vue 工程

## 运行方式

建议使用仓库内的本地代理服务打开，不要直接双击 `html` 文件。

```bash
cd frontend-native
node server.mjs
```

打开：

- [http://localhost:5501/index.html](http://localhost:5501/index.html)

说明：

- 该服务会同时提供静态页面
- 并将 `/api/*` 自动转发到本机后端 `http://127.0.0.1:8080`
- 这样手机、平板、WireGuard 访问时只需要连一个端口 `5501`

## 页面说明

- `index.html`：统一登录页
- `register.html`：注册页
- `admin.html`：管理员端
- `user.html`：普通用户端
