/**
 * 问卷相关 API
 */

import request from './request'

/**
 * 获取问卷列表
 */
export function getQuestionnaireList(params) {
  return request({
    url: '/questionnaires',
    method: 'get',
    params
  })
}

/**
 * 获取问卷详情
 */
export function getQuestionnaireDetail(id) {
  return request({
    url: `/questionnaires/${id}`,
    method: 'get'
  })
}

/**
 * 提交答案
 */
export function submitAnswers(id, data) {
  return request({
    url: `/questionnaires/${id}/submit`,
    method: 'post',
    data
  })
}

/**
 * 获取成绩
 */
export function getScore(submissionId) {
  return request({
    url: `/questionnaires/submissions/${submissionId}/score`,
    method: 'get'
  })
}

/**
 * 生成问卷（教师端）
 */
export function generateQuestionnaire(data) {
  return request({
    url: '/questionnaires/generate',
    method: 'post',
    data
  })
}

