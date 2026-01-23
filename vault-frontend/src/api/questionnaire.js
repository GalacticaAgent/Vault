/**
 * 问卷相关 API
 * 
 * 注意：问卷接口已按角色拆分
 * - 教师端：/teacher/questionnaires
 * - 学生端：/student/questionnaire
 */

import request from './request'

// ==================== 教师端接口 ====================

/**
 * 获取教师创建的问卷列表
 */
export function getTeacherQuestionnaireList(params) {
  return request({
    url: '/teacher/questionnaires',
    method: 'get',
    params
  })
}

/**
 * 获取问卷详情（教师端）
 */
export function getTeacherQuestionnaireDetail(id) {
  return request({
    url: `/teacher/questionnaires/${id}`,
    method: 'get'
  })
}

/**
 * 创建问卷（教师端）
 */
export function createQuestionnaire(data) {
  return request({
    url: '/teacher/questionnaires',
    method: 'post',
    data
  })
}

/**
 * 更新问卷（教师端）
 */
export function updateQuestionnaire(data) {
  return request({
    url: '/teacher/questionnaires',
    method: 'put',
    data
  })
}

/**
 * 删除问卷（教师端）
 */
export function deleteQuestionnaire(id) {
  return request({
    url: `/teacher/questionnaires/${id}`,
    method: 'delete'
  })
}

/**
 * 发布问卷（教师端）
 */
export function publishQuestionnaire(id) {
  return request({
    url: `/teacher/questionnaires/${id}/publish`,
    method: 'post'
  })
}

/**
 * 关闭问卷（教师端）
 */
export function closeQuestionnaire(id) {
  return request({
    url: `/teacher/questionnaires/${id}/close`,
    method: 'post'
  })
}

/**
 * AI 生成问卷（教师端）
 */
export function generateQuestionnaire(data) {
  return request({
    url: '/teacher/questionnaires/generate',
    method: 'post',
    data
  })
}

// ==================== 学生端接口 ====================

/**
 * 获取待完成的问卷列表（学生端）
 */
export function getPendingQuestionnaires(params) {
  return request({
    url: '/student/questionnaire/pending',
    method: 'get',
    params
  })
}

/**
 * 获取已完成的问卷列表（学生端）
 */
export function getCompletedQuestionnaires(params) {
  return request({
    url: '/student/questionnaire/completed',
    method: 'get',
    params
  })
}

/**
 * 获取问卷详情用于答题（学生端）
 */
export function getQuestionnaireForAnswer(id) {
  return request({
    url: `/student/questionnaire/${id}`,
    method: 'get'
  })
}

/**
 * 提交问卷答案（学生端）
 */
export function submitQuestionnaire(id, data) {
  return request({
    url: `/student/questionnaire/${id}/submit`,
    method: 'post',
    data
  })
}

/**
 * 保存答题进度（学生端）
 */
export function saveQuestionnaireProgress(id, data) {
  return request({
    url: `/student/questionnaire/${id}/progress`,
    method: 'post',
    data
  })
}

/**
 * 获取答题进度（学生端）
 */
export function getQuestionnaireProgress(id) {
  return request({
    url: `/student/questionnaire/${id}/progress`,
    method: 'get'
  })
}

/**
 * 获取问卷成绩（学生端）
 */
export function getQuestionnaireScore(id) {
  return request({
    url: `/student/questionnaire/${id}/score`,
    method: 'get'
  })
}

/**
 * 获取问卷详细报告（学生端）
 */
export function getQuestionnaireReport(id) {
  return request({
    url: `/student/questionnaire/${id}/report`,
    method: 'get'
  })
}

/**
 * 获取错题分析（学生端）
 */
export function getMistakeAnalysis(id) {
  return request({
    url: `/student/questionnaire/${id}/mistakes`,
    method: 'get'
  })
}

