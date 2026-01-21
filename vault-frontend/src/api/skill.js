/**
 * Skill 相关 API
 */

import request from './request'

/**
 * 上传 Skill
 */
export function uploadSkill(data) {
  return request({
    url: '/skills',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取 Skill 列表
 */
export function getSkillList(params) {
  return request({
    url: '/skills',
    method: 'get',
    params
  })
}

/**
 * 获取 Skill 详情
 */
export function getSkillDetail(id) {
  return request({
    url: `/skills/${id}`,
    method: 'get'
  })
}

/**
 * 删除 Skill
 */
export function deleteSkill(id) {
  return request({
    url: `/skills/${id}`,
    method: 'delete'
  })
}

