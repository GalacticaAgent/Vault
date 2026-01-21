import request from './request'

/**
 * 用户登录
 * @param {Object} data - 登录数据 { username, password }
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 */
export function registerApi(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 测试API连通性
 */
export function testApi() {
  return request({
    url: '/auth/test',
    method: 'get'
  })
}

/**
 * 获取当前用户信息（占位符，后续实现）
 */
export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}
