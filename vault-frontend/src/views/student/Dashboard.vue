<template>
  <div class="dashboard-container">
    <div class="top-cards">
      <!-- User Profile Card -->
      <el-card class="dashboard-card user-card" shadow="hover">
        <div class="user-card-content">
          <el-avatar :size="64" class="large-avatar" style="background: #a855f7">张</el-avatar>
          <div class="user-details">
            <div class="name-id">
              <span class="name">张三</span>
              <span class="student-id">学号: 2024001</span>
            </div>
            <div class="course-info">所在课程: 操作系统</div>
            <div class="tags">
              <span class="tag green">理论扎实</span>
              <span class="tag purple">代码规范</span>
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
            <div class="value">12.5 小时</div>
          </div>
          <div class="overview-item">
            <div class="label"><el-icon><Calendar /></el-icon> 连续活跃天数</div>
            <div class="value">15 天</div>
          </div>
          <div class="overview-item">
            <div class="label"><el-icon><Trophy /></el-icon> 综合排名</div>
            <div class="value">Top 15%</div>
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
            <div class="knowledge-point">并发控制 · 信号量机制</div>
            <div class="recommend-meta">推荐练习<br>3 道相关题目</div>
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
        <el-button class="add-card-btn" plain>+ 添加卡片</el-button>
      </div>

      <div class="dashboard-grid">
        <!-- Weakest Knowledge Points -->
        <el-card class="grid-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>我最薄弱的知识点排行 (Top 5)</span>
              <div class="header-actions">
                <el-icon><Refresh /></el-icon>
                <el-icon><MoreFilled /></el-icon>
              </div>
            </div>
          </template>
          <div class="knowledge-list">
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
                <div class="course-name">操作系统 (Operating System)</div>
                <div class="course-chapter">第 4 章：文件管理系统</div>
              </div>
            </div>
            <div class="progress-section">
              <div class="progress-label">
                <span>当前章节进度</span>
                <span>70%</span>
              </div>
              <el-progress 
                :percentage="70" 
                :stroke-width="12" 
                color="#8b5cf6"
                :show-text="false" 
              />
            </div>
            <div class="next-step">
              <span class="label">下一节:</span>
              <span class="value">4.2 文件目录结构</span>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Timer, 
  Calendar, 
  Trophy, 
  StarFilled, 
  Refresh, 
  MoreFilled,
  Monitor,
  EditPen
} from '@element-plus/icons-vue'

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

const timeRange = ref('week')

const chartOption = computed(() => ({
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
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
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
      data: [2.5, 4.0, 3.2, 5.5, 3.8, 6.0, 4.5],
      itemStyle: {
        color: '#8b5cf6',
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: { color: '#7c3aed' }
      }
    }
  ]
}))

const weakPoints = [
  { name: '并发控制', score: 68 },
  { name: '死锁处理', score: 72 },
  { name: '设备驱动', score: 75 }
]

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
  height: 320px;
  display: flex;
  flex-direction: column;
}

.card-header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px 0;
}

.chart-container {
  flex: 1;
  min-height: 0;
  padding: 10px;
}

.chart {
  height: 100%;
  width: 100%;
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

