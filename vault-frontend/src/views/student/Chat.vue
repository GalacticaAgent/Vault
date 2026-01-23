<template>
  <div class="chat-container">
    <!-- Left Sidebar: Recent Chats -->
    <div 
      class="chat-sidebar" 
      :class="{ 'is-collapsed': !isSidebarVisible }"
    >
      <div class="sidebar-header">
        <el-button class="new-chat-btn" round @click="handleNewChat">
          <el-icon><Plus /></el-icon> 新对话
        </el-button>
        <el-tooltip content="收起侧边栏" placement="right">
          <div class="collapse-sidebar-btn" @click="toggleSidebar">
            <el-icon><Fold /></el-icon>
          </div>
        </el-tooltip>
      </div>

      <div class="recent-section">
        <div class="section-title" @click="toggleRecentSection">
          <span>最近</span>
          <el-icon :class="{ 'is-rotated': isRecentCollapsed }"><ArrowDown /></el-icon>
        </div>
        <div class="chat-list-wrapper" :class="{ 'is-collapsed': isRecentCollapsed }">
          <div class="chat-list" v-loading="chatListLoading">
            <div 
              class="chat-item" 
              v-for="item in recentChats" 
              :key="item.id"
              :class="{ 'is-active': currentChatId === item.id }"
              @click="loadChatDetail(item.id)"
            >
              <div class="chat-item-content">
                <el-icon><Clock /></el-icon>
                <span class="chat-title">{{ item.title }}</span>
              </div>
              
              <!-- More Options Dropdown -->
              <el-dropdown trigger="click" @command="(cmd) => handleChatOptionCommand(cmd, item.id)">
                <el-icon class="more-icon" @click.stop><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu class="chat-options-dropdown">
                    <el-dropdown-item command="share" icon="Share">分享</el-dropdown-item>
                    <el-dropdown-item command="rename" icon="Edit">重命名</el-dropdown-item>
                    <el-dropdown-item command="delete" icon="Delete" class="danger-text">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div v-if="recentChats.length === 0 && !chatListLoading" class="empty-chat-list">
              暂无聊天记录
            </div>
          </div>
        </div>
      </div>
      
      <div class="version-info">Vault AI v1.2.0</div>
    </div>

    <!-- Main Chat Area -->
    <div class="chat-main">
      <div class="chat-header">
        <div class="header-left">
          <el-tooltip content="展开侧边栏" placement="bottom" v-if="!isSidebarVisible">
            <div class="expand-sidebar-btn" @click="toggleSidebar">
              <el-icon><Expand /></el-icon>
            </div>
          </el-tooltip>
          <el-icon class="stars-icon"><Opportunity /></el-icon>
          <h2>智能问答</h2>
        </div>
        <el-button text class="share-btn" @click="handleShareChat">
          <el-icon><Share /></el-icon> 分享
        </el-button>
      </div>

      <div class="chat-content" :class="{ 'empty-state': messages.length === 0 }">
        <template v-if="messages.length === 0">
          <div class="empty-placeholder">
            <el-icon class="large-icon"><MagicStick /></el-icon>
            <h3>今天需要我做什么？</h3>
          </div>
        </template>
        
        <template v-else>
          <div class="message-list">
            <div v-for="(msg, index) in messages" :key="index" class="message-item" :class="msg.role">
              <div class="message-avatar">
                <el-avatar :size="36" v-if="msg.role === 'user'" :src="userStore.userInfo?.avatar" style="background: #00bcd4">
                  {{ !userStore.userInfo?.avatar ? (userStore.userInfo?.username?.charAt(0) || '张') : '' }}
                </el-avatar>
                <div v-else-if="msg.agent" class="ai-avatar" :style="{ background: msg.agent.color }">
                  <el-icon><component :is="msg.agent.icon" /></el-icon>
                </div>
                <div v-else class="ai-avatar"><el-icon><Opportunity /></el-icon></div>
              </div>
              <div class="message-content-wrapper">
                <div class="message-bubble">
                  <div class="message-text" v-html="formatMessage(msg.content)"></div>
                </div>
                <div v-if="msg.deepThink" class="deep-think-box">
                  <div class="dt-header">
                    <el-icon><Cpu /></el-icon> 深度思考过程
                  </div>
                  <div class="dt-content">
                    正在分析用户意图...<br>
                    识别到关键词：{{ msg.keywords || '无' }}...<br>
                    检索相关知识库...<br>
                    生成回答策略...
                  </div>
                </div>
                
                <!-- AI Message Actions -->
                <div v-if="msg.role === 'ai'" class="message-actions">
                  <el-tooltip content="复制" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleCopyMessage(msg.content)"><CopyDocument /></el-icon>
                  </el-tooltip>
                  <el-tooltip content="重新生成" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleRegenerate(index)"><Refresh /></el-icon>
                  </el-tooltip>
                  <el-tooltip content="朗读" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleReadAloud(msg.content)"><VideoPlay /></el-icon>
                  </el-tooltip>
                  <el-tooltip content="赞" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleLike(index)"><CircleCheck /></el-icon>
                  </el-tooltip>
                  <el-tooltip content="踩" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleDislike(index)"><CircleClose /></el-icon>
                  </el-tooltip>
                  <el-tooltip content="分享" placement="bottom" :show-after="500">
                    <el-icon class="msg-action-icon" @click="handleShareMessage(msg.content)"><Share /></el-icon>
                  </el-tooltip>
                  <el-dropdown trigger="click">
                    <el-icon class="msg-action-icon"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item>举报</el-dropdown-item>
                        <el-dropdown-item>反馈问题</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
            
            <!-- Typing Indicator -->
            <div v-if="isTyping" class="message-item ai">
              <div class="message-avatar">
                <div class="ai-avatar"><el-icon><Opportunity /></el-icon></div>
              </div>
              <div class="message-bubble typing-bubble">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <div class="chat-input-area">
        <div class="input-box-wrapper" :class="{ 'is-focused': isInputFocused }">
          <div class="input-left">
            <input type="file" ref="fileInputRef" style="display: none" @change="handleFileChange" accept=".pdf,.py,.java,.js,.ts,.cpp,.c,.txt,.md" />
            <el-icon class="attach-icon" @click="triggerFileUpload"><Paperclip /></el-icon>
            <div class="deep-think-btn" :class="{ active: isDeepThinkActive }" @click="toggleDeepThink">
              <el-icon><Cpu /></el-icon>
              <span>深度思考</span>
            </div>
          </div>
          
          <div class="input-container">
            <input 
              ref="inputRef"
              type="text" 
              v-model="inputMessage"
              @focus="isInputFocused = true"
              @blur="handleInputBlur"
              @keydown.enter.prevent="handleSendMessage"
              @input="handleInput"
              placeholder="发消息或输入“@”选择智能体" 
              class="chat-input" 
            />
            
            <!-- Agent Selection Popover -->
            <div v-if="showAgentList" class="agent-popover" :style="agentPopoverStyle">
              <div class="agent-popover-header">选择智能体</div>
              <div class="agent-list">
                <div 
                  v-for="(agent, index) in filteredAgents" 
                  :key="agent.id"
                  class="agent-item"
                  :class="{ 'is-selected': index === selectedAgentIndex }"
                  @click="selectAgent(agent)"
                  @mouseenter="selectedAgentIndex = index"
                >
                  <div class="agent-icon" :style="{ background: agent.color }">
                    <el-icon><component :is="agent.icon" /></el-icon>
                  </div>
                  <div class="agent-info">
                    <div class="agent-name">{{ agent.name }}</div>
                    <div class="agent-desc">{{ agent.description }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="input-actions">
            <el-tooltip content="截图 (Alt+A)" placement="top">
              <el-icon class="action-icon" @click="handleScreenshot"><Scissor /></el-icon>
            </el-tooltip>
            <el-tooltip content="语音通话" placement="top">
              <el-icon class="action-icon" @click="handleVoiceCall"><Phone /></el-icon>
            </el-tooltip>
            <el-tooltip content="语音输入" placement="top">
              <el-icon class="action-icon" @click="handleVoiceInput"><Microphone /></el-icon>
            </el-tooltip>
            <div class="send-btn" @click="handleSendMessage" :class="{ 'can-send': inputMessage.trim() }">
              <el-icon><Top /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { 
  Plus, 
  Clock, 
  MoreFilled, 
  Share, 
  MagicStick, 
  Cpu, 
  Picture, 
  EditPen, 
  Reading, 
  Monitor, 
  Search, 
  Grid,
  Paperclip,
  Phone,
  Microphone,
  Top,
  Scissor,
  VideoCamera,
  Collection,
  Star,
  Edit,
  Warning,
  Delete,
  Fold,
  Expand,
  ArrowDown,
  Opportunity,
  Promotion,
  Notebook,
  DataAnalysis,
  CopyDocument,
  Refresh,
  VideoPlay,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getChatList, sendMessage, getChatDetail, deleteChat, shareChat, uploadFile } from '@/api/chat'

const userStore = useUserStore()
const recentChats = ref([])
const chatListLoading = ref(false)
const currentChatId = ref(null)

const agents = [
  { id: 'writer', name: '写作助手', description: '帮我撰写、润色文章', icon: 'EditPen', color: '#f43f5e' },
  { id: 'coder', name: '编程专家', description: '解答代码问题，生成代码', icon: 'Monitor', color: '#8b5cf6' },
  { id: 'translator', name: '翻译官', description: '多语言精准翻译', icon: 'Reading', color: '#0ea5e9' },
  { id: 'analyzer', name: '数据分析师', description: '分析数据，生成图表', icon: 'DataAnalysis', color: '#10b981' },
  { id: 'tutor', name: '学术导师', description: '学术论文辅导与建议', icon: 'Notebook', color: '#f59e0b' }
]

const messages = ref([])
const inputMessage = ref('')
const isInputFocused = ref(false)
const isDeepThinkActive = ref(false)
const isSidebarVisible = ref(true)
const isRecentCollapsed = ref(false)
const isTyping = ref(false)

// Agent selection state
const showAgentList = ref(false)
const inputRef = ref(null)
const fileInputRef = ref(null)
const selectedAgentIndex = ref(0)
const agentPopoverStyle = ref({ bottom: '100%', left: '0' })
const currentAgent = ref(null)

const filteredAgents = ref(agents)

const toggleDeepThink = () => {
  isDeepThinkActive.value = !isDeepThinkActive.value
}

const toggleSidebar = () => {
  isSidebarVisible.value = !isSidebarVisible.value
}

const toggleRecentSection = () => {
  isRecentCollapsed.value = !isRecentCollapsed.value
}

const handleInput = (e) => {
  const val = e.target.value
  const lastChar = val.slice(-1)
  
  if (lastChar === '@') {
    showAgentList.value = true
    selectedAgentIndex.value = 0
    filteredAgents.value = agents
  } else if (showAgentList.value) {
    const atIndex = val.lastIndexOf('@')
    if (atIndex !== -1) {
      const query = val.slice(atIndex + 1).toLowerCase()
      filteredAgents.value = agents.filter(a => 
        a.name.toLowerCase().includes(query) || 
        a.description.toLowerCase().includes(query)
      )
      
      if (filteredAgents.value.length === 0) {
        showAgentList.value = false
      }
    } else {
      showAgentList.value = false
    }
  }
}

const handleInputBlur = () => {
  // Delay hiding to allow click event on agent item
  setTimeout(() => {
    showAgentList.value = false
    isInputFocused.value = false
  }, 200)
}

const selectAgent = (agent) => {
  const val = inputMessage.value
  const atIndex = val.lastIndexOf('@')
  if (atIndex !== -1) {
    inputMessage.value = val.slice(0, atIndex) + '' // Remove @ part, we'll use agent context
    currentAgent.value = agent
    showAgentList.value = false
    inputRef.value.focus()
  }
}

// 文件上传相关函数
const triggerFileUpload = () => {
  fileInputRef.value?.click()
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 文件大小限制 10MB
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 10MB')
    return
  }
  
  try {
    ElMessage.info('正在上传文件...')
    const response = await uploadFile(file)
    
    // 上传成功后，在输入框中添加文件引用
    const fileInfo = response.data
    inputMessage.value += `\n[已上传文件: ${fileInfo.fileName}]`
    ElMessage.success('文件上传成功')
    
    // 清空file input
    event.target.value = ''
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败: ' + (error.message || '未知错误'))
  }
}

const handleSendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content) return

  // Add user message
  const userMsg = {
    role: 'user',
    content: content,
    agent: currentAgent.value
  }
  messages.value.push(userMsg)
  
  const usedAgent = currentAgent.value
  const useDeepThink = isDeepThinkActive.value
  
  currentAgent.value = null
  inputMessage.value = ''
  showAgentList.value = false
  isTyping.value = true
  
  try {
    // 调用后端API发送消息
    const response = await sendMessage({
      chatId: currentChatId.value,
      content: content,
      agentType: usedAgent?.id,
      useDeepThink: useDeepThink
    })
    
    // 如果是新会话，保存chatId
    if (!currentChatId.value && response.data.chatId) {
      currentChatId.value = response.data.chatId
      // 刷新聊天列表
      loadChatList()
    }
    
    // 添加AI回复
    messages.value.push({
      role: 'ai',
      content: response.data.content || response.data.reply || '抱歉，我没有理解您的问题',
      agent: usedAgent,
      deepThink: useDeepThink,
      messageId: response.data.messageId
    })
    
    isTyping.value = false
    
    // Auto scroll to bottom
    nextTick(() => {
      const container = document.querySelector('.chat-content')
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    })
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error(error.response?.data?.message || '发送消息失败，请重试')
    isTyping.value = false
    // 移除用户消息
    messages.value.pop()
  }
}

const formatMessage = (content) => {
  return content.replace(/\n/g, '<br>')
}

const handleChatOptionCommand = async (command, chatId) => {
  if (command === 'delete') {
    handleDeleteChat(chatId)
  } else if (command === 'share') {
    try {
      const response = await shareChat(chatId)
      const shareUrl = `${window.location.origin}/share/${response.data.shareId}`
      navigator.clipboard.writeText(shareUrl).then(() => {
        ElMessage.success('分享链接已复制到剪贴板')
      })
    } catch (error) {
      ElMessage.error('分享失败')
    }
  } else if (command === 'rename') {
    ElMessage.info('重命名功能开发中')
  }
}

const handleShareChat = async () => {
  if (!currentChatId.value) {
    ElMessage.warning('请先选择一个对话')
    return
  }
  
  try {
    const response = await shareChat(currentChatId.value)
    const shareUrl = `${window.location.origin}/share/${response.data.shareId}`
    navigator.clipboard.writeText(shareUrl).then(() => {
      ElMessage.success('分享链接已复制到剪贴板')
    }).catch(() => {
      ElMessage.error('复制失败，请手动复制')
    })
  } catch (error) {
    console.error('分享失败:', error)
    ElMessage.error('分享失败')
  }
}

const handleScreenshot = () => {
  ElMessage.info('正在启动截图工具...')
  // 模拟截图过程
  setTimeout(() => {
    ElMessage.success('截图已保存到剪贴板')
  }, 800)
}

const handleVoiceCall = () => {
  ElMessageBox.confirm(
    '是否发起语音通话？',
    '语音通话',
    {
      confirmButtonText: '呼叫',
      cancelButtonText: '取消',
      type: 'info',
      icon: Phone
    }
  ).then(() => {
    ElMessage.success('正在呼叫智能助手...')
  }).catch(() => {})
}

const handleVoiceInput = () => {
  ElMessage.success('正在聆听...请说话')
  // 模拟语音输入
  setTimeout(() => {
    inputMessage.value += '这是语音输入的文字'
    ElMessage.success('识别成功')
  }, 2000)
}

const handleCopyMessage = (content) => {
  navigator.clipboard.writeText(content).then(() => {
    ElMessage.success('已复制')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const handleRegenerate = (index) => {
  ElMessage.info('正在重新生成回答...')
}

const handleReadAloud = (content) => {
  ElMessage.success('正在朗读...')
}

const handleLike = (index) => {
  ElMessage.success('感谢点赞')
}

const handleDislike = (index) => {
  ElMessage.success('感谢反馈，我们将持续改进')
}

const handleShareMessage = (content) => {
  navigator.clipboard.writeText(content).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

// 加载聊天列表
const loadChatList = async () => {
  try {
    chatListLoading.value = true
    const response = await getChatList({ page: 1, size: 20 })
    // 后端返回的data直接是数组，不是分页对象
    const chatList = Array.isArray(response.data) ? response.data : []
    recentChats.value = chatList.map(chat => ({
      id: chat.id,
      title: chat.title || '新对话',
      updateTime: chat.updateTime
    }))
  } catch (error) {
    console.error('加载聊天列表失败:', error)
    recentChats.value = []
  } finally {
    chatListLoading.value = false
  }
}

// 加载聊天详情
const loadChatDetail = async (chatId) => {
  try {
    const response = await getChatDetail(chatId)
    currentChatId.value = chatId
    
    // 转换消息格式
    messages.value = response.data.messages.map(msg => ({
      role: msg.role === 'user' ? 'user' : 'ai',
      content: msg.content,
      messageId: msg.id,
      createTime: msg.createTime
    }))
    
    // 滚动到底部
    nextTick(() => {
      const container = document.querySelector('.chat-content')
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    })
  } catch (error) {
    console.error('加载聊天详情失败:', error)
    ElMessage.error('加载聊天记录失败')
  }
}

// 创建新对话
const handleNewChat = () => {
  currentChatId.value = null
  messages.value = []
  ElMessage.success('已创建新对话')
}

// 删除对话
const handleDeleteChat = async (chatId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条对话吗？删除后无法恢复。',
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteChat(chatId)
    ElMessage.success('删除成功')
    
    // 刷新列表
    loadChatList()
    
    // 如果删除的是当前对话，清空消息
    if (currentChatId.value === chatId) {
      currentChatId.value = null
      messages.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除对话失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载时获取聊天列表
onMounted(() => {
  loadChatList()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100%;
  background-color: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.02);
  overflow: hidden;
}

.chat-sidebar {
  width: 280px;
  border-right: 1px solid #f1f5f9;
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  white-space: nowrap;
  background-color: #f8fafc;
}

.chat-sidebar.is-collapsed {
  width: 0;
  padding: 24px 0;
  border-right: none;
  opacity: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.collapse-sidebar-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}

.collapse-sidebar-btn:hover {
  background-color: #e2e8f0;
  color: #1e293b;
}

.expand-sidebar-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
  margin-right: 12px;
}

.expand-sidebar-btn:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.new-chat-btn {
  flex: 1;
  justify-content: flex-start;
  color: #7c3aed;
  border-color: #ddd6fe;
  background-color: #ffffff;
  transition: all 0.3s;
  height: 48px;
  font-weight: 600;
  margin-right: 16px;
  box-shadow: 0 2px 4px rgba(124, 58, 237, 0.05);
}

.new-chat-btn:hover {
  background-color: #f5f3ff;
  border-color: #c4b5fd;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(124, 58, 237, 0.1);
}

.new-chat-btn .el-icon {
  margin-right: 8px;
}

.section-title {
  font-size: 13px;
  font-weight: 700;
  color: #94a3b8;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  user-select: none;
  padding: 0 4px;
}

.section-title:hover {
  color: #374151;
}

.section-title .el-icon {
  transition: transform 0.3s;
}

.section-title .el-icon.is-rotated {
  transform: rotate(-90deg);
}

.chat-list-wrapper {
  max-height: 500px;
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease;
  overflow: hidden;
  opacity: 1;
}

.chat-list-wrapper.is-collapsed {
  max-height: 0;
  opacity: 0;
}

.chat-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-radius: 12px;
  cursor: pointer;
  color: #334155;
  font-size: 14px;
  transition: all 0.2s ease;
  margin-bottom: 4px;
  position: relative;
}

.chat-item:hover {
  background-color: #e2e8f0;
  transform: translateX(4px);
  color: #0f172a;
}

.chat-item.is-active {
  background-color: #ddd6fe;
  color: #7c3aed;
  font-weight: 500;
}

.chat-item.is-active:hover {
  background-color: #c4b5fd;
}

.empty-chat-list {
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
  padding: 40px 20px;
}

.chat-item-content {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
  flex: 1;
}

.chat-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.more-icon {
  color: #9ca3af;
  opacity: 0;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.chat-item:hover .more-icon {
  opacity: 1;
}

.more-icon:hover {
  background-color: #e5e7eb;
  color: #6b7280;
}

.version-info {
  margin-top: auto;
  font-size: 12px;
  color: #9ca3af;
  padding-top: 20px;
  border-top: 1px solid #f3f4f6;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: #ffffff;
}

.input-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.deep-think-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  background-color: #f3f4f6;
  color: #6b7280;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.deep-think-btn:hover {
  background-color: #e5e7eb;
}

.deep-think-btn.active {
  background-color: #ede9fe;
  color: #7c3aed;
}

.send-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn.can-send {
  background-color: #000000;
}

.send-btn:hover {
  transform: scale(1.05);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  border-bottom: 1px solid #f8fafc;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars-icon {
  color: #8b5cf6;
  font-size: 20px;
}

.chat-header h2 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.share-btn {
  color: #8b5cf6;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px;
  overflow-y: auto;
}

.chat-content.empty-state {
  justify-content: center;
}

.message-list {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 24px;
}

.message-item {
  display: flex;
  gap: 16px;
  width: 100%;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #a855f7 0%, #8b5cf6 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.message-content-wrapper {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.deep-think-box {
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  font-size: 13px;
  color: #6b7280;
  animation: fadeIn 0.5s ease;
}

.dt-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #7c3aed;
  margin-bottom: 8px;
  font-size: 12px;
}

.dt-content {
  line-height: 1.6;
  padding-left: 4px;
  border-left: 2px solid #e5e7eb;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

.typing-bubble {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  width: fit-content;
}

.typing-dot {
  width: 6px;
  height: 6px;
  background-color: #9ca3af;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.message-bubble {
  padding: 14px 20px;
  border-radius: 16px;
  font-size: 15px;
  line-height: 1.7;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.message-item.ai .message-bubble {
  background-color: #f8fafc;
  color: #334155;
  border-top-left-radius: 4px;
  border: 1px solid #f1f5f9;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.2);
}

.empty-placeholder {
  text-align: center;
  color: #9ca3af;
}

.large-icon {
  font-size: 48px;
  color: #c4b5fd;
  margin-bottom: 16px;
}

.empty-placeholder h3 {
  font-size: 20px;
  font-weight: 500;
  color: #6b7280;
}

.chat-input-area {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.tools-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tool-tag {
  cursor: pointer;
  border-color: #e2e8f0;
  color: #64748b;
  background: white;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  height: 40px;
  transition: all 0.2s;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.tool-tag:hover {
  border-color: #c4b5fd;
  color: #7c3aed;
  background: #f5f3ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.1);
}

.input-box-wrapper {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 28px;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 72px;
}

.input-box-wrapper.is-focused {
  border-color: #8b5cf6;
  box-shadow: 0 12px 48px rgba(139, 92, 246, 0.12);
  transform: translateY(-4px);
}

.input-container {
  flex: 1;
  display: flex;
  align-items: center;
  position: relative;
  min-width: 0;
}

.agent-popover {
  position: absolute;
  bottom: 100%;
  left: 0;
  width: 280px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  margin-bottom: 12px;
  overflow: hidden;
  z-index: 100;
  animation: slideUp 0.2s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.agent-popover-header {
  padding: 12px 16px;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  border-bottom: 1px solid #f3f4f6;
  background: #f9fafb;
}

.agent-list {
  max-height: 240px;
  overflow-y: auto;
  padding: 4px;
}

.agent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.agent-item:hover, .agent-item.is-selected {
  background-color: #f3f4f6;
}

.agent-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.agent-info {
  flex: 1;
  overflow: hidden;
}

.agent-name {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 2px;
}

.agent-desc {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 16px;
  color: #374151;
  min-width: 0;
}

.action-icon, .attach-icon, .send-icon {
  font-size: 20px;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.2s;
  padding: 4px;
  border-radius: 4px;
}

.action-icon:hover, .attach-icon:hover {
  color: #6b7280;
  background-color: #f3f4f6;
}

.send-icon:hover {
  color: #7c3aed;
  background-color: #f5f3ff;
  transform: scale(1.1);
}

.input-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.disclaimer {
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
}

.message-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  padding-left: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.message-item:hover .message-actions {
  opacity: 1;
}

.msg-action-icon {
  color: #9ca3af;
  cursor: pointer;
  font-size: 20px;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.2s;
}

.msg-action-icon:hover {
  background-color: #f3f4f6;
  color: #6b7280;
}
</style>

