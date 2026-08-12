import { http } from '@/api/http'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  username: string
  nickname: string
}

/** 登录：成功返回签名令牌与用户信息 */
export async function login(payload: LoginRequest) {
  const response = await http.post<LoginResult>('/auth/login', payload)
  return response.data
}

/** 退出：无状态令牌，服务端无需操作，客户端清除本地令牌即可 */
export async function logout() {
  await http.post('/auth/logout')
}

/** 当前登录用户（令牌无效时返回 401） */
export async function getMe() {
  const response = await http.get<{ username: string }>('/auth/me')
  return response.data
}
