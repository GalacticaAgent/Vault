/**
 * 学生相关 API
 */

import request from './request'

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
 * 获取学生肖像
 */
export function getStudentProfile() {
  return request({
    url: '/student/profile',
    method: 'get'
  })
}

