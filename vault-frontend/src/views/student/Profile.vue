<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrapper" @click="triggerAvatarUpload">
          <el-avatar 
            :size="100" 
            :src="userStore.userInfo?.avatar" 
            class="profile-avatar"
          >
            {{ !userStore.userInfo?.avatar ? (userStore.userInfo?.username?.charAt(0) || '张') : '' }}
          </el-avatar>
          <div class="avatar-overlay">
            <el-icon><Camera /></el-icon>
          </div>
          <input 
            type="file" 
            ref="avatarInput" 
            accept="image/*" 
            style="display: none" 
            @change="handleAvatarChange"
          >
        </div>
        
        <div class="profile-info">
          <h2 class="user-name">{{ userStore.userInfo?.username || '张三' }}</h2>
          <div class="info-row">
            <span class="label">学号：</span>
            <span class="value">2024001</span>
          </div>
          <div class="info-row">
            <span class="label">所在课程：</span>
            <span class="value">操作系统</span>
          </div>
          
          <div class="tags-row">
            <el-tag class="profile-tag green" effect="plain" round>理论扎实</el-tag>
            <el-tag class="profile-tag purple" effect="plain" round>代码规范</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const avatarInput = ref(null)

const triggerAvatarUpload = () => {
  avatarInput.value.click()
}

const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 2MB')
      return
    }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      userStore.updateAvatar(e.target.result)
      ElMessage.success('头像更新成功')
    }
    reader.readAsDataURL(file)
  }
}
</script>

<style scoped>
.profile-container {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 32px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.profile-avatar {
  background: #8b5cf6;
  font-size: 32px;
  font-weight: 600;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.profile-info {
  flex: 1;
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.info-row {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.label {
  color: #9ca3af;
}

.value {
  color: #4b5563;
}

.tags-row {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.profile-tag {
  padding: 0 16px;
  height: 28px;
  line-height: 26px;
  border-radius: 14px;
}

.profile-tag.green {
  color: #10b981;
  border-color: #10b981;
  background-color: #ecfdf5;
}

.profile-tag.purple {
  color: #8b5cf6;
  border-color: #8b5cf6;
  background-color: #f5f3ff;
}
</style>

