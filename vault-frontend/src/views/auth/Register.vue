<template>
  <div class="register-container">
    <!-- 动态背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="register-content">
      <div class="register-card">
        <div class="register-header">
          <h2>欢迎加入</h2>
          <p>请填写以下信息完成注册</p>
        </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <!-- 基本信息 -->
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="4-20位字母、数字或下划线"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="至少8位，包含字母和数字"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称（选填）"
            clearable
          />
        </el-form-item>
        
        <!-- 角色选择 -->
        <el-form-item label="身份" prop="role" class="role-selector">
          <el-radio-group v-model="registerForm.role" class="role-group">
            <el-radio label="STUDENT" class="role-option">
              <div class="role-content">
                <svg class="role-icon" viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>
                </svg>
                <span>学生</span>
              </div>
            </el-radio>
            <el-radio label="TEACHER" class="role-option">
              <div class="role-content">
                <svg class="role-icon" viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                  <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3zm6.82 6L12 12.72 5.18 9 12 5.28 18.82 9zM17 15.99l-5 2.73-5-2.73v-3.72L12 15l5-2.73v3.72z"/>
                </svg>
                <span>教师</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 学生特有信息 -->
        <template v-if="registerForm.role === 'STUDENT'">
          <el-form-item label="学号" prop="studentNumber">
            <el-input
              v-model="registerForm.studentNumber"
              placeholder="请输入学号"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="专业" prop="major">
            <el-input
              v-model="registerForm.major"
              placeholder="请输入专业（选填）"
              clearable
            />
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="年级" prop="grade">
                <el-input
                  v-model="registerForm.grade"
                  placeholder="如：2024级"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="班级" prop="className">
                <el-input
                  v-model="registerForm.className"
                  placeholder="如：1班"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        
        <!-- 教师特有信息 -->
        <template v-if="registerForm.role === 'TEACHER'">
          <el-form-item label="工号" prop="teacherNumber">
            <el-input
              v-model="registerForm.teacherNumber"
              placeholder="请输入工号"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="院系" prop="department">
            <el-input
              v-model="registerForm.department"
              placeholder="请输入院系（选填）"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="职称" prop="title">
            <el-input
              v-model="registerForm.title"
              placeholder="请输入职称（选填）"
              clearable
            />
          </el-form-item>
        </template>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            <span v-if="!loading">立即注册</span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <span class="footer-text">已有账号？</span>
          <el-link type="primary" @click="goToLogin" class="login-link">立即登录</el-link>
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
import { registerApi } from '@/api/auth'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  nickname: '',
  role: 'STUDENT',
  studentNumber: '',
  major: '',
  grade: '',
  className: '',
  teacherNumber: '',
  department: '',
  title: ''
})

// 自定义验证规则
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test(value)) {
    callback(new Error('密码至少8位，包含字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名只能包含字母、数字和下划线，长度4-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择身份', trigger: 'change' }
  ],
  studentNumber: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  teacherNumber: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构建请求数据
        const requestData = {
          username: registerForm.username,
          password: registerForm.password,
          email: registerForm.email,
          nickname: registerForm.nickname || registerForm.username,
          role: registerForm.role
        }
        
        // 根据角色添加额外字段
        if (registerForm.role === 'STUDENT') {
          requestData.studentNumber = registerForm.studentNumber
          requestData.major = registerForm.major
          requestData.grade = registerForm.grade
          requestData.className = registerForm.className
        } else if (registerForm.role === 'TEACHER') {
          requestData.teacherNumber = registerForm.teacherNumber
          requestData.department = registerForm.department
          requestData.title = registerForm.title
        }
        
        const response = await registerApi(requestData)
        
        // 注册成功后自动登录
        if (response.data) {
          userStore.setToken(response.data.token)
          userStore.setUserInfo(response.data.userInfo)
          
          ElMessage.success('注册成功，正在跳转...')
          
          // 根据角色自动跳转到对应页面
          const role = response.data.userInfo.role
          if (role === 'STUDENT') {
            router.push('/student/dashboard')
          } else if (role === 'TEACHER') {
            router.push('/teacher/dashboard')
          } else {
            router.push('/')
          }
        }
      } catch (error) {
        console.error('注册错误:', error)
        ElMessage.error(error.response?.data?.message || error.message || '注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 容器和背景 */
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
.register-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 580px;
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
  margin-bottom: 30px;
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
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.15) inset,
              0 2px 4px rgba(255, 255, 255, 0.3) inset;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-icon:hover {
  transform: translateY(-8px) scale(1.08) rotate(2deg);
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.25) inset,
              0 4px 8px rgba(255, 255, 255, 0.4) inset;
}

.logo-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 22px;
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
  font-size: 50px;
  font-weight: 800;
  color: white;
  letter-spacing: 3.5px;
  text-shadow: 0 6px 16px rgba(0, 0, 0, 0.3),
               0 2px 4px rgba(0, 0, 0, 0.2);
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
  font-size: 15px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 400;
  letter-spacing: 0.5px;
}

/* 注册卡片 */
.register-card {
  width: 100%;
  max-height: 80vh;
  padding: 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 20px 70px rgba(0, 0, 0, 0.3),
              0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  overflow-y: auto;
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

.register-header {
  text-align: center;
  margin-bottom: 28px;
}

.register-header h2 {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

.register-header p {
  margin: 0;
  font-size: 14px;
  color: #95a5a6;
}

/* 表单样式 */
.register-form {
  margin-top: 20px;
}

.register-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 8px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  background: #f8f9fa;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 10px 16px;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
  transform: translateY(-1px);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.25),
              0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.register-form :deep(.el-input__inner) {
  font-size: 14px;
  color: #2c3e50;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #bdc3c7;
}

/* 角色选择器样式 */
.role-selector {
  margin: 20px 0 40px 0;
  position: relative;
  z-index: 1;
}

.role-selector :deep(.el-form-item__label) {
  position: relative;
  display: block;
  margin-bottom: 12px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.role-group {
  width: 100%;
  display: flex;
  gap: 12px;
}

.role-option {
  flex: 1;
  margin-right: 0 !important;
}

.role-option :deep(.el-radio__input) {
  display: none;
}

.role-option :deep(.el-radio__label) {
  padding: 0;
  width: 100%;
}

.role-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 18px;
  border: 2px solid #e8ecef;
  border-radius: 16px;
  background: #f8f9fa;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  gap: 8px;
}

.role-content:hover {
  border-color: #667eea;
  background: white;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
}

.role-option.is-checked .role-content {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25),
              0 0 0 2px rgba(102, 126, 234, 0.15);
}

.role-icon {
  color: #95a5a6;
  transition: color 0.3s ease;
}

.role-option.is-checked .role-icon {
  color: #667eea;
}

.role-content span {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
}

/* 按钮样式 */
.register-button {
  width: 100%;
  height: 52px;
  margin-top: 28px;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 1.5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4),
              0 2px 8px rgba(118, 75, 162, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.register-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.register-button:hover::before {
  left: 100%;
}

.register-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 32px rgba(102, 126, 234, 0.5),
              0 4px 12px rgba(118, 75, 162, 0.4);
}

.register-button:active {
  transform: translateY(0);
}

/* 底部链接 */
.register-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
  margin-top: 16px;
}

.footer-text {
  font-size: 14px;
  color: #7f8c8d;
  margin-right: 8px;
}

.login-link {
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

/* 滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.register-card::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .register-content {
    max-width: 100%;
  }

  .register-card {
    padding: 30px 25px;
    max-height: 75vh;
    border-radius: 16px;
  }
  
  .title {
    font-size: 38px;
    letter-spacing: 2px;
  }
  
  .subtitle {
    font-size: 14px;
  }

  .register-header h2 {
    font-size: 20px;
  }

  .role-group {
    flex-direction: column;
  }

  .role-content {
    padding: 16px;
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
  .register-card {
    padding: 25px 20px;
  }

  .logo-icon {
    width: 75px;
    height: 75px;
  }

  .logo-svg {
    width: 38px;
    height: 38px;
  }

  .title {
    font-size: 32px;
  }
}
</style>
