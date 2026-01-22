/**
 * 用户状态模块
 */

import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'
import { login as loginApi, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isStudent: (state) => state.userInfo?.role === 'STUDENT',
    isTeacher: (state) => state.userInfo?.role === 'TEACHER',
    isAdmin: (state) => state.userInfo?.role === 'ADMIN'
  },
  
  actions: {
    /**
     * 登录
     */
    async login(loginForm) {
      try {
        const response = await loginApi(loginForm)
        this.token = response.data.token
        this.userInfo = response.data.userInfo
        setToken(this.token)
        setUserInfo(this.userInfo)
        return Promise.resolve(response)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    /**
     * 获取用户信息
     */
    async getUserInfo() {
      try {
        const response = await getCurrentUser()
        this.userInfo = response.data
        setUserInfo(this.userInfo)
        return Promise.resolve(response)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    /**
     * 设置 Token
     */
    setToken(token) {
      this.token = token
      setToken(token)
    },
    
    /**
     * 设置用户信息
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      setUserInfo(userInfo)
    },
    
    /**
     * 更新头像
     */
    updateAvatar(avatarUrl) {
      if (this.userInfo) {
        this.userInfo = { ...this.userInfo, avatar: avatarUrl }
        setUserInfo(this.userInfo)
      }
    },

    /**
     * 登出
     */
    logout() {
      this.token = ''
      this.userInfo = null
      removeToken()
      removeUserInfo()
    }
  }
})

