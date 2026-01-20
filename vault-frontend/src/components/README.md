# Components 组件层

## 职责
封装可复用的 Vue 组件，包括布局组件、通用组件和业务组件。

## 目录结构

### layout/ - 布局组件
布局相关组件，用于页面整体结构。

#### Header.vue
顶部导航栏
- Logo 展示
- 用户信息
- 退出登录
- 消息通知

#### Sidebar.vue
侧边栏导航
- 菜单导航
- 折叠/展开
- 角色权限控制

#### Footer.vue
页面底部
- 版权信息
- 友情链接
- 联系方式

#### Layout.vue
页面布局容器
- 组合 Header + Sidebar + Footer
- 响应式布局

### common/ - 通用组件
通用 UI 组件，与业务无关。

#### Button.vue
自定义按钮组件
- 多种样式
- 加载状态
- 禁用状态

#### Card.vue
卡片容器组件
- 标题
- 内容区域
- 操作按钮

#### Modal.vue
弹窗组件
- 标题
- 内容
- 确认/取消

#### Loading.vue
加载动画组件
- 全屏加载
- 局部加载

#### Pagination.vue
分页组件
- 页码切换
- 每页数量选择

### business/ - 业务组件
与业务逻辑相关的组件。

#### ChatBox.vue
对话框组件
- 消息列表
- 输入框
- 附件上传

#### MessageBubble.vue
消息气泡组件
- 用户消息
- AI 消息
- Markdown 渲染
- 代码高亮

#### DashboardCard.vue
看板卡片组件
- 卡片标题
- 数据展示
- 刷新按钮
- 删除按钮

#### QuestionItem.vue
题目展示组件
- 题干展示
- 选项渲染
- 答案提交

#### ProfileChart.vue
肖像图表组件
- 知识点雷达图
- 学习曲线图
- 数据可视化
