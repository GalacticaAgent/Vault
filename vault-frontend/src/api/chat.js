/**
 * 聊天相关 API
 */

import request from './request'

/**
 * 获取对话列表
 */
export function getChatList(params) {
  return request({
    url: '/chats',
    method: 'get',
    params
  })
}

/**
 * 创建对话
 */
export function createChat(data) {
  return request({
    url: '/chats',
    method: 'post',
    data
  })
}

/**
 * 获取对话详情
 */
export function getChatDetail(id) {
  return request({
    url: `/chats/${id}`,
    method: 'get'
  })
}

/**
 * 发送消息
 */
export function sendMessage(chatId, data) {
  return request({
    url: `/chats/${chatId}/messages`,
    method: 'post',
    data
  })
}

/**
 * 获取消息历史
 */
export function getMessageHistory(chatId, params) {
  return request({
    url: `/chats/${chatId}/messages`,
    method: 'get',
    params
  })
}

/**
 * 删除对话
 */
export function deleteChat(id) {
  return request({
    url: `/chats/${id}`,
    method: 'delete'
  })
}

/**
 * 分享对话
 */
export function shareChat(id) {
  return request({
    url: `/chats/${id}/share`,
    method: 'post'
  })
}

