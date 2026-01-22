/**
 * 问卷状态模块
 */

import { defineStore } from 'pinia'

export const useQuestionnaireStore = defineStore('questionnaire', {
  state: () => ({
    currentQuestionnaire: null,
    answers: {}
  }),
  
  actions: {
    /**
     * 设置当前问卷
     */
    setCurrentQuestionnaire(questionnaire) {
      this.currentQuestionnaire = questionnaire
    },
    
    /**
     * 设置答案
     */
    setAnswer(questionId, answer) {
      this.answers[questionId] = answer
    },
    
    /**
     * 获取答案
     */
    getAnswer(questionId) {
      return this.answers[questionId] || null
    },
    
    /**
     * 清空答案
     */
    clearAnswers() {
      this.answers = {}
    }
  }
})

