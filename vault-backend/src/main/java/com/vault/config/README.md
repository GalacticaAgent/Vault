# Config 配置层

## 职责
系统配置类，包括 Web 配置、数据库配置、缓存配置、安全配置等。

## 主要配置类

### WebMvcConfig.java
Web MVC 配置
- 跨域配置（CORS）
- 拦截器注册
- 静态资源映射
- 消息转换器配置

### RedisConfig.java
Redis 缓存配置
- RedisTemplate 配置
- 序列化方式（JSON）
- 缓存过期策略
- 连接池配置

### ElasticsearchConfig.java
Elasticsearch 配置
- 连接配置
- RestHighLevelClient 配置
- 索引设置

### MilvusConfig.java
Milvus 向量数据库配置
- 连接配置
- Collection 管理
- 向量索引配置

### AsyncConfig.java
异步任务配置
- 线程池配置
- 异步执行器
- 异步异常处理

### SwaggerConfig.java
API 文档配置
- Swagger UI 配置
- API 分组
- 接口描述
- 认证配置

### SecurityConfig.java
安全配置
- JWT 认证配置
- 权限拦截规则
- 密码加密方式

### MybatisPlusConfig.java
MyBatis-Plus 配置
- 分页插件
- 乐观锁插件
- 逻辑删除配置
- 自动填充配置

## 配置文件
- application.yml (主配置)
- application-dev.yml (开发环境)
- application-prod.yml (生产环境)
