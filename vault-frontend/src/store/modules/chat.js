/**
 * 聊天状态模块
 */

import { defineStore } from 'pinia'

export const useChatStore = defineStore('chat', {
  state: () => ({
    currentChatId: null,
    chatList: [],
    messages: {}
  }),
  
  getters: {
    currentChat: (state) => {
      return state.chatList.find(chat => chat.id === state.currentChatId) || null
    },
    currentMessages: (state) => {
      return state.messages[state.currentChatId] || []
    }
  },
  
  actions: {
    /**
     * 设置当前对话
     */
    setCurrentChatId(chatId) {
      this.currentChatId = chatId
    },
    
    /**
     * 添加消息
     */
    addMessage(chatId, message) {
      if (!this.messages[chatId]) {
        this.messages[chatId] = []
      }
      this.messages[chatId].push(message)
    },
    
    /**
     * 清空消息
     */
    clearMessages(chatId) {
      if (this.messages[chatId]) {
        this.messages[chatId] = []
      }
    }
  }
})

