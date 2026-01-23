/**
 * 教师相关 API
 */

import request from './request'

// ==================== 教师看板 ====================

/**
 * 获取教师看板数据
 */
export function getTeacherDashboard() {
  return request({
    url: '/teacher/dashboard',
    method: 'get'
  })
}

/**
 * 添加自定义查询卡片
 */
export function addDashboardCard(data) {
  return request({
    url: '/teacher/dashboard/card',
    method: 'post',
    data
  })
}

/**
 * 删除自定义查询卡片
 */
export function deleteDashboardCard(id) {
  return request({
    url: `/teacher/dashboard/card/${id}`,
    method: 'delete'
  })
}

/**
 * 更新自定义查询卡片
 */
export function updateDashboardCard(id, data) {
  return request({
    url: `/teacher/dashboard/card/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取自定义卡片列表
 */
export function getDashboardCards() {
  return request({
    url: '/teacher/dashboard/cards',
    method: 'get'
  })
}

/**
 * 刷新卡片数据
 */
export function refreshDashboardCard(id) {
  return request({
    url: `/teacher/dashboard/card/${id}/refresh`,
    method: 'post'
  })
}

/**
 * 获取教师的班级列表
 */
export function getTeacherClasses() {
  return request({
    url: '/teacher/dashboard/classes',
    method: 'get'
  })
}

/**
 * 获取班级学生列表
 */
export function getClassStudents(classId) {
  return request({
    url: `/teacher/dashboard/class/${classId}/students`,
    method: 'get'
  })
}

/**
 * 获取班级统计数据
 */
export function getClassStatistics(classId) {
  return request({
    url: `/teacher/dashboard/class/${classId}/statistics`,
    method: 'get'
  })
}

/**
 * 获取学生详细报告
 */
export function getStudentReport(studentId) {
  return request({
    url: `/teacher/dashboard/student/${studentId}/report`,
    method: 'get'
  })
}

/**
 * 获取知识点掌握情况
 */
export function getKnowledgeStatistics(classId) {
  return request({
    url: `/teacher/dashboard/class/${classId}/knowledge`,
    method: 'get'
  })
}

/**
 * 班级对比
 */
export function compareClasses(classIds) {
  return request({
    url: '/teacher/dashboard/classes/compare',
    method: 'get',
    params: { classIds: classIds.join(',') }
  })
}

// ==================== 废弃的旧接口（兼容性保留） ====================

/**
 * @deprecated 请使用 getStudentReport(studentId)
 */
export function getStudentAnalysis(studentId) {
  return getStudentReport(studentId)
}

