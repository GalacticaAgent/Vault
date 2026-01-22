/**
 * 看板状态模块
 */

import { defineStore } from 'pinia'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    fixedCards: [],
    customCards: []
  }),
  
  actions: {
    /**
     * 设置固定卡片
     */
    setFixedCards(cards) {
      this.fixedCards = cards
    },
    
    /**
     * 设置自定义卡片
     */
    setCustomCards(cards) {
      this.customCards = cards
    },
    
    /**
     * 添加自定义卡片
     */
    addCustomCard(card) {
      this.customCards.push(card)
    },
    
    /**
     * 删除自定义卡片
     */
    removeCustomCard(cardId) {
      const index = this.customCards.findIndex(card => card.id === cardId)
      if (index > -1) {
        this.customCards.splice(index, 1)
      }
    }
  }
})

