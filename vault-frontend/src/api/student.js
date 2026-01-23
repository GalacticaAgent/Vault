/**
 * 学生相关 API
 */

import request from './request'

// ==================== 学生看板 ====================

/**
 * 获取学生看板数据
 */
export function getStudentDashboard() {
  return request({
    url: '/student/dashboard',
    method: 'get'
  })
}

/**
 * 添加自定义卡片
 */
export function addDashboardCard(data) {
  return request({
    url: '/student/dashboard/card',
    method: 'post',
    data
  })
}

/**
 * 删除卡片
 */
export function deleteDashboardCard(id) {
  return request({
    url: `/student/dashboard/card/${id}`,
    method: 'delete'
  })
}

/**
 * 更新卡片
 */
export function updateDashboardCard(id, data) {
  return request({
    url: `/student/dashboard/card/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取卡片列表
 */
export function getDashboardCards() {
  return request({
    url: '/student/dashboard/cards',
    method: 'get'
  })
}

/**
 * 刷新卡片数据
 */
export function refreshDashboardCard(id) {
  return request({
    url: `/student/dashboard/card/${id}/refresh`,
    method: 'post'
  })
}

/**
 * 获取学生肖像
 */
export function getStudentProfile() {
  return request({
    url: '/student/profile',
    method: 'get'
  })
}

