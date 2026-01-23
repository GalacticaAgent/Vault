<template>
  <div class="dashboard-container">
    <div class="top-cards">
      <!-- User Profile Card -->
      <el-card class="dashboard-card user-card" shadow="hover">
        <div class="user-card-content">
          <el-avatar 
            :size="64" 
            class="large-avatar" 
            :src="avatarUrl"
            style="background: #a855f7"
          >
            {{ !avatarUrl ? studentName.charAt(0) : '' }}
          </el-avatar>
          <div class="user-details">
            <div class="name-id">
              <span class="name">{{ studentName }}</span>
              <span class="student-id">学号: {{ studentNumber }}</span>
            </div>
            <div class="course-info">所在课程: {{ currentCourse }}</div>
            <div class="tags">
              <span 
                v-for="(tag, index) in studentTags" 
                :key="index" 
                class="tag"
                :class="index % 2 === 0 ? 'green' : 'purple'"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- Learning Overview Card -->
      <el-card class="dashboard-card overview-card" shadow="hover">
        <div class="card-title">学习概览</div>
        <div class="overview-items">
          <div class="overview-item">
            <div class="label"><el-icon><Timer /></el-icon> 本周学习时间</div>
            <div class="value">{{ weeklyStudyHours }} 小时</div>
          </div>
          <div class="overview-item">
            <div class="label"><el-icon><Calendar /></el-icon> 连续活跃天数</div>
            <div class="value">{{ continuousActiveDays }} 天</div>
          </div>
          <div class="overview-item">
            <div class="label"><el-icon><Trophy /></el-icon> 综合排名</div>
            <div class="value">{{ overallRank }}</div>
          </div>
          <div class="overview-item">
            <div class="label"><el-icon><Monitor /></el-icon> 提问总数</div>
            <div class="value">{{ totalQuestions }} 次</div>
          </div>
        </div>
      </el-card>

      <!-- Recommendation Card -->
      <el-card class="dashboard-card recommend-card" shadow="hover">
        <div class="recommend-content">
          <div class="recommend-header">
            <div class="recommend-title">个性化推荐</div>
            <el-icon class="decoration-icon"><StarFilled /></el-icon>
          </div>
          <div class="recommend-body">
            <div class="sub-label">待巩固知识点</div>
            <div class="knowledge-point">{{ recommendedKnowledge }}</div>
            <div class="recommend-meta">{{ recommendedAction }}</div>
          </div>
          <el-button class="start-btn" round>开始学习</el-button>
        </div>
      </el-card>
    </div>
    
    <!-- Study Activity Chart -->
    <el-card class="dashboard-card chart-card" shadow="hover">
      <div class="card-header-wrapper">
        <div class="card-title">学习投入趋势</div>
        <el-radio-group v-model="timeRange" size="small">
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
        </el-radio-group>
      </div>
      <div class="chart-container">
        <v-chart class="chart" :option="chartOption" autoresize />
      </div>
    </el-card>

    <div class="bottom-section">
      <div class="section-header">
        <h2>我的看板</h2>
        <el-button class="add-card-btn" plain @click="showAddCardDialog">+ 添加卡片</el-button>
      </div>

      <div class="dashboard-grid">
        <!-- 自定义卡片 - 优先显示 -->
        <el-card 
          v-for="card in customCards" 
          :key="card.id" 
          class="grid-card custom-card" 
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>{{ card.title }}</span>
              <div class="header-actions">
                <el-icon @click="refreshCard(card.id)" style="cursor: pointer"><Refresh /></el-icon>
                <el-icon @click="deleteCard(card.id)" style="cursor: pointer; color: #f56c6c"><Delete /></el-icon>
              </div>
            </div>
          </template>
          <div class="custom-card-content">
            <div class="card-query">{{ card.query }}</div>
            <div class="card-data" v-if="card.content">
              <pre>{{ formatCardContent(card.content) }}</pre>
            </div>
            <div v-else class="empty-state">
              <p>数据加载中...</p>
            </div>
            <div class="card-meta">
              <span>更新时间: {{ formatTime(card.updateTime) }}</span>
            </div>
          </div>
        </el-card>

        <!-- Weakest Knowledge Points -->
        <el-card class="grid-card" shadow="hover" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>我最薄弱的知识点排行 (Top 5)</span>
              <div class="header-actions">
                <el-icon @click="loadDashboard" style="cursor: pointer"><Refresh /></el-icon>
                <el-icon><MoreFilled /></el-icon>
              </div>
            </div>
          </template>
          <div class="knowledge-list" v-if="weakPoints.length > 0">
            <div v-for="(item, index) in weakPoints" :key="index" class="knowledge-item">
              <div class="k-info">
                <span class="k-name">{{ item.name }}</span>
                <span class="k-score">{{ item.score }}%</span>
              </div>
              <el-progress 
                :percentage="item.score" 
                :color="getProgressColor(item.score)"
                :stroke-width="8"
                :show-text="false"
              />
              <div class="k-action">
                <el-button link type="primary" size="small">专项练习 ></el-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>暂无数据</p>
          </div>
        </el-card>

        <!-- Recent Progress -->
        <el-card class="grid-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近期学习进度追踪</span>
              <div class="header-actions">
                <el-icon><Refresh /></el-icon>
                <el-icon><MoreFilled /></el-icon>
              </div>
            </div>
          </template>
          <div class="progress-content">
            <div class="current-course">
              <div class="course-icon">
                <el-icon><Monitor /></el-icon>
              </div>
              <div class="course-meta">
                <div class="course-name">{{ courseName }}</div>
                <div class="course-chapter">{{ chapterName }}</div>
              </div>
            </div>
            <div class="progress-section">
              <div class="progress-label">
                <span>当前章节进度</span>
                <span>{{ learningProgress }}%</span>
              </div>
              <el-progress 
                :percentage="learningProgress" 
                :stroke-width="12" 
                color="#8b5cf6"
                :show-text="false" 
              />
            </div>
            <div class="next-step">
              <span class="label">下一节:</span>
              <span class="value">{{ sectionName }}</span>
              <el-button type="primary" round size="small" class="continue-btn">继续学习</el-button>
            </div>
          </div>
        </el-card>

        <!-- Pending Assignments -->
        <el-card class="grid-card full-width" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待完成作业提醒</span>
              <div class="header-actions">
                <el-icon><Refresh /></el-icon>
                <el-icon><MoreFilled /></el-icon>
              </div>
            </div>
          </template>
          <div class="assignment-list">
            <div v-for="(assign, index) in assignments" :key="index" class="assignment-item">
              <div class="assign-left">
                <div class="assign-icon">
                  <el-icon><EditPen /></el-icon>
                </div>
                <div class="assign-info">
                  <div class="assign-title">{{ assign.title }}</div>
                  <div class="assign-meta">
                    <span class="course-tag">{{ assign.course }}</span>
                    <span class="due-date" :class="{ urgent: assign.isUrgent }">截止: {{ assign.dueDate }}</span>
                  </div>
                </div>
              </div>
              <el-button type="primary" plain round size="small">去完成</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 添加卡片对话框 -->
    <el-dialog 
      v-model="addCardDialogVisible" 
      title="添加自定义卡片" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="cardForm" label-width="100px">
        <el-form-item label="卡片标题">
          <el-input v-model="cardForm.title" placeholder="例如：我的薄弱知识点" />
        </el-form-item>
        <el-form-item label="查询描述">
          <el-input 
            v-model="cardForm.query" 
            type="textarea" 
            :rows="3"
            placeholder="例如：列出我最薄弱的 5 个知识点"
          />
        </el-form-item>
        <el-form-item label="刷新间隔">
          <el-select v-model="cardForm.refreshInterval" placeholder="选择刷新频率">
            <el-option label="不自动刷新" :value="0" />
            <el-option label="每30秒" :value="30000" />
            <el-option label="每分钟" :value="60000" />
            <el-option label="每5分钟" :value="300000" />
            <el-option label="每10分钟" :value="600000" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddCard" :loading="addingCard">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Timer, 
  Calendar, 
  Trophy, 
  StarFilled, 
  Refresh, 
  MoreFilled,
  Monitor,
  EditPen,
  Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentDashboard, addDashboardCard, deleteDashboardCard, refreshDashboardCard } from '@/api/student'
import { useUserStore } from '@/store/modules/user'

// ECharts imports
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent
} from 'echarts/components'

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  GridComponent,
  TooltipComponent,
  LegendComponent
])

// 引入用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const dashboardData = ref(null)
const timeRange = ref('week')

// 基本信息
const basicStats = computed(() => dashboardData.value?.basicStats || {})
const studentName = computed(() => basicStats.value.name || '学生')
const studentNumber = computed(() => basicStats.value.studentNumber || '')
const currentCourse = computed(() => basicStats.value.currentCourse || '暂无课程')
const studentTags = computed(() => basicStats.value.tags || [])
const weeklyStudyHours = computed(() => basicStats.value.weeklyStudyHours || 0)
const continuousActiveDays = computed(() => basicStats.value.continuousActiveDays || 0)
const overallRank = computed(() => basicStats.value.overallRankPercentile || 'N/A')
const totalQuestions = computed(() => basicStats.value.totalQuestions || 0)
const totalScores = computed(() => basicStats.value.totalScores || 0)

// 头像URL - 将相对路径转换为完整URL
const avatarUrl = computed(() => {
  const avatar = userStore.userInfo?.avatar
  if (!avatar) return ''
  // 如果已经是完整URL，直接返回
  if (avatar.startsWith('http')) return avatar
  // 否则拼接API基础URL
  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${window.location.origin}${baseURL}${avatar}`
})

// 薄弱知识点
const weakPoints = computed(() => {
  const weakKnowledgePoints = dashboardData.value?.weakKnowledgePoints || []
  return weakKnowledgePoints.map(point => ({
    name: point.knowledgeName,
    score: Math.round((point.proficiency || 0) * 100)
  }))
})

// 个性化推荐 - 基于第一个薄弱知识点
const recommendedKnowledge = computed(() => {
  const weakKnowledgePoints = dashboardData.value?.weakKnowledgePoints || []
  if (weakKnowledgePoints.length > 0) {
    return weakKnowledgePoints[0].knowledgeName
  }
  return '暂无推荐'
})

const recommendedAction = computed(() => {
  const weakKnowledgePoints = dashboardData.value?.weakKnowledgePoints || []
  if (weakKnowledgePoints.length > 0) {
    const point = weakKnowledgePoints[0]
    return point.suggestion || '推荐练习相关题目'
  }
  return '继续加油学习'
})

// 当前学习进度
const currentLearningProgress = computed(() => dashboardData.value?.currentLearningProgress || {})
const courseName = computed(() => currentLearningProgress.value.courseName || '暂无课程')
const chapterName = computed(() => currentLearningProgress.value.chapterName || '暂无章节')
const sectionName = computed(() => currentLearningProgress.value.sectionName || '暂无小节')
const learningProgress = computed(() => currentLearningProgress.value.progress || 0)

// 图表数据 - 从后端获取学习趋势
const chartOption = computed(() => {
  const studyTrends = dashboardData.value?.studyTrends || []
  const days = studyTrends.map(t => t.dayOfWeek || 'N/A')
  const hours = studyTrends.map(t => t.studyHours || 0)
  
  // 如果没有数据，使用默认值
  const xAxisData = days.length > 0 ? days : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  const seriesData = hours.length > 0 ? hours : [2.5, 4.0, 3.2, 5.5, 3.8, 6.0, 4.5]
  
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#9ca3af' }
    },
    yAxis: {
      type: 'value',
      splitLine: { 
        lineStyle: { type: 'dashed', color: '#f3f4f6' } 
      },
      axisLabel: { color: '#9ca3af' }
    },
    series: [
      {
        name: '学习时长 (h)',
        type: 'bar',
        barWidth: '40%',
        data: seriesData,
        itemStyle: {
          color: '#8b5cf6',
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: { color: '#7c3aed' }
        }
      }
    ]
  }
})

const getProgressColor = (score) => {
  if (score < 60) return '#ef4444'
  if (score < 80) return '#eab308'
  return '#10b981'
}

const assignments = [
  { 
    title: 'Lab 3: 内存管理实现', 
    course: '操作系统', 
    dueDate: '明天 23:59', 
    isUrgent: true 
  },
  { 
    title: '期中复习测验', 
    course: '计算机网络', 
    dueDate: '下周一 12:00', 
    isUrgent: false 
  },
  {
    title: '数据库设计文档',
    course: '数据库系统',
    dueDate: '2024-05-20',
    isUrgent: false
  }
]

// 自定义卡片相关
const addCardDialogVisible = ref(false)
const addingCard = ref(false)
const cardForm = ref({
  title: '',
  query: '',
  refreshInterval: 0
})

// 自定义卡片列表
const customCards = computed(() => dashboardData.value?.customCards || [])

// 显示添加卡片对话框
const showAddCardDialog = () => {
  cardForm.value = {
    title: '',
    query: '',
    refreshInterval: 0
  }
  addCardDialogVisible.value = true
}

// 添加新卡片
const handleAddCard = async () => {
  if (!cardForm.value.title || !cardForm.value.query) {
    ElMessage.warning('请填写卡片标题和查询内容')
    return
  }
  
  try {
    addingCard.value = true
    await addDashboardCard({
      title: cardForm.value.title,
      query: cardForm.value.query,
      refreshInterval: cardForm.value.refreshInterval
    })
    ElMessage.success('卡片添加成功')
    addCardDialogVisible.value = false
    await loadDashboard() // 重新加载数据
  } catch (error) {
    console.error('添加卡片失败:', error)
    ElMessage.error('添加卡片失败')
  } finally {
    addingCard.value = false
  }
}

// 刷新卡片
const refreshCard = async (cardId) => {
  try {
    await refreshDashboardCard(cardId)
    ElMessage.success('卡片已刷新')
    await loadDashboard() // 重新加载数据
  } catch (error) {
    console.error('刷新卡片失败:', error)
    ElMessage.error('刷新卡片失败')
  }
}

// 删除卡片
const deleteCard = async (cardId) => {
  try {
    await ElMessageBox.confirm('确定删除此卡片？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDashboardCard(cardId)
    ElMessage.success('卡片已删除')
    await loadDashboard() // 重新加载数据
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除卡片失败:', error)
    ElMessage.error('删除卡片失败')
  }
}

// 格式化卡片内容
const formatCardContent = (content) => {
  if (!content) return '暂无数据'
  // 如果内容是数组，格式化为列表
  if (Array.isArray(content)) {
    return content.join('\n')
  }
  // 如果内容是对象，格式化为键值对
  if (typeof content === 'object') {
    return Object.entries(content)
      .map(([key, value]) => `${key}: ${value}`)
      .join('\n')
  }
  return content
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 小于24小时
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 小于7天
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  // 超过7天显示日期
  return date.toLocaleDateString()
}

// 加载看板数据
const loadDashboard = async () => {
  try {
    loading.value = true
    const response = await getStudentDashboard()
    dashboardData.value = response.data
    console.log('Dashboard data loaded:', dashboardData.value)
    console.log('Study trends:', dashboardData.value?.studyTrends)
  } catch (error) {
    console.error('加载看板数据失败:', error)
    ElMessage.error('加载看板数据失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(async () => {
  // 先获取用户信息（包括头像）
  try {
    await userStore.getUserInfo()
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
  // 然后加载看板数据
  loadDashboard()
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.top-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.dashboard-card {
  height: 200px;
  border-radius: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.dashboard-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.08);
  border-color: #e5e7eb;
}

.user-card-content {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.name-id {
  display: flex;
  flex-direction: column;
}

.name {
  font-size: 20px;
  font-weight: bold;
  color: #1f2937;
}

.student-id {
  font-size: 12px;
  color: #6b7280;
}

.course-info {
  font-size: 14px;
  color: #4b5563;
}

.tags {
  display: flex;
  gap: 8px;
}

.tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid;
}

.tag.green {
  color: #10b981;
  border-color: #10b981;
  background: #ecfdf5;
}

.tag.purple {
  color: #8b5cf6;
  border-color: #8b5cf6;
  background: #f5f3ff;
}

.chart-card {
  height: 360px;
  display: flex;
  flex-direction: column;
}

.chart-card :deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.card-header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  flex-shrink: 0;
}

.chart-container {
  flex: 1;
  min-height: 0;
  padding: 0 20px 20px;
}

.chart {
  height: 100%;
  width: 100%;
  min-height: 250px;
}

.overview-card .card-title {
  font-weight: bold;
  margin-bottom: 16px;
}

.overview-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.overview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-item .label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.overview-item .value {
  font-weight: bold;
  color: #1f2937;
}

.recommend-card {
  background: linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.recommend-card :deep(.el-card__body) {
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.recommend-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.recommend-title {
  font-size: 14px;
  opacity: 0.9;
}

.decoration-icon {
  font-size: 40px;
  opacity: 0.2;
  position: absolute;
  top: 10px;
  right: 10px;
}

.recommend-body {
  margin-top: 8px;
}

.sub-label {
  font-size: 12px;
  opacity: 0.8;
}

.knowledge-point {
  font-size: 18px;
  font-weight: bold;
  margin: 4px 0 8px;
}

.recommend-meta {
  font-size: 12px;
  opacity: 0.9;
  line-height: 1.4;
}

.start-btn {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: white;
  width: 100%;
  margin-top: auto;
  backdrop-filter: blur(4px);
  transition: all 0.3s;
  font-weight: 500;
}

.start-btn:hover {
  background: rgba(255, 255, 255, 0.4);
  color: white;
  transform: scale(1.02);
}

.bottom-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  font-size: 16px;
  color: #4b5563;
  margin: 0;
}

.add-card-btn {
  color: #8b5cf6;
  background: #f5f3ff;
  border-color: #ddd6fe;
  transition: all 0.2s;
}

.add-card-btn:hover {
  background: #ede9fe;
  border-color: #c4b5fd;
  color: #7c3aed;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.grid-card {
  border-radius: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.grid-card:hover {
  box-shadow: 0 8px 16px -6px rgba(0, 0, 0, 0.06);
}

.full-width {
  grid-column: span 2;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #1f2937;
}

.header-actions {
  display: flex;
  gap: 12px;
  color: #9ca3af;
  cursor: pointer;
}

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.knowledge-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.k-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #4b5563;
}

.k-name {
  font-weight: 500;
}

.k-score {
  font-family: monospace;
}

.k-action {
  display: flex;
  justify-content: flex-end;
}

.progress-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.current-course {
  display: flex;
  gap: 12px;
  align-items: center;
}

.course-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8b5cf6;
  font-size: 20px;
}

.course-meta {
  flex: 1;
}

.course-name {
  font-weight: 600;
  font-size: 14px;
  color: #1f2937;
}

.course-chapter {
  font-size: 12px;
  color: #6b7280;
}

.progress-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
}

.next-step {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  background: #f9fafb;
  padding: 8px 12px;
  border-radius: 8px;
}

.next-step .label {
  color: #6b7280;
}

.next-step .value {
  flex: 1;
  font-weight: 500;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.assignment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.assignment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  background-color: #ffffff;
  border: 1px solid #f3f4f6;
  transition: all 0.2s;
}

.assignment-item:hover {
  background-color: #f9fafb;
  border-color: #e5e7eb;
}

.assign-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.assign-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #fff1f2;
  color: #f43f5e;
  display: flex;
  align-items: center;
  justify-content: center;
}

.assign-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.assign-title {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.assign-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
  align-items: center;
}

.course-tag {
  background: #f3f4f6;
  padding: 1px 6px;
  border-radius: 4px;
}

.due-date.urgent {
  color: #ef4444;
  font-weight: 500;
}
</style>

