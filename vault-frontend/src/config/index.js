/**
 * 全局配置
 */

export default {
  // API 基础地址
  apiBaseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  
  // 应用标题
  appTitle: 'Vault 智能教学系统',
  
  // 分页配置
  pagination: {
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  
  // 文件上传配置
  upload: {
    maxSize: 10 * 1024 * 1024, // 10MB
    allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'application/pdf', 'application/msword']
  },
  
  // Token 配置
  token: {
    key: 'vault_token',
    expireTime: 7 * 24 * 60 * 60 * 1000 // 7 天
  }
}

