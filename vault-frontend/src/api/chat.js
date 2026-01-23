/**
 * 聊天相关 API
 * 
 * 后端路径：/chat (注意是单数)
 */

import request from './request'

/**
 * 获取对话列表
 */
export function getChatList(params) {
  return request({
    url: '/chat/list',
    method: 'get',
    params
  })
}

/**
 * 发送消息（同步）
 */
export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data
  })
}

/**
 * 获取会话详情
 */
export function getChatDetail(chatId) {
  return request({
    url: `/chat/${chatId}`,
    method: 'get'
  })
}

/**
 * 删除对话
 */
export function deleteChat(chatId) {
  return request({
    url: `/chat/${chatId}`,
    method: 'delete'
  })
}

/**
 * 上传文件
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/chat/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 创建分享
 */
export function shareChat(chatId) {
  return request({
    url: '/chat/share',
    method: 'post',
    data: { chatId }
  })
}

/**
 * 获取分享详情（无需认证）
 */
export function getSharedChat(shareId) {
  return request({
    url: `/chat/share/${shareId}`,
    method: 'get'
  })
}

// ==================== WebSocket 相关 ====================

/**
 * 获取 WebSocket 连接地址
 * 
 * 使用方式：
 * const token = userStore.token
 * const ws = new WebSocket(`ws://localhost:8080/api/chat/ws?token=${token}`)
 */
export function getWebSocketUrl(token) {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = import.meta.env.VITE_WS_BASE_URL || window.location.host.replace(':5173', ':8080')
  return `${protocol}//${host}/api/chat/ws?token=${token}`
}

// ==================== 废弃的旧接口（兼容性保留） ====================

/**
 * @deprecated 后端不支持单独创建对话，发送消息时会自动创建
 */
export function createChat(data) {
  console.warn('createChat is deprecated, chat will be created automatically when sending first message')
  return Promise.reject(new Error('This API is deprecated'))
}

/**
 * @deprecated 后端使用 /chat/send，不是 /chats/{id}/messages
 */
export function getMessageHistory(chatId, params) {
  console.warn('getMessageHistory is deprecated, use getChatDetail instead')
  return getChatDetail(chatId)
}

