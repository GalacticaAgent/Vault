/**
 * 资料相关 API
 */

import request from './request'

/**
 * 上传资料
 */
export function uploadMaterial(data) {
  return request({
    url: '/materials',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取资料列表
 */
export function getMaterialList(params) {
  return request({
    url: '/materials',
    method: 'get',
    params
  })
}

/**
 * 获取资料详情
 */
export function getMaterialDetail(id) {
  return request({
    url: `/materials/${id}`,
    method: 'get'
  })
}

/**
 * 删除资料
 */
export function deleteMaterial(id) {
  return request({
    url: `/materials/${id}`,
    method: 'delete'
  })
}

