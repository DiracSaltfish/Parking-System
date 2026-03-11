# Apifox 导入说明

导入文件：

- `docs/apifox/openapi.yaml`

导入步骤：

1. 打开 Apifox
2. 选择“导入项目”
3. 选择 “OpenAPI / Swagger”
4. 选中本目录下的 `openapi.yaml`
5. 服务地址填写 `http://localhost:8080`

说明：

- 用户端接口需要先调用登录接口拿到 `Bearer Token`
- 当前两个骨架接口会返回 HTTP `200`，但响应体 `code` 为 `202`
  - `POST /api/admin/parking/exempt`
  - `PUT /api/admin/fee-rule`
