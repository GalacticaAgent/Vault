<template>
  <div class="test-container">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>API 连接测试</span>
        </div>
      </template>
      
      <div class="test-content">
        <el-space direction="vertical" :size="20" style="width: 100%">
          <!-- 测试API连通性 -->
          <div>
            <h3>1. 测试后端API连接</h3>
            <el-button type="primary" @click="testApi" :loading="testing">
              测试连接
            </el-button>
            <div v-if="apiResult" class="result-box" :class="apiResult.success ? 'success' : 'error'">
              {{ apiResult.message }}
            </div>
          </div>

          <!-- 测试登录 -->
          <el-divider />
          <div>
            <h3>2. 测试登录接口</h3>
            <el-form :model="testLoginForm" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="testLoginForm.username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="testLoginForm.password" type="password" placeholder="请输入密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="testLogin" :loading="loginTesting">
                  测试登录
                </el-button>
              </el-form-item>
            </el-form>
            <div v-if="loginResult" class="result-box" :class="loginResult.success ? 'success' : 'error'">
              <pre>{{ JSON.stringify(loginResult.data, null, 2) }}</pre>
            </div>
          </div>

          <!-- 环境信息 -->
          <el-divider />
          <div>
            <h3>3. 环境信息</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="前端地址">
                {{ window.location.origin }}
              </el-descriptions-item>
              <el-descriptions-item label="API基础路径">
                {{ apiBaseUrl }}
              </el-descriptions-item>
              <el-descriptions-item label="后端地址">
                http://localhost:8080/api
              </el-descriptions-item>
              <el-descriptions-item label="代理配置">
                /api -> http://localhost:8080
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-space>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { testApi as testApiRequest, login } from '@/api/auth'

const testing = ref(false)
const loginTesting = ref(false)
const apiResult = ref(null)
const loginResult = ref(null)
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api'

const testLoginForm = reactive({
  username: '',
  password: ''
})

// 测试API连接
const testApi = async () => {
  testing.value = true
  apiResult.value = null
  
  try {
    const response = await testApiRequest()
    apiResult.value = {
      success: true,
      message: `✅ 连接成功！${response.message || response.data}`,
      data: response
    }
    ElMessage.success('API连接正常')
  } catch (error) {
    apiResult.value = {
      success: false,
      message: `❌ 连接失败：${error.message}`,
      data: error
    }
    ElMessage.error('API连接失败')
  } finally {
    testing.value = false
  }
}

// 测试登录
const testLogin = async () => {
  if (!testLoginForm.username || !testLoginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loginTesting.value = true
  loginResult.value = null
  
  try {
    const response = await login(testLoginForm)
    loginResult.value = {
      success: true,
      message: '✅ 登录成功',
      data: response.data
    }
    ElMessage.success('登录测试成功')
  } catch (error) {
    loginResult.value = {
      success: false,
      message: `❌ 登录失败：${error.response?.data?.message || error.message}`,
      data: error.response?.data || error
    }
    ElMessage.error('登录测试失败')
  } finally {
    loginTesting.value = false
  }
}
</script>

<style scoped>
.test-container {
  padding: 20px;
  min-height: 100vh;
  background: #f5f5f5;
}

.test-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.test-content {
  padding: 20px 0;
}

.result-box {
  margin-top: 15px;
  padding: 15px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

.result-box.success {
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  color: #0050b3;
}

.result-box.error {
  background: #fff2f0;
  border: 1px solid #ffccc7;
  color: #cf1322;
}

pre {
  margin: 0;
  font-family: monospace;
  font-size: 12px;
}

h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}
</style>
