<template>
  <div class="login-container">
    <!-- 动态背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <!-- Logo 和标题 -->
      <div class="logo-section">
        <div class="logo-icon">
          <div class="logo-ring"></div>
          <svg viewBox="0 0 24 24" width="50" height="50" fill="currentColor" class="logo-svg">
            <path d="M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z"/>
          </svg>
        </div>
        <h1 class="title">
          <span class="title-text">Vault CS</span>
        </h1>
        <p class="subtitle">智能化的计算机科学教学与学习平台</p>
      </div>

      <!-- 登录表单 -->
      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username" class="form-item-custom">
            <div class="input-wrapper">
              <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                clearable
                class="custom-input"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password" class="form-item-custom">
            <div class="input-wrapper">
              <svg class="input-icon" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
              </svg>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                clearable
                class="custom-input"
              />
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-button">
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <span class="footer-text">还没有账号？</span>
            <el-link type="primary" @click="goToRegister" class="register-link">
              立即注册
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        console.log('登录成功，用户信息:', userStore.userInfo)
        ElMessage.success('登录成功')
        
        // 根据角色自动跳转到对应页面
        const role = userStore.userInfo?.role
        console.log('用户角色:', role)
        
        if (role === 'STUDENT') {
          console.log('跳转到学生页面')
          router.push('/student/dashboard')
        } else if (role === 'TEACHER') {
          console.log('跳转到教师页面')
          router.push('/teacher/dashboard')
        } else if (role === 'ADMIN') {
          console.log('跳转到管理员页面')
          router.push('/admin')
        } else {
          console.log('未知角色，跳转到首页')
          router.push('/')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error(error.response?.data?.message || error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* 容器和背景 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  overflow: hidden;
}

/* 动态背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
  }
}

/* 内容区域 */
.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 440px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 35px;
  color: white;
  animation: slideDown 0.8s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-icon {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 24px;
  margin-bottom: 24px;
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15),
              0 0 0 1px rgba(255, 255, 255, 0.15) inset;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-icon:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.25) inset;
}

.logo-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  animation: pulse 3s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.5;
  }
}

.logo-svg {
  color: white;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
  z-index: 1;
}

.title {
  margin: 0 0 12px 0;
  font-size: 48px;
  font-weight: 700;
  color: white;
  letter-spacing: 2px;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.title-text {
  display: inline-block;
  animation: glow 2s ease-in-out infinite;
}

@keyframes glow {
  0%, 100% {
    text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  50% {
    text-shadow: 0 4px 20px rgba(255, 255, 255, 0.3),
                 0 4px 12px rgba(0, 0, 0, 0.2);
  }
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 400;
  letter-spacing: 0.5px;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  padding: 40px 36px 36px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  animation: cardFadeIn 0.8s ease-out 0.2s both;
  backdrop-filter: blur(10px);
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
  margin: 0 0 6px 0;
  font-size: 26px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #95a5a6;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.form-item-custom {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 1;
  color: #95a5a6;
  transition: color 0.3s ease;
  pointer-events: none;
}

.input-wrapper:focus-within .input-icon {
  color: #667eea;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 48px;
  padding-right: 16px;
  height: 50px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1.5px solid #e8ecef;
  background: #ffffff;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2),
              0 0 0 3px rgba(102, 126, 234, 0.08);
}

.custom-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #2c3e50;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #bdc3c7;
}

/* 按钮样式 */
.form-item-button {
  margin-top: 28px;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.45);
}

.login-button:active {
  transform: translateY(0);
}

/* 底部链接 */
.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

.footer-text {
  font-size: 14px;
  color: #7f8c8d;
  margin-right: 8px;
}

.register-link {
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .login-content {
    max-width: 100%;
  }

  .login-card {
    padding: 35px 25px 30px;
    border-radius: 16px;
  }
  
  .title {
    font-size: 42px;
    letter-spacing: 2px;
  }
  
  .subtitle {
    font-size: 14px;
  }

  .card-header h2 {
    font-size: 22px;
  }

  .custom-input :deep(.el-input__wrapper),
  .login-button {
    height: 48px;
  }

  .circle-1 {
    width: 200px;
    height: 200px;
  }

  .circle-2 {
    width: 150px;
    height: 150px;
  }

  .circle-3 {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 400px) {
  .login-card {
    padding: 30px 20px 25px;
  }

  .logo-icon {
    width: 80px;
    height: 80px;
  }

  .logo-svg {
    width: 40px;
    height: 40px;
  }

  .title {
    font-size: 36px;
  }

  .footer-badge {
    font-size: 12px;
    padding: 10px 20px;
  }
}
</style>
