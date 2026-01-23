/**
 * Skill 相关 API
 * 
 * 后端路径：/skill (注意是单数)
 */

import request from './request'

/**
 * 获取 Skill 列表
 */
export function getSkillList(params) {
  return request({
    url: '/skill/list',
    method: 'get',
    params
  })
}

/**
 * 执行 Skill
 */
export function executeSkill(data) {
  return request({
    url: '/skill/execute',
    method: 'post',
    data
  })
}

/**
 * 重新加载 Skills
 */
export function reloadSkills() {
  return request({
    url: '/skill/reload',
    method: 'post'
  })
}

/**
 * 搜索 Skills
 */
export function searchSkills(keyword) {
  return request({
    url: '/skill/search',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 上传 Skill（教师端）
 */
export function uploadSkill(formData) {
  return request({
    url: '/skill/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ==================== 废弃的旧接口（兼容性保留） ====================

/**
 * @deprecated 后端路径已改为单数 /skill，请使用上面的新接口
 */
export function getSkillDetail(id) {
  console.warn('getSkillDetail is deprecated, backend does not support this endpoint')
  return Promise.reject(new Error('This API is deprecated'))
}

/**
 * @deprecated 后端路径已改为单数 /skill，请使用上面的新接口
 */
export function deleteSkill(id) {
  console.warn('deleteSkill is deprecated, backend does not support this endpoint')
  return Promise.reject(new Error('This API is deprecated'))
}

