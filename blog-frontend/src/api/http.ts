import axios from 'axios'

/** 登录令牌在 localStorage 中的键名（与 stores/auth 共用） */
export const AUTH_TOKEN_KEY = 'personal-blog-auth-token'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截：带上登录令牌
http.interceptors.request.use((config) => {
  const token = localStorage.getItem(AUTH_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：401（未登录 / 令牌过期）时通知全局弹出登录框；
// 登录接口自身的 401（用户名密码错误）除外，由登录弹窗展示错误。
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status: number | undefined = error?.response?.status
    const url: string | undefined = error?.config?.url
    if (status === 401 && !url?.includes('/auth/login')) {
      window.dispatchEvent(new CustomEvent('auth:required'))
    }
    return Promise.reject(error)
  },
)
