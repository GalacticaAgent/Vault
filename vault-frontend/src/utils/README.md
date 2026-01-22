# Utils 工具层

## 职责
提供前端通用工具函数，简化开发。

## 主要工具文件

### auth.js
认证工具
- `getToken()` - 获取 Token
- `setToken(token)` - 保存 Token
- `removeToken()` - 删除 Token
- `hasToken()` - 检查是否有 Token

### storage.js
本地存储工具
- `setItem(key, value)` - 保存数据（自动 JSON 序列化）
- `getItem(key)` - 获取数据（自动 JSON 反序列化）
- `removeItem(key)` - 删除数据
- `clear()` - 清空存储

### date.js
日期处理工具
- `formatTime(time)` - 格式化时间（YYYY-MM-DD HH:mm:ss）
- `formatDate(time)` - 格式化日期（YYYY-MM-DD）
- `formatRelativeTime(time)` - 相对时间（3 分钟前）
- `parseDate(str)` - 解析日期字符串

### validator.js
表单验证工具
- `validateEmail(email)` - 验证邮箱
- `validatePhone(phone)` - 验证手机号
- `validatePassword(password)` - 验证密码强度
- `validateStudentId(id)` - 验证学号

### markdown.js
Markdown 处理工具
- `renderMarkdown(content)` - 渲染 Markdown
- `highlightCode(code, lang)` - 代码高亮
- `sanitizeHtml(html)` - HTML 安全过滤

### file.js
文件处理工具
- `formatFileSize(bytes)` - 格式化文件大小
- `getFileExtension(filename)` - 获取文件扩展名
- `isImage(filename)` - 判断是否为图片
- `downloadFile(url, filename)` - 下载文件

### debounce.js
防抖节流工具
- `debounce(fn, delay)` - 防抖函数
- `throttle(fn, delay)` - 节流函数

### clipboard.js
剪贴板工具
- `copyToClipboard(text)` - 复制到剪贴板
- `pasteFromClipboard()` - 从剪贴板粘贴

## 使用示例
```javascript
import { formatTime } from '@/utils/date'
import { renderMarkdown } from '@/utils/markdown'

// 格式化时间
const formattedTime = formatTime(new Date())

// 渲染 Markdown
const html = renderMarkdown('# 标题\n内容')
```
