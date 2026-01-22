<template>
  <div class="questionnaire-container">
    <div class="page-header" v-if="!currentQuiz">
      <h2>问卷测验</h2>
      <div class="header-tabs">
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'classroom' }"
          @click="currentTab = 'classroom'"
        >
          <el-icon><Timer /></el-icon> 课堂检测
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'homework' }"
          @click="currentTab = 'homework'"
        >
          <el-icon><Notebook /></el-icon> 课后作业
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'practice' }"
          @click="currentTab = 'practice'"
        >
          <el-icon><Reading /></el-icon> 自主练习
        </div>
      </div>
    </div>

    <div class="content-area" v-if="!currentQuiz">
      <!-- Classroom Tests Tab -->
      <template v-if="currentTab === 'classroom'">
        <!-- Notification Banner -->
        <div class="notification-banner">
          <div class="banner-content">
            <el-icon class="warning-icon"><WarningFilled /></el-icon>
            <div class="banner-text">
              <span class="banner-title">新的课堂测验已发布</span>
              <span class="banner-desc">🚀 进程通信专题测验 - 6题, 25分钟 刚刚发布</span>
            </div>
          </div>
          <el-icon class="close-icon"><Close /></el-icon>
        </div>

        <!-- Info Box -->
        <div class="info-box">
          <div class="info-header">
            <el-icon><InfoFilled /></el-icon> 课堂检测说明
          </div>
          <div class="info-content">
            课堂检测由教师在课上发起，有时间限制，需在规定时间内完成。系统自动保存进度，可随时继续作答。
          </div>
        </div>

        <!-- Quiz Cards -->
        <div class="quiz-list">
          <!-- Card 1: In Progress -->
          <div class="quiz-card active">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>Lab3随堂测验-进程调度</h3>
                  <span class="status-tag process">进行中</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 3 题</span>
                  <span><el-icon><Timer /></el-icon> 30 分钟</span>
                  <span class="difficulty medium">中等</span>
                </div>
              </div>
              <div class="timer-display">15:23</div>
            </div>
            
            <div class="quiz-progress-section">
              <div class="progress-label">剩余时间</div>
              <el-progress :percentage="50" :show-text="false" color="#f97316" :stroke-width="8" />
            </div>

            <el-button class="action-btn orange" round @click="startQuiz(1)">继续作答</el-button>
          </div>

          <!-- Card 2: Completed -->
          <div class="quiz-card">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>虚拟内存快速测验</h3>
                  <span class="status-tag completed">已完成</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 2 题</span>
                  <span><el-icon><Timer /></el-icon> 20 分钟</span>
                  <span class="difficulty hard">困难</span>
                </div>
              </div>
              <div class="score-display">
                <span class="score-val">100</span>
                <span class="score-unit">分</span>
              </div>
            </div>

            <el-button class="action-btn purple" round>查看详情</el-button>
          </div>
        </div>
      </template>

      <!-- Homework Tab -->
      <template v-if="currentTab === 'homework'">
        <div class="info-box blue-theme">
          <div class="info-header">
            <el-icon><Notebook /></el-icon> 作业说明
          </div>
          <div class="info-content">
            课后作业用于巩固知识点，请在截止日期前完成提交。作业成绩将计入平时分。
          </div>
        </div>

        <div class="quiz-list">
          <div class="quiz-card">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>第三章：内存管理作业</h3>
                  <span class="status-tag pending">未开始</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 10 题</span>
                  <span>截止：2023-11-20</span>
                  <span class="difficulty hard">困难</span>
                </div>
              </div>
            </div>
            <el-button class="action-btn blue" round @click="startQuiz(2)">开始作业</el-button>
          </div>

          <div class="quiz-card">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>第二章：进程控制作业</h3>
                  <span class="status-tag completed">已批改</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 8 题</span>
                  <span>提交：2023-11-10</span>
                  <span class="difficulty medium">中等</span>
                </div>
              </div>
              <div class="score-display">
                <span class="score-val">95</span>
                <span class="score-unit">分</span>
              </div>
            </div>
            <el-button class="action-btn purple" round>查看解析</el-button>
          </div>
        </div>
      </template>

      <!-- Practice Tab -->
      <template v-if="currentTab === 'practice'">
        <div class="info-box green-theme">
          <div class="info-header">
            <el-icon><Reading /></el-icon> 自主练习
          </div>
          <div class="info-content">
            海量题库供你刷题，系统会根据你的薄弱知识点智能推荐题目，提升学习效率。
          </div>
        </div>

        <div class="quiz-list">
          <div class="quiz-card active">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>每日一练：死锁预防</h3>
                  <span class="status-tag new">推荐</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 5 题</span>
                  <span><el-icon><Timer /></el-icon> 不限时</span>
                  <span class="difficulty medium">中等</span>
                </div>
              </div>
            </div>
            <el-button class="action-btn green" round @click="startQuiz(3)">开始练习</el-button>
          </div>

          <div class="quiz-card">
            <div class="card-top">
              <div class="quiz-main-info">
                <div class="quiz-title-row">
                  <h3>错题本重练</h3>
                  <span class="status-tag process">待复习</span>
                </div>
                <div class="quiz-meta">
                  <span><el-icon><Document /></el-icon> 12 题</span>
                  <span>来自历史错题</span>
                </div>
              </div>
            </div>
            <el-button class="action-btn green" round>去复习</el-button>
          </div>
        </div>
      </template>
    </div>

    <!-- Quiz Taking Interface -->
    <div class="quiz-taking-area" v-else>
      <div class="quiz-header">
        <el-button link @click="currentQuiz = null">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
        <div class="quiz-title">{{ currentQuiz.title }}</div>
        <div class="quiz-timer">
          <el-icon><Timer /></el-icon> {{ timer }}
        </div>
      </div>

      <div class="quiz-body">
        <div class="question-nav">
          <div class="nav-title">题目导航</div>
          <div class="nav-grid">
            <div 
              v-for="(q, index) in currentQuiz.questions" 
              :key="q.id"
              class="nav-item"
              :class="{ 'active': currentQuestionIndex === index, 'answered': userAnswers[q.id] }"
              @click="currentQuestionIndex = index"
            >
              {{ index + 1 }}
            </div>
          </div>
          <el-button type="primary" class="submit-btn" @click="submitQuiz">提交试卷</el-button>
        </div>

        <div class="question-content">
          <div class="question-header">
            <span class="q-type">{{ currentQuestion.type === 'single' ? '单选题' : '多选题' }}</span>
            <span class="q-score">({{ currentQuestion.score }}分)</span>
          </div>
          <div class="question-text">{{ currentQuestionIndex + 1 }}. {{ currentQuestion.content }}</div>
          
          <div class="options-list">
            <div 
              v-for="(opt, idx) in currentQuestion.options" 
              :key="idx"
              class="option-item"
              :class="{ 'selected': isOptionSelected(opt.key) }"
              @click="selectOption(opt.key)"
            >
              <div class="option-key">{{ opt.key }}</div>
              <div class="option-text">{{ opt.text }}</div>
            </div>
          </div>

          <div class="question-actions">
            <el-button :disabled="currentQuestionIndex === 0" @click="currentQuestionIndex--">上一题</el-button>
            <el-button type="primary" :disabled="currentQuestionIndex === currentQuiz.questions.length - 1" @click="currentQuestionIndex++">下一题</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { 
  Timer, 
  Notebook, 
  Reading, 
  WarningFilled, 
  Close, 
  InfoFilled, 
  Document,
  ArrowLeft 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const currentQuiz = ref(null)
const currentQuestionIndex = ref(0)
const currentTab = ref('classroom')
const userAnswers = ref({})
const timer = ref('15:23')
let timerInterval = null

// Mock Quiz Data
const mockQuizData = {
  id: 1,
  title: 'Lab3随堂测验-进程调度',
  questions: [
    {
      id: 101,
      type: 'single',
      score: 10,
      content: '在操作系统中，进程控制块（PCB）不包含以下哪项信息？',
      options: [
        { key: 'A', text: '进程状态' },
        { key: 'B', text: '程序计数器' },
        { key: 'C', text: '磁盘剩余空间' },
        { key: 'D', text: '进程优先级' }
      ]
    },
    {
      id: 102,
      type: 'single',
      score: 10,
      content: '下列关于时间片轮转调度算法的描述，错误的是？',
      options: [
        { key: 'A', text: '时间片过大会退化为FCFS算法' },
        { key: 'B', text: '时间片过小会导致上下文切换开销过大' },
        { key: 'C', text: '适用于分时系统' },
        { key: 'D', text: '绝对保证了短作业优先' }
      ]
    },
    {
      id: 103,
      type: 'multi',
      score: 20,
      content: '产生死锁的必要条件包括哪些？',
      options: [
        { key: 'A', text: '互斥条件' },
        { key: 'B', text: '请求与保持条件' },
        { key: 'C', text: '不可剥夺条件' },
        { key: 'D', text: '循环等待条件' }
      ]
    }
  ]
}

const currentQuestion = computed(() => {
  return currentQuiz.value ? currentQuiz.value.questions[currentQuestionIndex.value] : {}
})

const startQuiz = (id) => {
  currentQuiz.value = mockQuizData
  startTimer()
}

const startTimer = () => {
  let [min, sec] = timer.value.split(':').map(Number)
  timerInterval = setInterval(() => {
    if (sec === 0) {
      if (min === 0) {
        clearInterval(timerInterval)
        submitQuiz()
        return
      }
      min--
      sec = 59
    } else {
      sec--
    }
    timer.value = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
  }, 1000)
}

const selectOption = (key) => {
  const qId = currentQuestion.value.id
  if (currentQuestion.value.type === 'single') {
    userAnswers.value[qId] = key
  } else {
    // Multi-select logic
    const current = userAnswers.value[qId] || []
    const idx = current.indexOf(key)
    if (idx === -1) {
      userAnswers.value[qId] = [...current, key].sort()
    } else {
      userAnswers.value[qId] = current.filter(k => k !== key)
    }
  }
}

const isOptionSelected = (key) => {
  const ans = userAnswers.value[currentQuestion.value.id]
  if (Array.isArray(ans)) {
    return ans.includes(key)
  }
  return ans === key
}

const submitQuiz = () => {
  ElMessageBox.confirm(
    '确定要提交试卷吗？提交后将无法修改答案。',
    '提交确认',
    {
      confirmButtonText: '确定提交',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    clearInterval(timerInterval)
    ElMessage({
      type: 'success',
      message: '试卷提交成功！得分：100分',
    })
    currentQuiz.value = null
  }).catch(() => {})
}

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})
</script>

<style scoped>
.questionnaire-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0;
}

.header-tabs {
  display: flex;
  gap: 32px;
  border-bottom: 1px solid #e5e7eb;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 4px 12px;
  color: #6b7280;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: #7c3aed;
  opacity: 0.8;
}

.tab-item.active {
  color: #7c3aed;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #7c3aed;
}

.content-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.notification-banner {
  background: linear-gradient(90deg, #fff7ed 0%, #fffaf5 100%);
  border: 1px solid #fed7aa;
  border-radius: 12px;
  padding: 14px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #c2410c;
  box-shadow: 0 2px 8px rgba(251, 146, 60, 0.05);
  transition: transform 0.3s;
}

.notification-banner:hover {
  transform: scale(1.005);
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.warning-icon {
  font-size: 18px;
  color: #ea580c;
}

.banner-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.banner-title {
  font-weight: bold;
  font-size: 14px;
}

.banner-desc {
  font-size: 12px;
  opacity: 0.9;
}

.close-icon {
  cursor: pointer;
  font-size: 16px;
}

.info-box {
  background-color: #eff6ff;
  border-radius: 8px;
  padding: 16px;
  color: #1e40af;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-content {
  font-size: 13px;
  line-height: 1.5;
  opacity: 0.9;
}

.quiz-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.quiz-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
  border: 1px solid #f0f0f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quiz-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.08);
  border-color: #e5e7eb;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.quiz-main-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quiz-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quiz-title-row h3 {
  margin: 0;
  font-size: 16px;
  color: #1f2937;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid;
}

.status-tag.process {
  color: #ea580c;
  border-color: #ea580c;
  background: #fff7ed;
}

.status-tag.completed {
  color: #059669;
  border-color: #059669;
  background: #ecfdf5;
}

.quiz-meta {
  display: flex;
  gap: 16px;
  color: #6b7280;
  font-size: 13px;
}

.quiz-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.difficulty {
  padding: 1px 6px;
  border-radius: 4px;
  background: #f3f4f6;
  font-size: 12px;
}

.difficulty.medium {
  background: #fff7ed;
  color: #c2410c;
}

.difficulty.hard {
  background: #fef2f2;
  color: #b91c1c;
}

.timer-display {
  font-size: 20px;
  font-weight: bold;
  color: #ea580c;
}

.score-display {
  text-align: right;
  color: #059669;
}

.score-val {
  font-size: 24px;
  font-weight: bold;
}

.score-unit {
  font-size: 12px;
}

.quiz-progress-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-label {
  font-size: 12px;
  color: #6b7280;
}

.action-btn {
  width: 100%;
  font-weight: 600;
  height: 44px;
  transition: all 0.3s;
  letter-spacing: 1px;
}

.action-btn.orange {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.2);
}

.action-btn.orange:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
}

.action-btn.purple {
  background: linear-gradient(135deg, #a855f7 0%, #8b5cf6 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.info-box.blue-theme {
  background: #eff6ff;
  border-color: #dbeafe;
}

.info-box.blue-theme .info-header {
  color: #2563eb;
}

.info-box.green-theme {
  background: #f0fdf4;
  border-color: #dcfce7;
}

.info-box.green-theme .info-header {
  color: #16a34a;
}

.status-tag.pending {
  background: #f3f4f6;
  color: #6b7280;
}

.status-tag.new {
  background: #dcfce7;
  color: #16a34a;
}

.action-btn.blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
}

.action-btn.blue:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

.action-btn.green {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
  border: none;
}

.action-btn.green:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(22, 163, 74, 0.3);
}

.quiz-taking-area {
  background: white;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  overflow: hidden;
}

.quiz-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9fafb;
}

.quiz-title {
  font-weight: 600;
  color: #1f2937;
  font-size: 16px;
}

.quiz-timer {
  font-family: monospace;
  font-size: 18px;
  font-weight: bold;
  color: #ea580c;
  display: flex;
  align-items: center;
  gap: 8px;
}

.quiz-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.question-nav {
  width: 280px;
  border-right: 1px solid #e5e7eb;
  padding: 20px;
  display: flex;
  flex-direction: column;
  background: #fcfcfc;
}

.nav-title {
  font-weight: 600;
  margin-bottom: 16px;
  color: #374151;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-bottom: 24px;
  flex: 1;
  overflow-y: auto;
}

.nav-item {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  font-size: 14px;
  color: #6b7280;
  transition: all 0.2s;
}

.nav-item:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.nav-item.active {
  background: #8b5cf6;
  color: white;
  border-color: #8b5cf6;
}

.nav-item.answered {
  background: #e0e7ff;
  color: #4f46e5;
  border-color: #c7d2fe;
}

.nav-item.active.answered {
  background: #8b5cf6;
  color: white;
  border-color: #8b5cf6;
}

.submit-btn {
  width: 100%;
  margin-top: auto;
}

.question-content {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.q-type {
  background: #f3f4f6;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  color: #4b5563;
}

.q-score {
  color: #9ca3af;
  font-size: 14px;
}

.question-text {
  font-size: 18px;
  font-weight: 500;
  color: #1f2937;
  line-height: 1.6;
  margin-bottom: 32px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 40px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-item:hover {
  border-color: #8b5cf6;
  background: #f5f3ff;
}

.option-item.selected {
  border-color: #8b5cf6;
  background: #ede9fe;
}

.option-key {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #6b7280;
  background: white;
}

.option-item.selected .option-key {
  background: #8b5cf6;
  color: white;
  border-color: #8b5cf6;
}

.option-text {
  font-size: 15px;
  color: #374151;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
}
</style>

