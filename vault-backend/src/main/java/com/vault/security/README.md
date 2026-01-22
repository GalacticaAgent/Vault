# Security 安全层

## 职责
处理系统安全相关功能，包括 JWT 认证、权限控制等。

## 主要类

### JwtAuthenticationFilter.java
JWT 认证过滤器
- Token 解析与验证
- 用户信息提取
- 权限校验

### JwtTokenProvider.java
JWT Token 提供者
- Token 生成
- Token 解析
- Token 验证

### UserDetailsServiceImpl.java
用户详情服务实现
- 用户信息加载
- 权限信息加载
- 认证支持

