<template>
  <div class="student-layout" :class="{ 'is-collapsed': isCollapsed }">
    <aside class="sidebar" :style="{ width: isCollapsed ? '80px' : '240px' }">
      <div class="logo-container">
        <div class="logo-icon">V</div>
        <span class="logo-text" v-show="!isCollapsed">Vault CS</span>
        <el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed">
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/student/dashboard" class="nav-item" active-class="active">
          <el-tooltip :disabled="!isCollapsed" content="用户" placement="right">
            <el-icon><Grid /></el-icon>
          </el-tooltip>
          <span v-show="!isCollapsed">用户</span>
        </router-link>
        <router-link to="/student/chat" class="nav-item" active-class="active">
          <el-tooltip :disabled="!isCollapsed" content="智能问答" placement="right">
            <el-icon><ChatDotSquare /></el-icon>
          </el-tooltip>
          <span v-show="!isCollapsed">智能问答</span>
        </router-link>
        <router-link to="/student/questionnaire" class="nav-item" active-class="active">
          <el-tooltip :disabled="!isCollapsed" content="问卷测验" placement="right">
            <el-icon><Document /></el-icon>
          </el-tooltip>
          <span v-show="!isCollapsed">问卷测验</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="search-bar" v-show="!isCollapsed">
          <el-icon><Search /></el-icon>
          <input type="text" placeholder="全局搜索..." />
        </div>
        <div class="search-bar-collapsed" v-show="isCollapsed">
          <el-icon><Search /></el-icon>
        </div>
        
        <div class="user-profile">
          <el-dropdown trigger="click" @command="handleAvatarCommand">
            <el-avatar 
              :size="isCollapsed ? 40 : 32" 
              class="user-avatar" 
              :src="userAvatar"
              style="background: #a855f7"
            >
              {{ !userAvatar ? userName.charAt(0) : '' }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="change-avatar">更换头像</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div class="user-info" v-show="!isCollapsed">
            <div class="user-name">{{ userName }}</div>
            <div class="user-role">学生</div>
          </div>
        </div>
      </div>
    </aside>

    <main class="main-content">
      <router-view />
    </main>

    <!-- Hidden file input for avatar change -->
    <input 
      type="file" 
      ref="avatarInput" 
      style="display: none" 
      accept="image/*" 
      @change="onAvatarFileChange"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Grid, 
  ChatDotSquare, 
  Document, 
  Fold, 
  Expand,
  Search
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)
const avatarInput = ref(null)

const userAvatar = computed(() => {
  const avatar = userStore.userInfo?.avatar
  if (!avatar) return ''
  // 如果已经是完整URL，直接返回
  if (avatar.startsWith('http')) return avatar
  // 否则拼接API基础URL
  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${window.location.origin}${baseURL}${avatar}`
})

const userName = computed(() => {
  return userStore.userInfo?.username || '张三'
})

const handleAvatarCommand = (command) => {
  if (command === 'change-avatar') {
    // 使用 setTimeout 确保 DOM 更新后再触发点击
    setTimeout(() => {
      avatarInput.value.click()
    }, 0)
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出成功')
    // 跳转到登录页
    router.push('/login')
  }
}

const onAvatarFileChange = async (event) => {
  const file = event.target.files[0]
  if (file) {
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.error('只能上传图片文件')
      return
    }
    
    // 验证文件大小（5MB）
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过5MB')
      return
    }
    
    try {
      // 显示加载提示
      const loading = ElMessage({
        message: '正在上传头像...',
        type: 'info',
        duration: 0
      })
      
      // 上传到服务器
      await userStore.updateAvatar(file)
      
      loading.close()
      ElMessage.success('头像更换成功')
      
      // 清空文件选择，允许重复选择同一文件
      event.target.value = ''
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '头像上传失败')
    }
  }
}
</script>

<style scoped>
.student-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: #f8fafc;
  color: #1e293b;
}

.sidebar {
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f1f5f9;
  flex-shrink: 0;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.02);
  z-index: 10;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
}

.logo-icon {
  width: 24px;
  height: 24px;
  background-color: #7c3aed;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
}

.logo-text {
  font-weight: 600;
  font-size: 16px;
  color: #1f2937;
  flex: 1;
  white-space: nowrap;
}

.collapse-btn {
  cursor: pointer;
  color: #6b7280;
  font-size: 18px;
}

.nav-menu {
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 4px 0;
  white-space: nowrap;
}

.nav-item:hover {
  background-color: #f1f5f9;
  color: #0f172a;
  transform: translateX(4px);
}

.nav-item.active {
  background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
  color: #7c3aed;
  font-weight: 600;
}

.is-collapsed .nav-item {
  padding: 12px;
  justify-content: center;
  gap: 0;
}

.is-collapsed .nav-item:hover {
  transform: scale(1.1);
}

.nav-item .el-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.sidebar-footer {
  margin-top: auto;
  padding: 24px 20px;
  border-top: 1px solid #f3f4f6;
  background: linear-gradient(to bottom, #ffffff, #fafafa);
}

.is-collapsed .sidebar-footer {
  padding: 24px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #f3f4f6;
  padding: 10px 14px;
  border-radius: 12px;
  margin-bottom: 24px;
  color: #9ca3af;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.search-bar-collapsed {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  border-radius: 50%;
  margin-bottom: 20px;
  cursor: pointer;
  color: #9ca3af;
}

.search-bar:focus-within {
  background-color: #ffffff;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
  color: #7c3aed;
}

.search-bar input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 12px;
  width: 100%;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.3s;
}

.is-collapsed .user-profile {
  padding: 0;
  margin-bottom: 20px;
}

.user-profile:hover {
  background: #f9fafb;
}

.user-info {
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  white-space: nowrap;
}

.user-role {
  font-size: 12px;
  color: #6b7280;
}

.console-log {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #9ca3af;
  padding: 12px 8px;
  border-top: 1px solid #f3f4f6;
  cursor: pointer;
  transition: color 0.3s;
  white-space: nowrap;
}

.is-collapsed .console-log {
  border-top: none;
  justify-content: center;
  padding: 0;
}

.console-log:hover {
  color: #6b7280;
}

.log-count {
  background: #e5e7eb;
  padding: 0 4px;
  border-radius: 4px;
  font-size: 10px;
}

.main-content {
  flex: 1;
  overflow: auto;
  padding: 32px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
}
</style>

