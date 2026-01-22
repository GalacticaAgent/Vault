# Exception 异常处理层

## 职责
统一处理系统异常，提供友好的错误响应。

## 主要类

### GlobalExceptionHandler.java
全局异常处理器
- 统一异常捕获
- 异常信息格式化
- HTTP 状态码映射

### BusinessException.java
业务异常类
- 业务异常封装
- 错误码和错误信息

### ErrorCode.java
错误码枚举
- 系统错误码定义
- 错误信息常量

## 错误码示例
- 10001: 用户不存在
- 10002: 密码错误
- 20001: 对话不存在
- 30001: 问卷不存在

