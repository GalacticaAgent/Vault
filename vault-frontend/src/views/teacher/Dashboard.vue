<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>教师看板</h1>
      <p class="subtitle">欢迎使用 Vault 智能教学系统</p>
    </div>
    
    <div class="dashboard-content">
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
          </div>
        </template>
        <div class="user-info">
          <p><strong>用户名：</strong>{{ userInfo?.username }}</p>
          <p><strong>工号：</strong>{{ userInfo?.teacherNumber }}</p>
          <p><strong>院系：</strong>{{ userInfo?.department || '未填写' }}</p>
          <p><strong>职称：</strong>{{ userInfo?.title || '未填写' }}</p>
        </div>
      </el-card>
      
      <el-card class="tip-card">
        <template #header>
          <div class="card-header">
            <span>💡 提示</span>
          </div>
        </template>
        <div class="tips">
          <p>• 如需退出登录，请点击右上角的"退出登录"按钮</p>
          <p>• 退出后可以重新登录或注册新账号</p>
          <p>• 更多功能正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
}

.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #333;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.dashboard-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.user-info p {
  margin: 12px 0;
  font-size: 14px;
  line-height: 1.6;
}

.tips p {
  margin: 12px 0;
  font-size: 14px;
  line-height: 1.8;
  color: #666;
}
</style>

