/**
 * 资料相关 API
 * 
 * 注意：材料接口已拆分
 * - 教师端：/teacher/material (上传、删除等管理功能)
 * - 公共接口：/material (查询、搜索等只读功能，注意是单数)
 */

import request from './request'

// ==================== 教师端接口 ====================

/**
 * 上传课程资料（教师端）
 */
export function uploadMaterial(courseId, formData) {
  const data = new FormData()
  data.append('file', formData.file)
  data.append('courseId', courseId)
  if (formData.name) data.append('name', formData.name)
  if (formData.description) data.append('description', formData.description)
  
  return request({
    url: '/teacher/material/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取课程资料列表（教师端）
 */
export function getCourseMaterials(courseId) {
  return request({
    url: `/teacher/material/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 删除资料（教师端）
 */
export function deleteMaterial(id) {
  return request({
    url: `/teacher/material/${id}`,
    method: 'delete'
  })
}

/**
 * 获取资料使用统计（教师端）
 */
export function getMaterialStatistics(id) {
  return request({
    url: `/teacher/material/${id}/statistics`,
    method: 'get'
  })
}

// ==================== 公共查询接口 ====================

/**
 * 获取资料列表（公共接口，仅查询公开资料）
 */
export function getMaterialList(params) {
  return request({
    url: '/material/list',
    method: 'get',
    params
  })
}

/**
 * 获取资料详情（公共接口）
 */
export function getMaterialDetail(id) {
  return request({
    url: `/material/${id}`,
    method: 'get'
  })
}

/**
 * 搜索资料（公共接口）
 */
export function searchMaterials(keyword) {
  return request({
    url: '/material/search',
    method: 'get',
    params: { keyword }
  })
}

// ==================== 学生端接口 ====================

/**
 * 获取推荐资料（学生端）
 */
export function getRecommendedMaterials() {
  return request({
    url: '/student/material/recommended',
    method: 'get'
  })
}

/**
 * 获取课程资料（学生端）
 */
export function getStudentCourseMaterials(courseId) {
  return request({
    url: `/student/material/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 记录学习记录（学生端）
 */
export function recordLearning(materialId, duration) {
  return request({
    url: `/student/material/${materialId}/record`,
    method: 'post',
    params: { duration }
  })
}

