# Vault Frontend

Vault 智能教学系统前端应用

## 技术栈

- Vue.js 3.x
- Element Plus
- Vue Router
- Pinia
- Vite 5.x
- Axios
- Markdown-it
- ECharts

## 快速开始

### 1. 环境要求

- Node.js 16+
- npm 或 pnpm

### 2. 安装依赖

```bash
npm install
# 或
pnpm install
```

### 3. 运行开发服务器

```bash
npm run dev
# 或
pnpm dev
```

访问：http://localhost:5173

### 4. 构建生产版本

```bash
npm run build
# 或
pnpm build
```

## 项目结构

```
vault-frontend/
├── src/
│   ├── main.js                    # 应用入口
│   ├── App.vue                    # 根组件
│   ├── assets/                    # 静态资源
│   │   └── styles/               # 全局样式
│   ├── components/                # 组件
│   │   ├── common/               # 通用组件
│   │   ├── dashboard/            # 看板组件
│   │   ├── chat/                 # 聊天组件
│   │   ├── questionnaire/        # 问卷组件
│   │   └── skill/                # 技能组件
│   ├── views/                     # 页面
│   │   ├── auth/                 # 认证页面
│   │   ├── common/               # 通用页面
│   │   ├── student/              # 学生页面
│   │   └── teacher/              # 教师页面
│   ├── router/                    # 路由配置
│   ├── store/                     # 状态管理
│   │   └── modules/              # 状态模块
│   ├── api/                       # API接口
│   ├── utils/                     # 工具函数
│   └── config/                    # 配置文件
├── index.html                     # HTML入口
├── vite.config.js                # Vite配置
└── package.json                   # 依赖配置
```

## 功能模块

### 学生端
- 登录/注册
- 个人看板
- AI对话
- 问卷填写
- 个人资料

### 教师端
- 登录/注册
- 教师看板
- AI对话
- 问卷生成
- 材料上传
- 技能管理
- 学生分析

## API配置

API代理配置在 `vite.config.js` 中：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 测试账号

### 学生账号
- 用户名：student1
- 密码：password123

### 教师账号
- 用户名：teacher1
- 密码：password123

## 许可证

MIT License
