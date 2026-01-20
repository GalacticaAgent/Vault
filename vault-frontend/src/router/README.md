# Router 路由层

## 职责
定义前端路由配置，管理页面跳转和权限控制。

## 路由配置 (index.js)

### 公开路由
- `/login` - 登录页
- `/register` - 注册页

### 学生端路由
需要认证，且角色为 STUDENT
- `/student/dashboard` - 学生看板
- `/student/chat` - 智能问答
- `/student/questionnaire` - 问卷答题
- `/student/profile` - 个人肖像
- `/student/code-submit` - 代码提交

### 教师端路由
需要认证，且角色为 TEACHER
- `/teacher/dashboard` - 教师看板
- `/teacher/chat` - 智能分析
- `/teacher/questionnaire/create` - 问卷创建
- `/teacher/material` - 资料管理
- `/teacher/student/:id` - 学生分析
- `/teacher/class` - 班级分析

### 公共路由
- `/404` - 页面不存在
- `/403` - 权限不足

## 路由守卫

### beforeEach 全局前置守卫
1. 检查是否需要认证（meta.requiresAuth）
2. 检查是否有 Token
3. 验证用户角色
4. 重定向逻辑：
   - 未登录 → 登录页
   - 角色不匹配 → 403 页面
   - 已登录访问登录页 → 重定向到对应看板

## Meta 字段说明
```javascript
meta: {
  requiresAuth: true,    // 是否需要认证
  roles: ['STUDENT'],    // 允许的角色
  title: '页面标题'       // 页面标题
}
```

## 使用示例
```javascript
import { useRouter } from 'vue-router'

const router = useRouter()

// 编程式导航
router.push('/student/dashboard')
router.replace('/login')
router.go(-1)
```
