import { defineStore } from 'pinia'

import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { AUTH_TOKEN_KEY } from '@/api/http'

/** 用户信息在 localStorage 中的键名 */
const AUTH_INFO_KEY = 'personal-blog-auth-info'

interface StoredInfo {
  username?: string
  nickname?: string
}

function readStoredInfo(): StoredInfo {
  try {
    const raw = localStorage.getItem(AUTH_INFO_KEY)
    if (!raw) return {}
    const parsed = JSON.parse(raw) as StoredInfo
    return {
      username: typeof parsed.username === 'string' ? parsed.username : '',
      nickname: typeof parsed.nickname === 'string' ? parsed.nickname : '',
    }
  } catch {
    return {}
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => {
    const info = readStoredInfo()
    return {
      token: localStorage.getItem(AUTH_TOKEN_KEY) ?? '',
      username: info.username ?? '',
      nickname: info.nickname ?? '',
      loginDialogVisible: false,
    }
  },
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
  },
  actions: {
    openLogin() {
      this.loginDialogVisible = true
    },
    closeLogin() {
      this.loginDialogVisible = false
    },
    /**
     * 需要登录的操作统一入口：已登录返回 true；
     * 未登录则弹出登录框并返回 false，调用方直接 return 即可。
     */
    requireLogin(): boolean {
      if (this.isLoggedIn) return true
      this.openLogin()
      return false
    },
    async login(username: string, password: string) {
      const result = await loginApi({ username, password })
      this.token = result.token
      this.username = result.username
      this.nickname = result.nickname
      localStorage.setItem(AUTH_TOKEN_KEY, result.token)
      localStorage.setItem(
        AUTH_INFO_KEY,
        JSON.stringify({ username: result.username, nickname: result.nickname }),
      )
      this.closeLogin()
    },
    async logout() {
      this.token = ''
      this.username = ''
      this.nickname = ''
      localStorage.removeItem(AUTH_TOKEN_KEY)
      localStorage.removeItem(AUTH_INFO_KEY)
      try {
        await logoutApi()
      } catch {
        // 退出接口失败不影响本地清除令牌
      }
    },
  },
})
