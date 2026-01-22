# 修复 Collation 冲突问题

## 问题描述

提交问卷答案时出现以下错误:
```
Illegal mix of collations (utf8mb4_general_ci,IMPLICIT) and (utf8mb4_unicode_ci,IMPLICIT) for operation '='
```

这是因为数据库表的字符集排序规则(collation)不一致导致的。

## 解决方案

### 方法1: 使用批处理脚本(推荐)

1. 双击运行 `fix_collation.bat`
2. 输入 MySQL root 密码
3. 等待修复完成

### 方法2: 使用 MySQL Workbench

1. 打开 MySQL Workbench
2. 连接到数据库
3. 打开文件: `database\fix_collation.sql`
4. 点击"闪电"图标执行脚本

### 方法3: 使用命令行

```bash
mysql -u root -p < database\fix_collation.sql
```

## 修复内容

该脚本会将以下内容统一为 `utf8mb4_unicode_ci`:
- 数据库 vault 的默认 collation
- 所有表的 collation
- 所有列的 collation

## 修复后

重新启动后端服务,问卷提交功能应该可以正常工作。

## 验证修复

运行以下 SQL 查询验证所有表的 collation:

```sql
SELECT
    TABLE_NAME,
    TABLE_COLLATION
FROM
    information_schema.TABLES
WHERE
    TABLE_SCHEMA = 'vault';
```

所有表的 `TABLE_COLLATION` 应该都是 `utf8mb4_unicode_ci`。
