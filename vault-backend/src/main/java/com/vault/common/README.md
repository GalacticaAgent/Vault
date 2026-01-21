# Common 通用类层

## 职责
提供通用工具类和常量定义。

## 主要类

### Result.java
统一响应结果类
- 成功响应封装
- 失败响应封装
- 响应码和消息

### PageResult.java
分页响应结果类
- 分页数据封装
- 总数和分页信息

### Constants.java
常量定义
- 系统常量
- 业务常量
- 配置常量

## 使用示例
```java
// 成功响应
return Result.success(data);

// 失败响应
return Result.error(ErrorCode.USER_NOT_FOUND);

// 分页响应
return PageResult.success(list, total);
```

