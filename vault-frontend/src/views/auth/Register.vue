<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>Vault 智能教学系统</h1>
        <p>创建新账号</p>
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
        <el-form-item label="身份" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="STUDENT">学生</el-radio>
            <el-radio label="TEACHER">教师</el-radio>
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
            注册
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
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
          
          ElMessage.success('注册成功')
          
          // 根据角色跳转到不同页面
          const role = response.data.userInfo.role
          if (role === 'STUDENT') {
            router.push('/student')
          } else if (role === 'TEACHER') {
            router.push('/teacher')
          } else {
            router.push('/')
          }
        }
      } catch (error) {
        ElMessage.error(error.message || '注册失败，请稍后重试')
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
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 500px;
  max-height: 90vh;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.register-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.register-form {
  margin-top: 20px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-footer span {
  margin-right: 8px;
}

/* 滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}
</style>
