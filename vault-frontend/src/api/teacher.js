/**
 * 教师相关 API
 */

import request from './request'

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
 * 获取学生分析
 */
export function getStudentAnalysis(studentId) {
  return request({
    url: `/teacher/students/${studentId}/analysis`,
    method: 'get'
  })
}

/**
 * 获取班级统计
 */
export function getClassStatistics(params) {
  return request({
    url: '/teacher/class/statistics',
    method: 'get',
    params
  })
}
<<<<<<< HEAD

=======
>>>>>>> bcffb2a (教师看板)
