# Auth Controller

## 职责
处理用户认证相关的 HTTP 请求。

## 主要控制器

### AuthController.java
认证控制器
- POST /api/auth/register - 用户注册
- POST /api/auth/login - 用户登录
- POST /api/auth/logout - 用户登出
- POST /api/auth/refresh - 刷新 Token
- GET /api/auth/me - 获取当前用户信息

